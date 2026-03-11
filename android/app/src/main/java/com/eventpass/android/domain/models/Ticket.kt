package com.eventpass.android.domain.models

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Ticket model.
 * Migrated from iOS Domain/Models/Ticket.swift
 */
data class Ticket(
    val id: String = UUID.randomUUID().toString(),
    val ticketNumber: String, // Unique ticket number (e.g., "TKT-001234")
    val orderNumber: String, // Order number shared by tickets in same purchase
    val eventId: String,
    val eventTitle: String,
    val eventDate: LocalDateTime,
    val eventEndDate: LocalDateTime,
    val eventVenue: String,
    val eventVenueAddress: String,
    val eventVenueCity: String,
    val venueLatitude: Double,
    val venueLongitude: Double,
    val eventDescription: String,
    val eventOrganizerName: String,
    val eventPosterUrl: String? = null,
    val ticketType: TicketType,
    val userId: String,
    val purchaseDate: LocalDateTime = LocalDateTime.now(),
    val scanStatus: TicketScanStatus = TicketScanStatus.UNUSED,
    val scanDate: LocalDateTime? = null,
    val qrCodeData: String = "TICKET:${UUID.randomUUID()}",
    val seatNumber: String? = null,
    val userRating: Double? = null,
    val expiredAt: LocalDateTime? = null
) {
    /**
     * Whether the ticket is expired.
     */
    val isExpired: Boolean
        get() = eventEndDate < LocalDateTime.now()

    /**
     * Whether the ticket can be scanned.
     */
    val canBeScanned: Boolean
        get() = scanStatus == TicketScanStatus.UNUSED && !isExpired

    /**
     * Whether the ticket should be auto-deleted (60 days after expiry).
     */
    val shouldBeDeleted: Boolean
        get() {
            val expiredDate = expiredAt ?: return false
            val sixtyDaysAgo = LocalDateTime.now().minusDays(60)
            return expiredDate < sixtyDaysAgo
        }

    /**
     * Formatted price string.
     */
    val formattedPrice: String
        get() = "${ticketType.currency} ${String.format("%,.0f", ticketType.price)}"

    companion object {
        fun generateTicketNumber(): String {
            val random = (100000..999999).random()
            return "TKT-$random"
        }

        fun generateOrderNumber(): String {
            val random = (100000..999999).random()
            return "ORD-$random"
        }
    }
}

/**
 * Ticket scan status enum.
 */
enum class TicketScanStatus {
    UNUSED,
    SCANNED,
    EXPIRED;

    val displayName: String
        get() = when (this) {
            UNUSED -> "Valid"
            SCANNED -> "Used"
            EXPIRED -> "Expired"
        }
}

/**
 * Ticket type model.
 * Migrated from iOS Domain/Models/TicketType.swift
 */
data class TicketType(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double, // in UGX
    val quantity: Int,
    val sold: Int = 0,
    val description: String? = null,
    val perks: List<String> = emptyList(),
    val saleStartDate: LocalDateTime = LocalDateTime.now(),
    val saleEndDate: LocalDateTime = LocalDateTime.now().plusDays(30),
    val isUnlimitedQuantity: Boolean = false,
    val currency: String = "UGX",
    val maxPerOrder: Int = 10 // Maximum tickets per order
) {
    /**
     * Remaining tickets.
     */
    val remaining: Int
        get() = if (isUnlimitedQuantity) Int.MAX_VALUE else (quantity - sold)

    /**
     * Whether tickets are sold out.
     */
    val isSoldOut: Boolean
        get() = !isUnlimitedQuantity && remaining <= 0

    /**
     * Formatted price string.
     */
    val formattedPrice: String
        get() = when {
            isSoldOut -> "Sold Out"
            price == 0.0 -> "Free"
            else -> "$currency ${String.format("%,.0f", price)}"
        }

    /**
     * Availability status.
     */
    val availabilityStatus: TicketAvailabilityStatus
        get() {
            val now = LocalDateTime.now()
            return when {
                isSoldOut -> TicketAvailabilityStatus.SOLD_OUT
                now < saleStartDate -> TicketAvailabilityStatus.UPCOMING
                now > saleEndDate -> TicketAvailabilityStatus.EXPIRED
                else -> TicketAvailabilityStatus.ACTIVE
            }
        }

    /**
     * Whether ticket is available for purchase.
     */
    val isAvailableForPurchase: Boolean
        get() {
            val now = LocalDateTime.now()
            return now >= saleStartDate && now <= saleEndDate && !isSoldOut
        }

    /**
     * Alias for isAvailableForPurchase.
     */
    val isAvailable: Boolean
        get() = isAvailableForPurchase

    /**
     * Check if ticket is available for purchase with event start date.
     */
    fun isAvailableForPurchase(eventStartDate: LocalDateTime): Boolean {
        if (isSoldOut) return false

        val now = LocalDateTime.now()
        if (now >= eventStartDate) return false

        val effectiveEndDate = minOf(saleEndDate, eventStartDate)
        return now >= saleStartDate && now <= effectiveEndDate
    }

    /**
     * Availability text.
     */
    val availabilityText: String
        get() {
            val now = LocalDateTime.now()
            return when (availabilityStatus) {
                TicketAvailabilityStatus.UPCOMING -> {
                    val days = ChronoUnit.DAYS.between(now.toLocalDate(), saleStartDate.toLocalDate())
                    when {
                        days == 0L -> "On sale today"
                        days == 1L -> "On sale tomorrow"
                        days < 7 -> "On sale in $days days"
                        else -> "On sale ${saleStartDate.toLocalDate()}"
                    }
                }
                TicketAvailabilityStatus.ACTIVE -> {
                    val days = ChronoUnit.DAYS.between(now.toLocalDate(), saleEndDate.toLocalDate())
                    when {
                        days == 0L -> "Sale ends today"
                        days == 1L -> "Sale ends tomorrow"
                        days < 7 -> "Sale ends in $days days"
                        else -> "Sale ends ${saleEndDate.toLocalDate()}"
                    }
                }
                TicketAvailabilityStatus.EXPIRED -> "Sale ended"
                TicketAvailabilityStatus.SOLD_OUT -> "Sold out"
            }
        }

    /**
     * Formatted sale window.
     */
    val formattedSaleWindow: String
        get() = "${saleStartDate.toLocalDate()} - ${saleEndDate.toLocalDate()}"
}

/**
 * Ticket availability status enum.
 */
enum class TicketAvailabilityStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    ACTIVE("Active"),
    EXPIRED("Expired"),
    SOLD_OUT("Sold Out");

    val iconName: String
        get() = when (this) {
            UPCOMING -> "schedule"
            ACTIVE -> "check_circle"
            EXPIRED -> "cancel"
            SOLD_OUT -> "error"
        }

    val colorHex: Long
        get() = when (this) {
            UPCOMING -> 0xFFFF9800 // Orange
            ACTIVE -> 0xFF4CAF50 // Green
            EXPIRED -> 0xFF9E9E9E // Gray
            SOLD_OUT -> 0xFFF44336 // Red
        }
}
