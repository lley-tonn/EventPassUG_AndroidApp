package com.eventpass.android.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Cancellation system models.
 * Migrated from iOS Domain/Models/CancellationModels.swift
 *
 * Comprehensive event cancellation models for fintech-grade reliability.
 * Ensures all paid tickets are compensated and states remain consistent.
 */

// MARK: - Cancellation Status

/**
 * Tracks the lifecycle of an event cancellation.
 */
enum class CancellationStatus(val value: String) {
    DRAFT("draft"),                 // Cancellation started but not confirmed
    CONFIRMING("confirming"),       // Awaiting final confirmation
    PROCESSING("processing"),       // Cancellation in progress
    COMPLETED("completed"),         // Fully cancelled and compensated
    FAILED("failed");               // Cancellation failed (requires manual intervention)

    val displayName: String
        get() = when (this) {
            DRAFT -> "Draft"
            CONFIRMING -> "Awaiting Confirmation"
            PROCESSING -> "Processing"
            COMPLETED -> "Completed"
            FAILED -> "Failed"
        }

    val iconName: String
        get() = when (this) {
            DRAFT -> "description"
            CONFIRMING -> "warning"
            PROCESSING -> "sync"
            COMPLETED -> "verified"
            FAILED -> "error"
        }

    val colorHex: Long
        get() = when (this) {
            DRAFT -> 0xFF9E9E9E      // Gray
            CONFIRMING -> 0xFFFF9800 // Orange
            PROCESSING -> 0xFF2196F3 // Blue
            COMPLETED -> 0xFF4CAF50  // Green
            FAILED -> 0xFFF44336     // Red
        }

    val isReversible: Boolean
        get() = when (this) {
            DRAFT, CONFIRMING -> true
            PROCESSING, COMPLETED, FAILED -> false
        }
}

// MARK: - Cancellation Reason

/**
 * Categorizes why an event is being cancelled.
 */
enum class CancellationReason(val value: String) {
    ORGANIZER_DECISION("organizer_decision"),
    VENUE_ISSUE("venue_issue"),
    FORCE_MAJEURE("force_majeure"),
    REGULATION("regulation"),
    LOW_SALES("low_sales"),
    DUPLICATE("duplicate"),
    ADMIN_ACTION("admin_action");

    val displayName: String
        get() = when (this) {
            ORGANIZER_DECISION -> "Organizer Decision"
            VENUE_ISSUE -> "Venue Issue"
            FORCE_MAJEURE -> "Force Majeure"
            REGULATION -> "Government Regulation"
            LOW_SALES -> "Low Ticket Sales"
            DUPLICATE -> "Duplicate Event"
            ADMIN_ACTION -> "Platform Action"
        }

    val description: String
        get() = when (this) {
            ORGANIZER_DECISION -> "You have decided to cancel this event"
            VENUE_ISSUE -> "The venue is no longer available or suitable"
            FORCE_MAJEURE -> "Unforeseeable circumstances (natural disaster, pandemic, etc.)"
            REGULATION -> "Government restrictions or regulatory requirements"
            LOW_SALES -> "Insufficient ticket sales to proceed"
            DUPLICATE -> "This event was created in error (duplicate)"
            ADMIN_ACTION -> "EventPass platform administrative action"
        }

    val iconName: String
        get() = when (this) {
            ORGANIZER_DECISION -> "person_off"
            VENUE_ISSUE -> "domain_disabled"
            FORCE_MAJEURE -> "warning"
            REGULATION -> "account_balance"
            LOW_SALES -> "trending_down"
            DUPLICATE -> "content_copy"
            ADMIN_ACTION -> "shield"
        }

    /**
     * Reasons that typically warrant full refund.
     */
    val warrantsFullRefund: Boolean
        get() = when (this) {
            ORGANIZER_DECISION, VENUE_ISSUE, FORCE_MAJEURE, REGULATION, DUPLICATE, ADMIN_ACTION -> true
            LOW_SALES -> true // Still full refund, but might affect organizer payout
        }

    companion object {
        /**
         * Reasons available to organizers (vs admin-only).
         */
        val organizerReasons: List<CancellationReason>
            get() = listOf(ORGANIZER_DECISION, VENUE_ISSUE, FORCE_MAJEURE, REGULATION, LOW_SALES, DUPLICATE)

        /**
         * Reasons only available to platform admins.
         */
        val adminOnlyReasons: List<CancellationReason>
            get() = listOf(ADMIN_ACTION)
    }
}

// MARK: - Compensation Type

/**
 * How attendees will be compensated.
 */
enum class CompensationType(val value: String) {
    FULL_REFUND("full_refund"),
    PARTIAL_REFUND("partial_refund"),
    EVENT_CREDIT("event_credit");

    val displayName: String
        get() = when (this) {
            FULL_REFUND -> "Full Refund"
            PARTIAL_REFUND -> "Partial Refund"
            EVENT_CREDIT -> "Event Credit"
        }

    val description: String
        get() = when (this) {
            FULL_REFUND -> "100% of ticket price refunded to original payment method"
            PARTIAL_REFUND -> "Percentage of ticket price refunded"
            EVENT_CREDIT -> "Credit for future EventPass events"
        }

    val iconName: String
        get() = when (this) {
            FULL_REFUND -> "replay"
            PARTIAL_REFUND -> "percent"
            EVENT_CREDIT -> "credit_card"
        }
}

// MARK: - Cancellation Impact

/**
 * Calculated impact of cancelling an event.
 */
data class CancellationImpact(
    val eventId: String,
    val calculatedAt: LocalDateTime = LocalDateTime.now(),

    // Ticket statistics
    val ticketsSold: Int = 0,
    val attendeesCount: Int = 0,  // Unique attendees (may differ from tickets)
    val vipTickets: Int = 0,
    val regularTickets: Int = 0,
    val checkInsCompleted: Int = 0,
    val pendingPayments: Int = 0,
    val transferredTickets: Int = 0,
    val partiallyRefundedTickets: Int = 0,

    // Financial impact
    val grossRevenue: Double = 0.0,
    val refundTotal: Double = 0.0,
    val platformFeesRetained: Double = 0.0,
    val processingFeesEstimate: Double = 0.0,
    val netRefundAmount: Double = 0.0,
    val organizerPayoutAdjustment: Double = 0.0,

    // Currency
    val currency: String = "UGX",

    // Breakdown by ticket type
    val ticketTypeBreakdown: List<TicketTypeImpact> = emptyList(),

    // Payment method breakdown
    val paymentMethodBreakdown: List<PaymentMethodImpact> = emptyList()
) {
    // Computed properties
    val hasCheckedInAttendees: Boolean
        get() = checkInsCompleted > 0

    val hasPendingPayments: Boolean
        get() = pendingPayments > 0

    val hasTransferredTickets: Boolean
        get() = transferredTickets > 0

    val hasPartialRefunds: Boolean
        get() = partiallyRefundedTickets > 0

    val formattedRefundTotal: String
        get() = formatCurrency(refundTotal)

    val formattedGrossRevenue: String
        get() = formatCurrency(grossRevenue)

    val formattedNetRefund: String
        get() = formatCurrency(netRefundAmount)

    private fun formatCurrency(amount: Double): String {
        return "UGX ${String.format("%,.0f", amount)}"
    }

    // Edge case warnings
    val warnings: List<CancellationWarning>
        get() {
            val warningList = mutableListOf<CancellationWarning>()

            if (hasCheckedInAttendees) {
                warningList.add(CancellationWarning.CheckedInAttendees(checkInsCompleted))
            }
            if (hasPendingPayments) {
                warningList.add(CancellationWarning.PendingPayments(pendingPayments))
            }
            if (hasTransferredTickets) {
                warningList.add(CancellationWarning.TransferredTickets(transferredTickets))
            }
            if (hasPartialRefunds) {
                warningList.add(CancellationWarning.PartiallyRefundedTickets(partiallyRefundedTickets))
            }

            return warningList
        }
}

/**
 * Impact breakdown by ticket type.
 */
data class TicketTypeImpact(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val ticketsSold: Int,
    val revenue: Double,
    val refundAmount: Double
) {
    val formattedRevenue: String
        get() = "UGX ${String.format("%,.0f", revenue)}"
}

/**
 * Impact breakdown by payment method.
 */
data class PaymentMethodImpact(
    val paymentMethod: RefundPaymentMethod,
    val ticketCount: Int,
    val refundAmount: Double,
    val estimatedProcessingTime: String
) {
    val id: String
        get() = paymentMethod.value
}

// MARK: - Cancellation Warning

/**
 * Warnings about edge cases that need attention.
 */
sealed class CancellationWarning {
    data class CheckedInAttendees(val count: Int) : CancellationWarning()
    data class PendingPayments(val count: Int) : CancellationWarning()
    data class TransferredTickets(val count: Int) : CancellationWarning()
    data class PartiallyRefundedTickets(val count: Int) : CancellationWarning()
    data class OfflineTickets(val count: Int) : CancellationWarning()

    val id: String
        get() = when (this) {
            is CheckedInAttendees -> "checked_in"
            is PendingPayments -> "pending_payments"
            is TransferredTickets -> "transferred"
            is PartiallyRefundedTickets -> "partial_refunds"
            is OfflineTickets -> "offline"
        }

    val title: String
        get() = when (this) {
            is CheckedInAttendees -> "$count Attendee${if (count == 1) "" else "s"} Already Checked In"
            is PendingPayments -> "$count Pending Payment${if (count == 1) "" else "s"}"
            is TransferredTickets -> "$count Transferred Ticket${if (count == 1) "" else "s"}"
            is PartiallyRefundedTickets -> "$count Partially Refunded Ticket${if (count == 1) "" else "s"}"
            is OfflineTickets -> "$count Offline Ticket${if (count == 1) "" else "s"}"
        }

    val description: String
        get() = when (this) {
            is CheckedInAttendees -> "These attendees have already used their tickets. They will still receive refunds."
            is PendingPayments -> "Payments still processing. These will be cancelled and not charged."
            is TransferredTickets -> "Tickets transferred to new owners. Refunds go to current ticket holders."
            is PartiallyRefundedTickets -> "Tickets with prior partial refunds. Remaining balance will be refunded."
            is OfflineTickets -> "Tickets sold offline require manual refund processing."
        }

    val iconName: String
        get() = when (this) {
            is CheckedInAttendees -> "check_circle"
            is PendingPayments -> "schedule"
            is TransferredTickets -> "swap_horiz"
            is PartiallyRefundedTickets -> "history"
            is OfflineTickets -> "wifi_off"
        }

    val severity: WarningSeverity
        get() = when (this) {
            is CheckedInAttendees -> WarningSeverity.INFO
            is PendingPayments -> WarningSeverity.WARNING
            is TransferredTickets -> WarningSeverity.INFO
            is PartiallyRefundedTickets -> WarningSeverity.INFO
            is OfflineTickets -> WarningSeverity.WARNING
        }

    enum class WarningSeverity(val colorHex: Long) {
        INFO(0xFF2196F3),      // Blue
        WARNING(0xFFFF9800),   // Orange
        CRITICAL(0xFFF44336)   // Red
    }
}

// MARK: - Compensation Plan

/**
 * Defines how attendees will be compensated.
 */
data class CompensationPlan(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String,

    // Compensation settings
    val compensationType: CompensationType = CompensationType.FULL_REFUND,
    val refundPercentage: Double = 1.0,  // 1.0 for full, 0.5 for 50%, etc.
    val creditMultiplier: Double? = null, // e.g., 1.1 for 110% credit bonus

    // Processing
    val processingMethod: ProcessingMethod = ProcessingMethod.AUTOMATIC,
    val processingDeadline: LocalDateTime = LocalDateTime.now().plusDays(5),

    // Financial
    val totalRefundAmount: Double = 0.0,
    val platformFeeHandling: PlatformFeeHandling = PlatformFeeHandling.WAIVE,
    val estimatedProcessingFees: Double = 0.0,

    // Notes
    val organizerNote: String? = null,
    val internalNote: String? = null,

    // Notification
    val notificationTemplate: NotificationTemplate? = null
) {
    enum class ProcessingMethod(val value: String) {
        AUTOMATIC("automatic"),      // System processes all refunds
        MANUAL("manual"),            // Organizer handles refunds
        HYBRID("hybrid");            // System + manual for exceptions

        val displayName: String
            get() = when (this) {
                AUTOMATIC -> "Automatic Processing"
                MANUAL -> "Manual Processing"
                HYBRID -> "Hybrid (Auto + Manual)"
            }
    }

    enum class PlatformFeeHandling(val value: String) {
        WAIVE("waive"),              // Platform waives fees
        DEDUCT("deduct"),            // Fees deducted from refund
        ORGANIZER_PAYS("organizer"); // Organizer covers fees

        val displayName: String
            get() = when (this) {
                WAIVE -> "Platform Waives Fees"
                DEDUCT -> "Deduct from Refund"
                ORGANIZER_PAYS -> "Organizer Covers Fees"
            }
    }
}

// MARK: - Notification Template

/**
 * Template for attendee notifications.
 */
data class NotificationTemplate(
    val subject: String,
    val body: String,
    val includeRefundDetails: Boolean,
    val includeTimeline: Boolean,
    val includeSupportContact: Boolean
) {
    companion object {
        val defaultCancellation: NotificationTemplate
            get() = NotificationTemplate(
                subject = "Event Cancelled: {{event_name}}",
                body = """
                    We regret to inform you that {{event_name}} scheduled for {{event_date}} has been cancelled.

                    {{#refund_details}}
                    Refund Details:
                    Amount: {{refund_amount}}
                    Method: {{refund_method}}
                    Timeline: {{refund_timeline}}
                    {{/refund_details}}

                    We apologize for any inconvenience. If you have questions, please contact our support team.

                    - The EventPass Team
                """.trimIndent(),
                includeRefundDetails = true,
                includeTimeline = true,
                includeSupportContact = true
            )
    }
}

// MARK: - Event Cancellation

/**
 * Complete record of an event cancellation.
 */
data class EventCancellation(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String,
    val eventTitle: String,
    val organizerId: String,

    // Cancellation details
    val reason: CancellationReason,
    val reasonNote: String? = null,
    val status: CancellationStatus = CancellationStatus.DRAFT,

    // Impact snapshot
    val impact: CancellationImpact,

    // Compensation
    val compensationPlan: CompensationPlan,

    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val confirmedAt: LocalDateTime? = null,
    val processingStartedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null,

    // Audit
    val initiatedBy: String,
    val confirmedBy: String? = null,
    val confirmationCode: String? = null,  // The typed CONFIRM

    // Processing results
    val refundRequestsCreated: Int = 0,
    val refundsProcessed: Int = 0,
    val refundsFailed: Int = 0,
    val notificationsSent: Int = 0,
    val notificationsFailed: Int = 0,

    // Errors
    val processingErrors: List<CancellationProcessingError> = emptyList()
) {
    val isReversible: Boolean
        get() = status.isReversible

    val hasErrors: Boolean
        get() = processingErrors.isNotEmpty() || refundsFailed > 0 || notificationsFailed > 0

    val processingProgress: Double
        get() = if (impact.ticketsSold > 0) {
            refundsProcessed.toDouble() / impact.ticketsSold.toDouble()
        } else 1.0

    companion object {
        val sample: EventCancellation
            get() {
                val eventId = UUID.randomUUID().toString()
                val impact = CancellationImpact(
                    eventId = eventId,
                    ticketsSold = 150,
                    attendeesCount = 142,
                    vipTickets = 25,
                    regularTickets = 125,
                    checkInsCompleted = 0,
                    pendingPayments = 3,
                    transferredTickets = 5,
                    partiallyRefundedTickets = 2,
                    grossRevenue = 15_000_000.0,
                    refundTotal = 14_500_000.0,
                    platformFeesRetained = 0.0,
                    processingFeesEstimate = 145_000.0,
                    netRefundAmount = 14_355_000.0,
                    organizerPayoutAdjustment = -14_500_000.0,
                    ticketTypeBreakdown = listOf(
                        TicketTypeImpact(name = "VIP", ticketsSold = 25, revenue = 5_000_000.0, refundAmount = 5_000_000.0),
                        TicketTypeImpact(name = "Regular", ticketsSold = 125, revenue = 10_000_000.0, refundAmount = 9_500_000.0)
                    ),
                    paymentMethodBreakdown = listOf(
                        PaymentMethodImpact(RefundPaymentMethod.MTN_MOBILE_MONEY, 100, 10_000_000.0, "1-24 hours"),
                        PaymentMethodImpact(RefundPaymentMethod.AIRTEL_MONEY, 35, 3_500_000.0, "1-24 hours"),
                        PaymentMethodImpact(RefundPaymentMethod.CARD, 15, 1_000_000.0, "3-5 business days")
                    )
                )

                val plan = CompensationPlan(
                    eventId = eventId,
                    compensationType = CompensationType.FULL_REFUND,
                    refundPercentage = 1.0,
                    totalRefundAmount = 14_500_000.0,
                    notificationTemplate = NotificationTemplate.defaultCancellation
                )

                return EventCancellation(
                    eventId = eventId,
                    eventTitle = "Nyege Nyege Festival 2024",
                    organizerId = UUID.randomUUID().toString(),
                    reason = CancellationReason.ORGANIZER_DECISION,
                    reasonNote = "Due to unforeseen circumstances, we must cancel this event.",
                    impact = impact,
                    compensationPlan = plan,
                    initiatedBy = UUID.randomUUID().toString()
                )
            }
    }
}

// MARK: - Cancellation Error

/**
 * Errors that occurred during cancellation processing.
 */
data class CancellationProcessingError(
    val id: String = UUID.randomUUID().toString(),
    val ticketId: String? = null,
    val errorType: ErrorType,
    val message: String,
    val occurredAt: LocalDateTime = LocalDateTime.now(),
    val resolved: Boolean = false,
    val resolvedAt: LocalDateTime? = null,
    val resolution: String? = null
) {
    enum class ErrorType(val value: String) {
        REFUND_FAILED("refund_failed"),
        NOTIFICATION_FAILED("notification_failed"),
        TICKET_UPDATE_FAILED("ticket_update_failed"),
        PAYMENT_CANCELLATION_FAILED("payment_cancellation_failed"),
        UNKNOWN("unknown");

        val displayName: String
            get() = when (this) {
                REFUND_FAILED -> "Refund Failed"
                NOTIFICATION_FAILED -> "Notification Failed"
                TICKET_UPDATE_FAILED -> "Ticket Update Failed"
                PAYMENT_CANCELLATION_FAILED -> "Payment Cancellation Failed"
                UNKNOWN -> "Unknown Error"
            }
    }
}

// MARK: - Cancellation Analytics

/**
 * Analytics for cancellation tracking.
 */
data class CancellationAnalytics(
    val eventId: String,
    val organizerId: String,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,

    val totalCancellations: Int = 0,
    val cancellationsByReason: Map<CancellationReason, Int> = emptyMap(),
    val totalRefundsIssued: Double = 0.0,
    val averageProcessingTimeSeconds: Long = 0,
    val successRate: Double = 1.0
)

// MARK: - Cancellation Step (for UI flow)

/**
 * Steps in the cancellation wizard flow.
 */
enum class CancellationStep {
    REASON,
    IMPACT,
    COMPENSATION,
    NOTIFICATION,
    FINANCIAL,
    CONFIRMATION;

    val displayName: String
        get() = when (this) {
            REASON -> "Reason"
            IMPACT -> "Impact"
            COMPENSATION -> "Compensation"
            NOTIFICATION -> "Notification"
            FINANCIAL -> "Financial"
            CONFIRMATION -> "Confirm"
        }

    val stepNumber: Int
        get() = ordinal + 1

    companion object {
        val totalSteps: Int = entries.size
    }
}

// MARK: - Notification Preview

/**
 * Preview of cancellation notification for organizer review.
 */
data class NotificationPreview(
    val subject: String,
    val body: String,
    val recipientCount: Int,
    val estimatedDeliveryTime: String
)

// MARK: - Cancellation Notification Result

/**
 * Result of sending cancellation notifications.
 */
data class CancellationNotificationResult(
    val totalRecipients: Int,
    val successfulSends: Int,
    val failedSends: Int,
    val failedRecipients: List<String> = emptyList()
) {
    val allSuccessful: Boolean
        get() = failedSends == 0
}
