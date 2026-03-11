package com.eventpass.android.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Refund system models.
 * Migrated from iOS Domain/Models/RefundModels.swift
 *
 * Comprehensive refund system for fintech-grade reliability.
 * Supports all Uganda ticketing refund scenarios including mobile money.
 */

/**
 * Refund status - tracks the lifecycle of a refund request.
 */
enum class RefundStatus(val value: String) {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    PROCESSING("processing"),
    COMPLETED("completed"),
    FAILED("failed");

    val displayName: String
        get() = when (this) {
            PENDING -> "Pending Review"
            APPROVED -> "Approved"
            REJECTED -> "Rejected"
            PROCESSING -> "Processing"
            COMPLETED -> "Completed"
            FAILED -> "Failed"
        }

    val iconName: String
        get() = when (this) {
            PENDING -> "schedule"
            APPROVED -> "check_circle"
            REJECTED -> "cancel"
            PROCESSING -> "sync"
            COMPLETED -> "verified"
            FAILED -> "error"
        }

    val colorHex: Long
        get() = when (this) {
            PENDING -> 0xFFFF9800 // Orange
            APPROVED -> 0xFF2196F3 // Blue
            REJECTED -> 0xFFF44336 // Red
            PROCESSING -> 0xFF9C27B0 // Purple
            COMPLETED -> 0xFF4CAF50 // Green
            FAILED -> 0xFFF44336 // Red
        }

    val isFinal: Boolean
        get() = this in listOf(COMPLETED, REJECTED, FAILED)
}

/**
 * Refund reason - categorizes why a refund is being requested.
 */
enum class RefundReason(val value: String) {
    EVENT_CANCELLED("event_cancelled"),
    EVENT_RESCHEDULED("event_rescheduled"),
    CANNOT_ATTEND("cannot_attend"),
    DUPLICATE_PURCHASE("duplicate_purchase"),
    ORGANIZER_DECISION("organizer_decision"),
    FRAUDULENT("fraudulent"),
    TICKET_DOWNGRADE("ticket_downgrade"),
    OTHER("other");

    val displayName: String
        get() = when (this) {
            EVENT_CANCELLED -> "Event Cancelled"
            EVENT_RESCHEDULED -> "Event Rescheduled"
            CANNOT_ATTEND -> "Cannot Attend"
            DUPLICATE_PURCHASE -> "Duplicate Purchase"
            ORGANIZER_DECISION -> "Organizer Decision"
            FRAUDULENT -> "Fraudulent/Invalid Ticket"
            TICKET_DOWNGRADE -> "Ticket Downgrade"
            OTHER -> "Other"
        }

    val description: String
        get() = when (this) {
            EVENT_CANCELLED -> "The event has been cancelled by the organizer"
            EVENT_RESCHEDULED -> "The event date has been changed and I cannot attend"
            CANNOT_ATTEND -> "I am unable to attend the event"
            DUPLICATE_PURCHASE -> "I accidentally purchased multiple tickets"
            ORGANIZER_DECISION -> "Refund issued by the event organizer"
            FRAUDULENT -> "The ticket is invalid or fraudulent"
            TICKET_DOWNGRADE -> "Downgrading to a lower ticket tier"
            OTHER -> "Other reason"
        }

    val iconName: String
        get() = when (this) {
            EVENT_CANCELLED -> "event_busy"
            EVENT_RESCHEDULED -> "event"
            CANNOT_ATTEND -> "person_off"
            DUPLICATE_PURCHASE -> "content_copy"
            ORGANIZER_DECISION -> "admin_panel_settings"
            FRAUDULENT -> "gpp_bad"
            TICKET_DOWNGRADE -> "arrow_downward"
            OTHER -> "help"
        }

    /**
     * Reasons that trigger automatic approval.
     */
    val isAutoApproved: Boolean
        get() = this in listOf(EVENT_CANCELLED, DUPLICATE_PURCHASE, FRAUDULENT)

    companion object {
        /**
         * Reasons available for user selection.
         */
        val userSelectableReasons: List<RefundReason>
            get() = listOf(CANNOT_ATTEND, DUPLICATE_PURCHASE, EVENT_RESCHEDULED, OTHER)
    }
}

/**
 * Ticket refund state - tracks refund eligibility and state for a ticket.
 */
enum class TicketRefundState(val value: String) {
    NONE("none"),
    ELIGIBLE("eligible"),
    REQUESTED("requested"),
    APPROVED("approved"),
    REJECTED("rejected"),
    PROCESSING("processing"),
    REFUNDED("refunded");

    val displayName: String
        get() = when (this) {
            NONE -> "Not Refundable"
            ELIGIBLE -> "Eligible for Refund"
            REQUESTED -> "Refund Requested"
            APPROVED -> "Refund Approved"
            REJECTED -> "Refund Rejected"
            PROCESSING -> "Processing Refund"
            REFUNDED -> "Refunded"
        }

    val canRequestRefund: Boolean
        get() = this == ELIGIBLE

    val isRefundActive: Boolean
        get() = this in listOf(REQUESTED, APPROVED, PROCESSING)
}

/**
 * Payment method for refunds.
 */
enum class RefundPaymentMethod(val value: String) {
    MTN_MOBILE_MONEY("mtn_mobile_money"),
    AIRTEL_MONEY("airtel_money"),
    CARD("card"),
    BANK_TRANSFER("bank_transfer"),
    WALLET("wallet");

    val displayName: String
        get() = when (this) {
            MTN_MOBILE_MONEY -> "MTN Mobile Money"
            AIRTEL_MONEY -> "Airtel Money"
            CARD -> "Card"
            BANK_TRANSFER -> "Bank Transfer"
            WALLET -> "EventPass Wallet"
        }

    val iconName: String
        get() = when (this) {
            MTN_MOBILE_MONEY -> "phone_android"
            AIRTEL_MONEY -> "phone_android"
            CARD -> "credit_card"
            BANK_TRANSFER -> "account_balance"
            WALLET -> "account_balance_wallet"
        }

    val processingTime: String
        get() = when (this) {
            MTN_MOBILE_MONEY, AIRTEL_MONEY -> "1-24 hours"
            CARD -> "3-5 business days"
            BANK_TRANSFER -> "2-3 business days"
            WALLET -> "Instant"
        }
}

/**
 * Refund policy - defines refund rules for an event or ticket type.
 */
data class RefundPolicy(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String,
    val ticketTypeId: String? = null, // null = applies to all ticket types

    // Core policy settings
    val isRefundable: Boolean = true,
    val refundDeadlineHours: Int = 48, // Hours before event when refunds are cut off
    val refundPercentage: Double = 1.0, // 0.0 to 1.0
    val processingFeePercentage: Double = 0.05,

    // Time-based rules
    val fullRefundDeadlineHours: Int? = 72,
    val partialRefundDeadlineHours: Int? = 24,
    val partialRefundPercentage: Double? = 0.5,

    // Special conditions
    val allowRescheduledEventRefund: Boolean = true,
    val allowTransfer: Boolean = true,
    val requiresApproval: Boolean = false,
    val maxRefundsPerUser: Int? = null,

    // Policy text
    val policyText: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun defaultPolicy(eventId: String): RefundPolicy {
            return RefundPolicy(
                eventId = eventId,
                isRefundable = true,
                refundDeadlineHours = 48,
                refundPercentage = 1.0,
                processingFeePercentage = 0.05,
                fullRefundDeadlineHours = 72,
                partialRefundDeadlineHours = 24,
                partialRefundPercentage = 0.5,
                policyText = "Full refunds available up to 72 hours before the event. 50% refund available 24-72 hours before. No refunds within 24 hours of the event."
            )
        }

        fun nonRefundable(eventId: String): RefundPolicy {
            return RefundPolicy(
                eventId = eventId,
                isRefundable = false,
                refundDeadlineHours = 0,
                refundPercentage = 0.0,
                processingFeePercentage = 0.0,
                policyText = "This ticket is non-refundable. In case of event cancellation, a full refund will be processed automatically."
            )
        }
    }
}

/**
 * Refund request - user's request for a refund.
 */
data class RefundRequest(
    val id: String = UUID.randomUUID().toString(),
    val ticketId: String,
    val ticketNumber: String,
    val eventId: String,
    val eventTitle: String,
    val userId: String,
    val userName: String,
    val userEmail: String? = null,
    val userPhone: String? = null,

    // Request details
    val reason: RefundReason,
    val userNote: String? = null,
    val requestedAmount: Double,
    val approvedAmount: Double? = null,
    val currency: String = "UGX",

    // Original payment info
    val originalPaymentMethod: RefundPaymentMethod,
    val originalPaymentReference: String,
    val originalPurchaseDate: LocalDateTime,

    // Status tracking
    val status: RefundStatus = RefundStatus.PENDING,
    val requestedAt: LocalDateTime = LocalDateTime.now(),
    val reviewedAt: LocalDateTime? = null,
    val reviewedBy: String? = null,
    val reviewerNote: String? = null,
    val processedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null,
    val failureReason: String? = null,

    // Audit trail
    val statusHistory: List<RefundStatusChange> = emptyList()
) {
    val formattedRequestedAmount: String
        get() = "$currency ${String.format("%,.0f", requestedAmount)}"

    val formattedApprovedAmount: String?
        get() = approvedAmount?.let { "$currency ${String.format("%,.0f", it)}" }

    val timeSinceRequest: String
        get() {
            val hours = java.time.Duration.between(requestedAt, LocalDateTime.now()).toHours()
            return when {
                hours < 1 -> "Just now"
                hours < 24 -> "${hours}h ago"
                else -> "${hours / 24}d ago"
            }
        }
}

/**
 * Refund status change - tracks status changes for audit trail.
 */
data class RefundStatusChange(
    val id: String = UUID.randomUUID().toString(),
    val fromStatus: RefundStatus?,
    val toStatus: RefundStatus,
    val changedAt: LocalDateTime = LocalDateTime.now(),
    val changedBy: String? = null,
    val note: String? = null
)

/**
 * Refund transaction - financial record of a processed refund.
 */
data class RefundTransaction(
    val id: String = UUID.randomUUID().toString(),
    val refundRequestId: String,
    val ticketId: String,
    val eventId: String,
    val userId: String,
    val organizerId: String,

    // Financial details
    val originalAmount: Double,
    val refundAmount: Double,
    val processingFee: Double,
    val currency: String = "UGX",

    // Payment details
    val paymentMethod: RefundPaymentMethod,
    val paymentReference: String,
    val transactionReference: String = "",

    // Status
    val status: RefundStatus = RefundStatus.PROCESSING,
    val reason: RefundReason,

    // Timestamps
    val initiatedAt: LocalDateTime = LocalDateTime.now(),
    val processedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null,
    val failedAt: LocalDateTime? = null,
    val failureReason: String? = null,

    // Audit
    val processedBy: String? = null,
    val notes: String? = null
) {
    val netRefund: Double
        get() = refundAmount - processingFee

    val formattedNetRefund: String
        get() = "$currency ${String.format("%,.0f", netRefund)}"
}

/**
 * Refund eligibility result.
 */
data class RefundEligibilityResult(
    val isEligible: Boolean,
    val reason: String,
    val refundableAmount: Double,
    val refundPercentage: Double,
    val processingFee: Double,
    val netRefund: Double,
    val deadline: LocalDateTime?,
    val policy: RefundPolicy?
) {
    companion object {
        fun eligible(
            amount: Double,
            percentage: Double,
            fee: Double,
            deadline: LocalDateTime?,
            policy: RefundPolicy
        ): RefundEligibilityResult {
            return RefundEligibilityResult(
                isEligible = true,
                reason = "Eligible for refund",
                refundableAmount = amount,
                refundPercentage = percentage,
                processingFee = fee,
                netRefund = amount - fee,
                deadline = deadline,
                policy = policy
            )
        }

        fun notEligible(reason: String): RefundEligibilityResult {
            return RefundEligibilityResult(
                isEligible = false,
                reason = reason,
                refundableAmount = 0.0,
                refundPercentage = 0.0,
                processingFee = 0.0,
                netRefund = 0.0,
                deadline = null,
                policy = null
            )
        }
    }
}
