package com.eventpass.android.data.repository

import android.util.Log
import com.eventpass.android.domain.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock refund processor for payment integration.
 */
class MockRefundProcessor @Inject constructor() : RefundProcessor {
    override suspend fun processMobileMoneyRefund(
        phoneNumber: String,
        amount: Double,
        currency: String,
        reference: String,
        provider: RefundPaymentMethod
    ): String {
        // Simulate processing delay
        delay(2000)

        // Simulate occasional failures (5% failure rate)
        if ((1..20).random() == 1) {
            throw RefundError.ProcessingFailed("Mobile money service temporarily unavailable")
        }

        val timestamp = System.currentTimeMillis()
        return "MM-REF-$timestamp-${(1000..9999).random()}"
    }

    override suspend fun processCardRefund(
        originalTransactionRef: String,
        amount: Double,
        currency: String
    ): String {
        delay(1500)
        val timestamp = System.currentTimeMillis()
        return "CARD-REF-$timestamp-${(1000..9999).random()}"
    }

    override suspend fun processWalletRefund(
        userId: String,
        amount: Double,
        currency: String
    ): String {
        delay(500)
        val timestamp = System.currentTimeMillis()
        return "WALLET-REF-$timestamp-${(1000..9999).random()}"
    }
}

/**
 * Mock implementation of RefundRepository.
 * Migrated from iOS MockRefundRepository.
 */
@Singleton
class RefundRepositoryImpl @Inject constructor(
    private val refundProcessor: RefundProcessor
) : RefundRepository {

    private val refundRequests = mutableListOf<RefundRequest>()
    private val refundTransactions = mutableListOf<RefundTransaction>()
    private val refundPolicies = mutableMapOf<String, RefundPolicy>()  // eventId -> policy

    private val _refundStatusFlow = MutableSharedFlow<RefundRequest>()
    override val refundStatusFlow: SharedFlow<RefundRequest> = _refundStatusFlow.asSharedFlow()

    // MARK: - Eligibility

    override suspend fun checkEligibility(ticket: Ticket, event: Event): RefundEligibilityResult {
        delay(300)

        // Check if ticket already used
        if (ticket.scanStatus == TicketScanStatus.SCANNED) {
            return RefundEligibilityResult.notEligible("This ticket has already been used")
        }

        // Check if ticket already refunded
        val existingPending = refundRequests.find {
            it.ticketId == ticket.id && !it.status.isFinal
        }
        if (existingPending != null) {
            return RefundEligibilityResult.notEligible("A refund request is already pending for this ticket")
        }

        val existingCompleted = refundRequests.find {
            it.ticketId == ticket.id && it.status == RefundStatus.COMPLETED
        }
        if (existingCompleted != null) {
            return RefundEligibilityResult.notEligible("This ticket has already been refunded")
        }

        // Check event status
        if (event.status == EventStatus.CANCELLED) {
            val policy = RefundPolicy.defaultPolicy(event.id)
            return RefundEligibilityResult.eligible(
                amount = ticket.ticketType.price,
                percentage = 1.0,
                fee = 0.0,  // No fee for cancelled events
                deadline = null,
                policy = policy
            )
        }

        if (event.status == EventStatus.COMPLETED) {
            return RefundEligibilityResult.notEligible("The event has already ended")
        }

        // Get policy
        val policy = refundPolicies[event.id] ?: RefundPolicy.defaultPolicy(event.id)

        if (!policy.isRefundable) {
            return RefundEligibilityResult.notEligible("This ticket type is non-refundable")
        }

        // Check deadline
        val hoursUntilEvent = ChronoUnit.HOURS.between(LocalDateTime.now(), event.startDate)
        if (hoursUntilEvent < policy.refundDeadlineHours) {
            return RefundEligibilityResult.notEligible(
                "Refund deadline has passed (must be ${policy.refundDeadlineHours}+ hours before event)"
            )
        }

        // Calculate refund amount based on time windows
        var refundPercentage = policy.refundPercentage
        val ticketPrice = ticket.ticketType.price

        val fullDeadline = policy.fullRefundDeadlineHours
        val partialDeadline = policy.partialRefundDeadlineHours
        val partialPercentage = policy.partialRefundPercentage

        if (fullDeadline != null && hoursUntilEvent >= fullDeadline) {
            refundPercentage = 1.0
        } else if (partialDeadline != null && partialPercentage != null && hoursUntilEvent >= partialDeadline) {
            refundPercentage = partialPercentage
        }

        val refundAmount = ticketPrice * refundPercentage
        val processingFee = refundAmount * policy.processingFeePercentage
        val deadline = event.startDate.minusHours(policy.refundDeadlineHours.toLong())

        return RefundEligibilityResult.eligible(
            amount = refundAmount,
            percentage = refundPercentage,
            fee = processingFee,
            deadline = deadline,
            policy = policy
        )
    }

    override suspend fun getRefundPolicy(eventId: String, ticketTypeId: String?): RefundPolicy {
        delay(200)
        return refundPolicies[eventId] ?: RefundPolicy.defaultPolicy(eventId)
    }

    // MARK: - User Actions

    override suspend fun requestRefund(ticket: Ticket, reason: RefundReason, note: String?): RefundRequest {
        delay(500)

        // Check for existing request
        if (refundRequests.any { it.ticketId == ticket.id && !it.status.isFinal }) {
            throw RefundError.AlreadyRequested
        }

        // Check if ticket used
        if (ticket.scanStatus == TicketScanStatus.SCANNED) {
            throw RefundError.TicketAlreadyUsed
        }

        val request = RefundRequest(
            ticketId = ticket.id,
            ticketNumber = ticket.ticketNumber,
            eventId = ticket.eventId,
            eventTitle = ticket.eventTitle,
            userId = ticket.userId,
            userName = "User",  // Would come from user service
            reason = reason,
            userNote = note,
            requestedAmount = ticket.ticketType.price,
            currency = "UGX",
            originalPaymentMethod = RefundPaymentMethod.MTN_MOBILE_MONEY,  // Would come from original payment
            originalPaymentReference = "PAY-${ticket.orderNumber}",
            originalPurchaseDate = ticket.purchaseDate,
            status = if (reason.isAutoApproved) RefundStatus.APPROVED else RefundStatus.PENDING
        )

        refundRequests.add(request)
        _refundStatusFlow.emit(request)

        // Auto-process approved requests
        if (request.status == RefundStatus.APPROVED) {
            try {
                processRefund(request.id)
            } catch (e: Exception) {
                Log.e("RefundRepository", "Auto-process failed: ${e.message}")
            }
        }

        return request
    }

    override suspend fun cancelRefundRequest(requestId: String) {
        delay(300)

        val index = refundRequests.indexOfFirst { it.id == requestId }
        if (index == -1) throw RefundError.RequestNotFound

        if (refundRequests[index].status != RefundStatus.PENDING) {
            throw RefundError.AlreadyProcessed
        }

        val request = refundRequests[index]
        val updated = request.copy(
            status = RefundStatus.REJECTED,
            statusHistory = request.statusHistory + RefundStatusChange(
                fromStatus = RefundStatus.PENDING,
                toStatus = RefundStatus.REJECTED,
                note = "Cancelled by user"
            )
        )
        refundRequests[index] = updated
    }

    override suspend fun getRefundRequest(requestId: String): RefundRequest? {
        delay(200)
        return refundRequests.find { it.id == requestId }
    }

    override suspend fun getUserRefundRequests(userId: String): List<RefundRequest> {
        delay(300)
        return refundRequests
            .filter { it.userId == userId }
            .sortedByDescending { it.requestedAt }
    }

    override suspend fun getRefundRequestForTicket(ticketId: String): RefundRequest? {
        delay(200)
        return refundRequests.find { it.ticketId == ticketId }
    }

    // MARK: - Organizer Actions

    override suspend fun getEventRefundRequests(eventId: String, status: RefundStatus?): List<RefundRequest> {
        delay(300)

        var requests = refundRequests.filter { it.eventId == eventId }

        if (status != null) {
            requests = requests.filter { it.status == status }
        }

        return requests.sortedByDescending { it.requestedAt }
    }

    override suspend fun getOrganizerRefundRequests(organizerId: String, status: RefundStatus?): List<RefundRequest> {
        delay(400)

        // In real implementation, would filter by organizer's events
        var requests = refundRequests.toList()

        if (status != null) {
            requests = requests.filter { it.status == status }
        }

        return requests.sortedByDescending { it.requestedAt }
    }

    override suspend fun approveRefund(requestId: String, approvedAmount: Double?, note: String?): RefundRequest {
        delay(500)

        val index = refundRequests.indexOfFirst { it.id == requestId }
        if (index == -1) throw RefundError.RequestNotFound

        val request = refundRequests[index]
        if (request.status != RefundStatus.PENDING) {
            throw RefundError.AlreadyProcessed
        }

        // Validate amount if provided
        if (approvedAmount != null && (approvedAmount <= 0 || approvedAmount > request.requestedAmount)) {
            throw RefundError.InvalidAmount
        }

        val updated = request.copy(
            status = RefundStatus.APPROVED,
            approvedAmount = approvedAmount,
            reviewedAt = LocalDateTime.now(),
            reviewerNote = note,
            statusHistory = request.statusHistory + RefundStatusChange(
                fromStatus = request.status,
                toStatus = RefundStatus.APPROVED,
                note = note ?: "Approved by organizer"
            )
        )

        refundRequests[index] = updated
        _refundStatusFlow.emit(updated)

        // Trigger processing
        try {
            processRefund(requestId)
        } catch (e: Exception) {
            Log.e("RefundRepository", "Process refund failed: ${e.message}")
        }

        return updated
    }

    override suspend fun rejectRefund(requestId: String, note: String): RefundRequest {
        delay(400)

        val index = refundRequests.indexOfFirst { it.id == requestId }
        if (index == -1) throw RefundError.RequestNotFound

        val request = refundRequests[index]
        if (request.status != RefundStatus.PENDING) {
            throw RefundError.AlreadyProcessed
        }

        val updated = request.copy(
            status = RefundStatus.REJECTED,
            reviewedAt = LocalDateTime.now(),
            reviewerNote = note,
            statusHistory = request.statusHistory + RefundStatusChange(
                fromStatus = request.status,
                toStatus = RefundStatus.REJECTED,
                note = note
            )
        )

        refundRequests[index] = updated
        _refundStatusFlow.emit(updated)

        return updated
    }

    override suspend fun issueManualRefund(
        ticket: Ticket,
        amount: Double,
        reason: RefundReason,
        note: String?
    ): RefundRequest {
        delay(500)

        if (amount <= 0 || amount > ticket.ticketType.price) {
            throw RefundError.InvalidAmount
        }

        val request = RefundRequest(
            ticketId = ticket.id,
            ticketNumber = ticket.ticketNumber,
            eventId = ticket.eventId,
            eventTitle = ticket.eventTitle,
            userId = ticket.userId,
            userName = "User",
            reason = reason,
            userNote = note,
            requestedAmount = amount,
            approvedAmount = amount,
            currency = "UGX",
            originalPaymentMethod = RefundPaymentMethod.MTN_MOBILE_MONEY,
            originalPaymentReference = "PAY-${ticket.orderNumber}",
            originalPurchaseDate = ticket.purchaseDate,
            status = RefundStatus.APPROVED,
            reviewedAt = LocalDateTime.now(),
            reviewerNote = "Manual refund issued by organizer"
        )

        refundRequests.add(request)
        _refundStatusFlow.emit(request)

        // Process immediately
        try {
            processRefund(request.id)
        } catch (e: Exception) {
            Log.e("RefundRepository", "Manual refund process failed: ${e.message}")
        }

        return request
    }

    // MARK: - Processing

    override suspend fun processRefund(requestId: String): RefundTransaction {
        val index = refundRequests.indexOfFirst { it.id == requestId }
        if (index == -1) throw RefundError.RequestNotFound

        val request = refundRequests[index]
        if (request.status != RefundStatus.APPROVED) {
            throw RefundError.NotEligible("Refund not approved")
        }

        // Update status to processing
        val processingRequest = request.copy(
            status = RefundStatus.PROCESSING,
            statusHistory = request.statusHistory + RefundStatusChange(
                fromStatus = RefundStatus.APPROVED,
                toStatus = RefundStatus.PROCESSING,
                note = "Processing refund"
            )
        )
        refundRequests[index] = processingRequest

        val processingFee = request.requestedAmount * 0.05
        val refundAmount = request.approvedAmount ?: request.requestedAmount

        // Create transaction
        var transaction = RefundTransaction(
            refundRequestId = request.id,
            ticketId = request.ticketId,
            eventId = request.eventId,
            userId = request.userId,
            organizerId = UUID.randomUUID().toString(),  // Would come from event
            originalAmount = request.requestedAmount,
            refundAmount = refundAmount,
            processingFee = processingFee,
            currency = request.currency,
            paymentMethod = request.originalPaymentMethod,
            paymentReference = request.originalPaymentReference,
            status = RefundStatus.PROCESSING,
            reason = request.reason
        )

        // Process through payment provider
        try {
            val transactionRef = when (request.originalPaymentMethod) {
                RefundPaymentMethod.MTN_MOBILE_MONEY, RefundPaymentMethod.AIRTEL_MONEY -> {
                    refundProcessor.processMobileMoneyRefund(
                        phoneNumber = request.userPhone ?: "",
                        amount = refundAmount - processingFee,
                        currency = request.currency,
                        reference = request.originalPaymentReference,
                        provider = request.originalPaymentMethod
                    )
                }
                RefundPaymentMethod.CARD -> {
                    refundProcessor.processCardRefund(
                        originalTransactionRef = request.originalPaymentReference,
                        amount = refundAmount - processingFee,
                        currency = request.currency
                    )
                }
                RefundPaymentMethod.WALLET, RefundPaymentMethod.BANK_TRANSFER -> {
                    refundProcessor.processWalletRefund(
                        userId = request.userId,
                        amount = refundAmount - processingFee,
                        currency = request.currency
                    )
                }
            }

            // Update transaction as completed
            transaction = transaction.copy(
                status = RefundStatus.COMPLETED,
                transactionReference = transactionRef,
                processedAt = LocalDateTime.now(),
                completedAt = LocalDateTime.now()
            )
            refundTransactions.add(transaction)

            // Update request
            val completedRequest = processingRequest.copy(
                status = RefundStatus.COMPLETED,
                processedAt = LocalDateTime.now(),
                completedAt = LocalDateTime.now(),
                statusHistory = processingRequest.statusHistory + RefundStatusChange(
                    fromStatus = RefundStatus.PROCESSING,
                    toStatus = RefundStatus.COMPLETED,
                    note = "Refund completed. Reference: $transactionRef"
                )
            )
            refundRequests[index] = completedRequest
            _refundStatusFlow.emit(completedRequest)

            return transaction

        } catch (e: Exception) {
            // Handle failure
            transaction = transaction.copy(
                status = RefundStatus.FAILED,
                failedAt = LocalDateTime.now(),
                failureReason = e.message
            )
            refundTransactions.add(transaction)

            val failedRequest = processingRequest.copy(
                status = RefundStatus.FAILED,
                failureReason = e.message,
                statusHistory = processingRequest.statusHistory + RefundStatusChange(
                    fromStatus = RefundStatus.PROCESSING,
                    toStatus = RefundStatus.FAILED,
                    note = "Refund failed: ${e.message}"
                )
            )
            refundRequests[index] = failedRequest
            _refundStatusFlow.emit(failedRequest)

            throw RefundError.ProcessingFailed(e.message ?: "Unknown error")
        }
    }

    override suspend fun getRefundTransaction(transactionId: String): RefundTransaction? {
        delay(200)
        return refundTransactions.find { it.id == transactionId }
    }

    override suspend fun getRefundTransactions(eventId: String): List<RefundTransaction> {
        delay(300)
        return refundTransactions
            .filter { it.eventId == eventId }
            .sortedByDescending { it.initiatedAt }
    }

    // MARK: - Automation

    override suspend fun processEventCancellationRefunds(eventId: String): List<RefundRequest> {
        delay(500)

        val createdRequests = mutableListOf<RefundRequest>()

        // Auto-approve pending requests for this event
        refundRequests.forEachIndexed { index, request ->
            if (request.eventId == eventId && request.status == RefundStatus.PENDING) {
                val updated = request.copy(
                    status = RefundStatus.APPROVED,
                    reviewerNote = "Auto-approved due to event cancellation",
                    statusHistory = request.statusHistory + RefundStatusChange(
                        fromStatus = RefundStatus.PENDING,
                        toStatus = RefundStatus.APPROVED,
                        note = "Auto-approved: Event cancelled"
                    )
                )
                refundRequests[index] = updated
                createdRequests.add(updated)
            }
        }

        return createdRequests
    }

    override suspend fun markTicketsRefundableForReschedule(eventId: String, deadline: LocalDateTime) {
        delay(300)

        val hoursUntilDeadline = ChronoUnit.HOURS.between(LocalDateTime.now(), deadline).toInt()

        val policy = RefundPolicy(
            eventId = eventId,
            isRefundable = true,
            refundDeadlineHours = maxOf(0, hoursUntilDeadline),
            refundPercentage = 1.0,
            processingFeePercentage = 0.0,
            allowRescheduledEventRefund = true,
            policyText = "Full refunds available due to event reschedule. Deadline: $deadline"
        )

        refundPolicies[eventId] = policy
    }

    // MARK: - Analytics

    override suspend fun getRefundAnalytics(
        eventId: String?,
        organizerId: String?,
        periodStart: LocalDateTime,
        periodEnd: LocalDateTime
    ): RefundAnalytics {
        delay(400)

        var filteredRequests = refundRequests.filter {
            it.requestedAt >= periodStart && it.requestedAt <= periodEnd
        }

        if (eventId != null) {
            filteredRequests = filteredRequests.filter { it.eventId == eventId }
        }

        val pendingCount = filteredRequests.count { it.status == RefundStatus.PENDING }
        val approvedCount = filteredRequests.count { it.status == RefundStatus.APPROVED }
        val rejectedCount = filteredRequests.count { it.status == RefundStatus.REJECTED }
        val completedCount = filteredRequests.count { it.status == RefundStatus.COMPLETED }
        val failedCount = filteredRequests.count { it.status == RefundStatus.FAILED }

        val totalRequested = filteredRequests.sumOf { it.requestedAmount }
        val totalRefunded = filteredRequests
            .filter { it.status == RefundStatus.COMPLETED }
            .sumOf { it.approvedAmount ?: it.requestedAmount }

        // Calculate top reasons
        val reasonCounts = filteredRequests
            .groupBy { it.reason }
            .mapValues { it.value.size }

        return RefundAnalytics(
            eventId = eventId,
            organizerId = organizerId,
            periodStart = periodStart,
            periodEnd = periodEnd,
            totalRequests = filteredRequests.size,
            pendingRequests = pendingCount,
            approvedRequests = approvedCount,
            rejectedRequests = rejectedCount,
            completedRefunds = completedCount,
            failedRefunds = failedCount,
            totalAmountRequested = totalRequested,
            totalAmountRefunded = totalRefunded,
            totalProcessingFees = totalRefunded * 0.05,
            averageProcessingTimeHours = 4.5,  // Mock average
            refundRate = 0.03,  // 3% refund rate mock
            topReasons = reasonCounts
        )
    }
}
