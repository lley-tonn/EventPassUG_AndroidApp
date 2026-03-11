package com.eventpass.android.data.repository

import com.eventpass.android.domain.models.*
import kotlinx.coroutines.flow.SharedFlow

/**
 * Cancellation repository protocol.
 * Migrated from iOS Data/Repositories/CancellationRepository.swift
 *
 * Event cancellation service with full refund integration and automation.
 * Designed for fintech-grade reliability and complete audit trail.
 */
interface CancellationRepository {
    // Impact calculation
    suspend fun calculateImpact(eventId: String): CancellationImpact

    // Cancellation lifecycle
    suspend fun createCancellation(
        event: Event,
        reason: CancellationReason,
        note: String?,
        initiatedBy: String
    ): EventCancellation

    suspend fun updateCompensationPlan(
        cancellationId: String,
        plan: CompensationPlan
    ): EventCancellation

    suspend fun confirmCancellation(
        cancellationId: String,
        confirmationCode: String,
        confirmedBy: String
    ): EventCancellation

    suspend fun cancelDraft(cancellationId: String)

    // Processing
    suspend fun processCancellation(cancellationId: String): EventCancellation
    suspend fun retryFailedRefunds(cancellationId: String): EventCancellation

    // Queries
    suspend fun getCancellation(cancellationId: String): EventCancellation?
    suspend fun getCancellationForEvent(eventId: String): EventCancellation?
    suspend fun getOrganizerCancellations(organizerId: String): List<EventCancellation>

    // Notifications
    fun previewNotification(cancellation: EventCancellation): NotificationPreview
    suspend fun sendNotifications(cancellationId: String): CancellationNotificationResult

    // Analytics
    fun trackEvent(event: CancellationAnalyticsEvent, properties: Map<String, Any>)

    // Publishers
    val cancellationStatusFlow: SharedFlow<EventCancellation>
    val processingProgressFlow: SharedFlow<CancellationProgress>
}

// MARK: - Supporting Types

/**
 * Progress of cancellation processing.
 */
data class CancellationProgress(
    val cancellationId: String,
    val phase: ProcessingPhase,
    val currentStep: Int,
    val totalSteps: Int,
    val message: String
) {
    val progress: Double
        get() = if (totalSteps > 0) {
            currentStep.toDouble() / totalSteps.toDouble()
        } else 0.0

    enum class ProcessingPhase(val displayName: String) {
        UPDATING_EVENT("Updating Event"),
        INVALIDATING_TICKETS("Invalidating Tickets"),
        CREATING_REFUNDS("Creating Refunds"),
        PROCESSING_REFUNDS("Processing Refunds"),
        SENDING_NOTIFICATIONS("Sending Notifications"),
        UPDATING_ANALYTICS("Updating Analytics"),
        FINALIZING("Finalizing")
    }
}

// MARK: - Analytics Events

enum class CancellationAnalyticsEvent(val value: String) {
    CANCEL_STARTED("event_cancel_started"),
    CANCEL_CONFIRMED("event_cancel_confirmed"),
    REFUNDS_TRIGGERED("refunds_triggered"),
    ATTENDEES_NOTIFIED("attendees_notified"),
    CANCEL_COMPLETED("event_cancel_completed"),
    CANCEL_FAILED("event_cancel_failed")
}

// MARK: - Errors

sealed class CancellationServiceError : Exception() {
    object EventNotFound : CancellationServiceError() {
        private fun readResolve(): Any = EventNotFound
        override val message: String = "Event not found"
    }

    object EventAlreadyCancelled : CancellationServiceError() {
        private fun readResolve(): Any = EventAlreadyCancelled
        override val message: String = "This event has already been cancelled"
    }

    object CancellationNotFound : CancellationServiceError() {
        private fun readResolve(): Any = CancellationNotFound
        override val message: String = "Cancellation record not found"
    }

    object InvalidConfirmationCode : CancellationServiceError() {
        private fun readResolve(): Any = InvalidConfirmationCode
        override val message: String = "Invalid confirmation code. Please type CONFIRM exactly."
    }

    object CancellationNotReversible : CancellationServiceError() {
        private fun readResolve(): Any = CancellationNotReversible
        override val message: String = "This cancellation can no longer be reversed"
    }

    object ProcessingInProgress : CancellationServiceError() {
        private fun readResolve(): Any = ProcessingInProgress
        override val message: String = "Cancellation is already being processed"
    }

    object RefundServiceUnavailable : CancellationServiceError() {
        private fun readResolve(): Any = RefundServiceUnavailable
        override val message: String = "Refund service is temporarily unavailable"
    }

    object NotificationServiceUnavailable : CancellationServiceError() {
        private fun readResolve(): Any = NotificationServiceUnavailable
        override val message: String = "Notification service is temporarily unavailable"
    }

    object InsufficientPermissions : CancellationServiceError() {
        private fun readResolve(): Any = InsufficientPermissions
        override val message: String = "You don't have permission to perform this action"
    }

    data class InvalidState(val reason: String) : CancellationServiceError() {
        override val message: String = "Invalid state: $reason"
    }
}
