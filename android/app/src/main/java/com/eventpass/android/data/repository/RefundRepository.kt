package com.eventpass.android.data.repository

import com.eventpass.android.domain.models.*
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDateTime

/**
 * Refund repository protocol.
 * Migrated from iOS Data/Repositories/RefundRepository.swift
 *
 * Refund service with eligibility logic, processing, and automation.
 * Designed for fintech-grade reliability and auditability.
 */
interface RefundRepository {
    // Eligibility
    suspend fun checkEligibility(ticket: Ticket, event: Event): RefundEligibilityResult
    suspend fun getRefundPolicy(eventId: String, ticketTypeId: String?): RefundPolicy

    // User actions
    suspend fun requestRefund(ticket: Ticket, reason: RefundReason, note: String?): RefundRequest
    suspend fun cancelRefundRequest(requestId: String)
    suspend fun getRefundRequest(requestId: String): RefundRequest?
    suspend fun getUserRefundRequests(userId: String): List<RefundRequest>
    suspend fun getRefundRequestForTicket(ticketId: String): RefundRequest?

    // Organizer actions
    suspend fun getEventRefundRequests(eventId: String, status: RefundStatus?): List<RefundRequest>
    suspend fun getOrganizerRefundRequests(organizerId: String, status: RefundStatus?): List<RefundRequest>
    suspend fun approveRefund(requestId: String, approvedAmount: Double?, note: String?): RefundRequest
    suspend fun rejectRefund(requestId: String, note: String): RefundRequest
    suspend fun issueManualRefund(ticket: Ticket, amount: Double, reason: RefundReason, note: String?): RefundRequest

    // Processing
    suspend fun processRefund(requestId: String): RefundTransaction
    suspend fun getRefundTransaction(transactionId: String): RefundTransaction?
    suspend fun getRefundTransactions(eventId: String): List<RefundTransaction>

    // Automation
    suspend fun processEventCancellationRefunds(eventId: String): List<RefundRequest>
    suspend fun markTicketsRefundableForReschedule(eventId: String, deadline: LocalDateTime)

    // Analytics
    suspend fun getRefundAnalytics(
        eventId: String?,
        organizerId: String?,
        periodStart: LocalDateTime,
        periodEnd: LocalDateTime
    ): RefundAnalytics

    // Publisher
    val refundStatusFlow: SharedFlow<RefundRequest>
}

// MARK: - Refund Analytics

/**
 * Analytics for refund tracking.
 */
data class RefundAnalytics(
    val eventId: String?,
    val organizerId: String?,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,

    val totalRequests: Int = 0,
    val pendingRequests: Int = 0,
    val approvedRequests: Int = 0,
    val rejectedRequests: Int = 0,
    val completedRefunds: Int = 0,
    val failedRefunds: Int = 0,

    val totalAmountRequested: Double = 0.0,
    val totalAmountRefunded: Double = 0.0,
    val totalProcessingFees: Double = 0.0,

    val averageProcessingTimeHours: Double = 0.0,
    val refundRate: Double = 0.0,

    val topReasons: Map<RefundReason, Int> = emptyMap()
)

// MARK: - Refund Processor Protocol (Payment Integration Abstraction)

/**
 * Interface for payment processing integration.
 */
interface RefundProcessor {
    suspend fun processMobileMoneyRefund(
        phoneNumber: String,
        amount: Double,
        currency: String,
        reference: String,
        provider: RefundPaymentMethod
    ): String  // Returns transaction reference

    suspend fun processCardRefund(
        originalTransactionRef: String,
        amount: Double,
        currency: String
    ): String

    suspend fun processWalletRefund(
        userId: String,
        amount: Double,
        currency: String
    ): String
}

// MARK: - Refund Errors

sealed class RefundError : Exception() {
    data class NotEligible(val reason: String) : RefundError() {
        override val message: String = "Not eligible for refund: $reason"
    }

    object RequestNotFound : RefundError() {
        private fun readResolve(): Any = RequestNotFound
        override val message: String = "Refund request not found"
    }

    object AlreadyRequested : RefundError() {
        private fun readResolve(): Any = AlreadyRequested
        override val message: String = "A refund has already been requested for this ticket"
    }

    object AlreadyProcessed : RefundError() {
        private fun readResolve(): Any = AlreadyProcessed
        override val message: String = "This refund has already been processed"
    }

    object PolicyNotFound : RefundError() {
        private fun readResolve(): Any = PolicyNotFound
        override val message: String = "Refund policy not found"
    }

    data class ProcessingFailed(val reason: String) : RefundError() {
        override val message: String = "Refund processing failed: $reason"
    }

    object InvalidAmount : RefundError() {
        private fun readResolve(): Any = InvalidAmount
        override val message: String = "Invalid refund amount"
    }

    object TicketAlreadyRefunded : RefundError() {
        private fun readResolve(): Any = TicketAlreadyRefunded
        override val message: String = "This ticket has already been refunded"
    }

    object TicketAlreadyUsed : RefundError() {
        private fun readResolve(): Any = TicketAlreadyUsed
        override val message: String = "Used tickets cannot be refunded"
    }

    object RefundDeadlinePassed : RefundError() {
        private fun readResolve(): Any = RefundDeadlinePassed
        override val message: String = "The refund deadline has passed"
    }

    object MaxRefundsExceeded : RefundError() {
        private fun readResolve(): Any = MaxRefundsExceeded
        override val message: String = "Maximum number of refunds exceeded"
    }
}
