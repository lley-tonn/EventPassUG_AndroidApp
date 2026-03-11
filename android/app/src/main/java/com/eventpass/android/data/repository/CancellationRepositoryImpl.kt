package com.eventpass.android.data.repository

import android.util.Log
import com.eventpass.android.domain.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of CancellationRepository.
 * Migrated from iOS MockCancellationRepository.
 */
@Singleton
class CancellationRepositoryImpl @Inject constructor(
    private val refundRepository: RefundRepository
) : CancellationRepository {

    private val cancellations = mutableMapOf<String, EventCancellation>()
    private val eventCancellationMap = mutableMapOf<String, String>()  // eventId -> cancellationId

    private val _cancellationStatusFlow = MutableSharedFlow<EventCancellation>()
    override val cancellationStatusFlow: SharedFlow<EventCancellation> = _cancellationStatusFlow.asSharedFlow()

    private val _processingProgressFlow = MutableSharedFlow<CancellationProgress>()
    override val processingProgressFlow: SharedFlow<CancellationProgress> = _processingProgressFlow.asSharedFlow()

    // MARK: - Impact Calculation

    override suspend fun calculateImpact(eventId: String): CancellationImpact {
        // Simulate API delay
        delay(500)

        // Generate realistic mock impact data
        val ticketsSold = (50..500).random()
        val vipTickets = (ticketsSold * 0.15).toInt()
        val regularTickets = ticketsSold - vipTickets
        val vipPrice = 200_000.0
        val regularPrice = 80_000.0

        val grossRevenue = vipTickets * vipPrice + regularTickets * regularPrice
        val processingFees = grossRevenue * 0.01

        return CancellationImpact(
            eventId = eventId,
            ticketsSold = ticketsSold,
            attendeesCount = ticketsSold - (0..10).random(),
            vipTickets = vipTickets,
            regularTickets = regularTickets,
            checkInsCompleted = 0,
            pendingPayments = (0..5).random(),
            transferredTickets = (0..8).random(),
            partiallyRefundedTickets = (0..3).random(),
            grossRevenue = grossRevenue,
            refundTotal = grossRevenue,
            platformFeesRetained = 0.0,  // Waived on cancellation
            processingFeesEstimate = processingFees,
            netRefundAmount = grossRevenue - processingFees,
            organizerPayoutAdjustment = -grossRevenue,
            ticketTypeBreakdown = listOf(
                TicketTypeImpact(
                    name = "VIP",
                    ticketsSold = vipTickets,
                    revenue = vipTickets * vipPrice,
                    refundAmount = vipTickets * vipPrice
                ),
                TicketTypeImpact(
                    name = "Regular",
                    ticketsSold = regularTickets,
                    revenue = regularTickets * regularPrice,
                    refundAmount = regularTickets * regularPrice
                )
            ),
            paymentMethodBreakdown = listOf(
                PaymentMethodImpact(
                    paymentMethod = RefundPaymentMethod.MTN_MOBILE_MONEY,
                    ticketCount = (ticketsSold * 0.6).toInt(),
                    refundAmount = grossRevenue * 0.6,
                    estimatedProcessingTime = "1-24 hours"
                ),
                PaymentMethodImpact(
                    paymentMethod = RefundPaymentMethod.AIRTEL_MONEY,
                    ticketCount = (ticketsSold * 0.25).toInt(),
                    refundAmount = grossRevenue * 0.25,
                    estimatedProcessingTime = "1-24 hours"
                ),
                PaymentMethodImpact(
                    paymentMethod = RefundPaymentMethod.CARD,
                    ticketCount = (ticketsSold * 0.15).toInt(),
                    refundAmount = grossRevenue * 0.15,
                    estimatedProcessingTime = "3-5 business days"
                )
            )
        )
    }

    // MARK: - Cancellation Lifecycle

    override suspend fun createCancellation(
        event: Event,
        reason: CancellationReason,
        note: String?,
        initiatedBy: String
    ): EventCancellation {
        // Check if already cancelled
        if (eventCancellationMap.containsKey(event.id)) {
            throw CancellationServiceError.EventAlreadyCancelled
        }

        // Calculate impact
        val impact = calculateImpact(event.id)

        // Create default compensation plan
        val compensationPlan = CompensationPlan(
            eventId = event.id,
            compensationType = if (reason.warrantsFullRefund) CompensationType.FULL_REFUND else CompensationType.PARTIAL_REFUND,
            refundPercentage = 1.0,
            totalRefundAmount = impact.refundTotal,
            notificationTemplate = NotificationTemplate.defaultCancellation
        )

        // Create cancellation record
        val cancellation = EventCancellation(
            eventId = event.id,
            eventTitle = event.title,
            organizerId = event.organizerId,
            reason = reason,
            reasonNote = note,
            status = CancellationStatus.DRAFT,
            impact = impact,
            compensationPlan = compensationPlan,
            initiatedBy = initiatedBy
        )

        // Store
        cancellations[cancellation.id] = cancellation
        eventCancellationMap[event.id] = cancellation.id

        // Track analytics
        trackEvent(CancellationAnalyticsEvent.CANCEL_STARTED, mapOf(
            "event_id" to event.id,
            "reason" to reason.value,
            "tickets_sold" to impact.ticketsSold,
            "refund_total" to impact.refundTotal
        ))

        return cancellation
    }

    override suspend fun updateCompensationPlan(
        cancellationId: String,
        plan: CompensationPlan
    ): EventCancellation {
        val cancellation = cancellations[cancellationId]
            ?: throw CancellationServiceError.CancellationNotFound

        if (!cancellation.isReversible) {
            throw CancellationServiceError.CancellationNotReversible
        }

        // Create new cancellation with updated plan
        val updated = cancellation.copy(compensationPlan = plan)
        cancellations[cancellationId] = updated
        return updated
    }

    override suspend fun confirmCancellation(
        cancellationId: String,
        confirmationCode: String,
        confirmedBy: String
    ): EventCancellation {
        val cancellation = cancellations[cancellationId]
            ?: throw CancellationServiceError.CancellationNotFound

        // Validate confirmation code
        if (confirmationCode.uppercase() != "CONFIRM") {
            throw CancellationServiceError.InvalidConfirmationCode
        }

        if (!cancellation.isReversible) {
            throw CancellationServiceError.CancellationNotReversible
        }

        // Update status to confirming
        val updated = cancellation.copy(
            status = CancellationStatus.CONFIRMING,
            confirmedAt = LocalDateTime.now(),
            confirmedBy = confirmedBy,
            confirmationCode = confirmationCode
        )

        cancellations[cancellationId] = updated
        _cancellationStatusFlow.emit(updated)

        // Track analytics
        trackEvent(CancellationAnalyticsEvent.CANCEL_CONFIRMED, mapOf(
            "cancellation_id" to cancellationId,
            "event_id" to cancellation.eventId
        ))

        return updated
    }

    override suspend fun cancelDraft(cancellationId: String) {
        val cancellation = cancellations[cancellationId]
            ?: throw CancellationServiceError.CancellationNotFound

        if (!cancellation.isReversible) {
            throw CancellationServiceError.CancellationNotReversible
        }

        // Remove from storage
        cancellations.remove(cancellationId)
        eventCancellationMap.remove(cancellation.eventId)
    }

    // MARK: - Processing

    override suspend fun processCancellation(cancellationId: String): EventCancellation {
        var cancellation = cancellations[cancellationId]
            ?: throw CancellationServiceError.CancellationNotFound

        if (cancellation.status != CancellationStatus.CONFIRMING) {
            throw CancellationServiceError.InvalidState("Cancellation must be confirmed before processing")
        }

        // Update to processing
        cancellation = cancellation.copy(
            status = CancellationStatus.PROCESSING,
            processingStartedAt = LocalDateTime.now()
        )
        cancellations[cancellationId] = cancellation
        _cancellationStatusFlow.emit(cancellation)

        val totalSteps = cancellation.impact.ticketsSold + 3  // tickets + update event + notifications + finalize

        // Phase 1: Update event status
        publishProgress(cancellationId, CancellationProgress.ProcessingPhase.UPDATING_EVENT, 1, totalSteps, "Marking event as cancelled...")
        delay(300)

        // Phase 2: Invalidate tickets
        publishProgress(cancellationId, CancellationProgress.ProcessingPhase.INVALIDATING_TICKETS, 2, totalSteps, "Invalidating tickets and QR codes...")
        delay(500)

        // Phase 3: Create refund requests
        publishProgress(cancellationId, CancellationProgress.ProcessingPhase.CREATING_REFUNDS, 3, totalSteps, "Creating refund requests...")

        var refundRequestsCreated = 0
        var refundsProcessed = 0
        var refundsFailed = 0
        val processingErrors = mutableListOf<CancellationProcessingError>()

        // Simulate creating refunds for each ticket
        for (i in 0 until cancellation.impact.ticketsSold) {
            delay(50)  // 50ms per ticket

            val step = 3 + i
            publishProgress(
                cancellationId,
                CancellationProgress.ProcessingPhase.PROCESSING_REFUNDS,
                step,
                totalSteps,
                "Processing refund ${i + 1} of ${cancellation.impact.ticketsSold}..."
            )

            // Simulate occasional failures (2% failure rate)
            if ((1..100).random() <= 2) {
                refundsFailed++
                processingErrors.add(CancellationProcessingError(
                    ticketId = UUID.randomUUID().toString(),
                    errorType = CancellationProcessingError.ErrorType.REFUND_FAILED,
                    message = "Payment provider timeout"
                ))
            } else {
                refundRequestsCreated++
                refundsProcessed++
            }
        }

        // Phase 4: Send notifications
        publishProgress(cancellationId, CancellationProgress.ProcessingPhase.SENDING_NOTIFICATIONS, totalSteps - 1, totalSteps, "Sending notifications to attendees...")
        delay(500)

        val notificationsSent = cancellation.impact.attendeesCount
        val notificationsFailed = 0

        // Track analytics
        trackEvent(CancellationAnalyticsEvent.REFUNDS_TRIGGERED, mapOf(
            "cancellation_id" to cancellationId,
            "refunds_created" to refundRequestsCreated
        ))

        trackEvent(CancellationAnalyticsEvent.ATTENDEES_NOTIFIED, mapOf(
            "cancellation_id" to cancellationId,
            "notifications_sent" to notificationsSent
        ))

        // Phase 5: Finalize
        publishProgress(cancellationId, CancellationProgress.ProcessingPhase.FINALIZING, totalSteps, totalSteps, "Finalizing cancellation...")
        delay(200)

        // Update final status
        val finalStatus = CancellationStatus.COMPLETED  // Still completed but with errors
        cancellation = cancellation.copy(
            status = finalStatus,
            completedAt = LocalDateTime.now(),
            refundRequestsCreated = refundRequestsCreated,
            refundsProcessed = refundsProcessed,
            refundsFailed = refundsFailed,
            notificationsSent = notificationsSent,
            notificationsFailed = notificationsFailed,
            processingErrors = processingErrors
        )

        cancellations[cancellationId] = cancellation
        _cancellationStatusFlow.emit(cancellation)

        // Track completion
        trackEvent(CancellationAnalyticsEvent.CANCEL_COMPLETED, mapOf(
            "cancellation_id" to cancellationId,
            "refunds_processed" to refundsProcessed,
            "refunds_failed" to refundsFailed
        ))

        return cancellation
    }

    override suspend fun retryFailedRefunds(cancellationId: String): EventCancellation {
        var cancellation = cancellations[cancellationId]
            ?: throw CancellationServiceError.CancellationNotFound

        if (cancellation.status != CancellationStatus.COMPLETED || cancellation.refundsFailed <= 0) {
            throw CancellationServiceError.InvalidState("No failed refunds to retry")
        }

        // Simulate retrying failed refunds
        delay(1000)

        val retriedCount = cancellation.refundsFailed
        val newlyProcessed = (retriedCount * 0.8).toInt()  // 80% success on retry
        val stillFailed = retriedCount - newlyProcessed

        val updatedErrors = cancellation.processingErrors.mapIndexed { index, error ->
            if (index < newlyProcessed) {
                error.copy(
                    resolved = true,
                    resolvedAt = LocalDateTime.now(),
                    resolution = "Retry successful"
                )
            } else {
                error
            }
        }

        cancellation = cancellation.copy(
            refundsProcessed = cancellation.refundsProcessed + newlyProcessed,
            refundsFailed = stillFailed,
            processingErrors = updatedErrors
        )

        cancellations[cancellationId] = cancellation
        return cancellation
    }

    // MARK: - Queries

    override suspend fun getCancellation(cancellationId: String): EventCancellation? {
        return cancellations[cancellationId]
    }

    override suspend fun getCancellationForEvent(eventId: String): EventCancellation? {
        val cancellationId = eventCancellationMap[eventId] ?: return null
        return cancellations[cancellationId]
    }

    override suspend fun getOrganizerCancellations(organizerId: String): List<EventCancellation> {
        return cancellations.values
            .filter { it.organizerId == organizerId }
            .sortedByDescending { it.createdAt }
    }

    // MARK: - Notifications

    override fun previewNotification(cancellation: EventCancellation): NotificationPreview {
        val template = cancellation.compensationPlan.notificationTemplate ?: NotificationTemplate.defaultCancellation

        // Replace placeholders
        var body = template.body
            .replace("{{event_name}}", cancellation.eventTitle)
            .replace("{{event_date}}", "the scheduled date")
            .replace("{{refund_amount}}", cancellation.impact.formattedRefundTotal)
            .replace("{{refund_method}}", "original payment method")
            .replace("{{refund_timeline}}", "1-5 business days")

        // Handle conditional sections
        if (template.includeRefundDetails) {
            body = body.replace("{{#refund_details}}", "")
            body = body.replace("{{/refund_details}}", "")
        } else {
            // Remove refund details section
            val startIndex = body.indexOf("{{#refund_details}}")
            val endIndex = body.indexOf("{{/refund_details}}")
            if (startIndex >= 0 && endIndex >= 0) {
                body = body.removeRange(startIndex, endIndex + "{{/refund_details}}".length)
            }
        }

        return NotificationPreview(
            subject = template.subject.replace("{{event_name}}", cancellation.eventTitle),
            body = body,
            recipientCount = cancellation.impact.attendeesCount,
            estimatedDeliveryTime = "1-2 minutes"
        )
    }

    override suspend fun sendNotifications(cancellationId: String): CancellationNotificationResult {
        val cancellation = cancellations[cancellationId]
            ?: throw CancellationServiceError.CancellationNotFound

        // Simulate sending notifications
        delay(1000)

        val sent = cancellation.impact.attendeesCount
        val failed = (0..2).random()

        return CancellationNotificationResult(
            totalRecipients = sent,
            successfulSends = sent - failed,
            failedSends = failed,
            failedRecipients = if (failed > 0) listOf("invalid@example.com") else emptyList()
        )
    }

    // MARK: - Analytics

    override fun trackEvent(event: CancellationAnalyticsEvent, properties: Map<String, Any>) {
        Log.d("CancellationAnalytics", "${event.value}: $properties")
    }

    // MARK: - Helpers

    private suspend fun publishProgress(
        cancellationId: String,
        phase: CancellationProgress.ProcessingPhase,
        step: Int,
        total: Int,
        message: String
    ) {
        _processingProgressFlow.emit(CancellationProgress(
            cancellationId = cancellationId,
            phase = phase,
            currentStep = step,
            totalSteps = total,
            message = message
        ))
    }
}
