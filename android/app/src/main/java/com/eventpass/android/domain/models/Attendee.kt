package com.eventpass.android.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Attendee model.
 * Migrated from iOS Domain/Models/Attendee.swift
 *
 * Represents a ticket holder/attendee for an event.
 */
data class Attendee(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String,
    val ticketId: String,
    val orderId: String,
    val fullName: String,
    // NOTE: Email/phone are intentionally NOT exported for privacy
    val email: String? = null,
    val phoneNumber: String? = null,
    val ticketType: String,
    val purchaseDate: LocalDateTime,
    val checkInStatus: CheckInStatus = CheckInStatus.NOT_CHECKED_IN,
    val attendanceStatus: AttendanceStatus = AttendanceStatus.EXPECTED,
    val isVIP: Boolean = false,
    val marketingConsent: Boolean = false
) {
    /**
     * Display initials for avatar.
     */
    val initials: String
        get() {
            val parts = fullName.split(" ")
            val first = parts.firstOrNull()?.firstOrNull()?.uppercaseChar() ?: ""
            val last = if (parts.size > 1) parts.last().firstOrNull()?.uppercaseChar() ?: "" else ""
            return "$first$last"
        }

    /**
     * Check-in status text.
     */
    val checkInStatusText: String
        get() = checkInStatus.displayName

    companion object {
        /**
         * Creates an Attendee from a Ticket.
         */
        fun fromTicket(ticket: Ticket, marketingConsent: Boolean = false): Attendee {
            return Attendee(
                eventId = ticket.eventId,
                ticketId = ticket.id,
                orderId = ticket.orderNumber,
                fullName = "Attendee", // In production, fetch from user service
                ticketType = ticket.ticketType.name,
                purchaseDate = ticket.purchaseDate,
                checkInStatus = when (ticket.scanStatus) {
                    TicketScanStatus.SCANNED -> CheckInStatus.CHECKED_IN
                    else -> CheckInStatus.NOT_CHECKED_IN
                },
                attendanceStatus = when (ticket.scanStatus) {
                    TicketScanStatus.SCANNED -> AttendanceStatus.ATTENDED
                    TicketScanStatus.EXPIRED -> AttendanceStatus.ABSENT
                    else -> AttendanceStatus.EXPECTED
                },
                isVIP = ticket.ticketType.name.contains("VIP", ignoreCase = true),
                marketingConsent = marketingConsent
            )
        }

        /**
         * Mock attendees for preview and testing.
         */
        fun mockAttendees(eventId: String, count: Int = 25): List<Attendee> {
            val names = listOf(
                "Sarah Nakamya", "David Ochieng", "Grace Atwine", "Peter Ssempala",
                "Mary Kirabo", "John Mukasa", "Agnes Namuli", "Robert Kasozi",
                "Florence Nabwami", "Joseph Sserwanga", "Rose Namutebi", "Patrick Kizza"
            )
            val ticketTypes = listOf("General Admission", "VIP", "VVIP", "Early Bird")

            return (1..count).map { index ->
                val name = names[index % names.size]
                val ticketType = ticketTypes[index % ticketTypes.size]
                Attendee(
                    eventId = eventId,
                    ticketId = UUID.randomUUID().toString(),
                    orderId = "ORD-${(100000..999999).random()}",
                    fullName = name,
                    ticketType = ticketType,
                    purchaseDate = LocalDateTime.now().minusDays((1..30).random().toLong()),
                    checkInStatus = if ((1..10).random() > 3) CheckInStatus.CHECKED_IN else CheckInStatus.NOT_CHECKED_IN,
                    attendanceStatus = when ((1..10).random()) {
                        in 1..6 -> AttendanceStatus.ATTENDED
                        in 7..8 -> AttendanceStatus.EXPECTED
                        else -> AttendanceStatus.ABSENT
                    },
                    isVIP = ticketType.contains("VIP"),
                    marketingConsent = (1..10).random() > 5
                )
            }
        }
    }
}

/**
 * Check-in status enum.
 * Matches iOS CheckInStatus.
 */
enum class CheckInStatus(val value: String) {
    NOT_CHECKED_IN("notCheckedIn"),
    CHECKED_IN("checkedIn"),
    NO_SHOW("noShow");

    val displayName: String
        get() = when (this) {
            NOT_CHECKED_IN -> "Not Checked In"
            CHECKED_IN -> "Checked In"
            NO_SHOW -> "No Show"
        }
}

/**
 * Attendance status enum.
 * Matches iOS AttendanceStatus.
 */
enum class AttendanceStatus(val value: String) {
    EXPECTED("expected"),
    ATTENDED("attended"),
    ABSENT("absent");

    val displayName: String
        get() = when (this) {
            EXPECTED -> "Expected"
            ATTENDED -> "Attended"
            ABSENT -> "Absent"
        }
}

/**
 * Attendee export options.
 */
data class AttendeeExportOptions(
    val includeEmail: Boolean = true,
    val includePhone: Boolean = true,
    val includeTicketType: Boolean = true,
    val includePurchaseDate: Boolean = true,
    val includeCheckInStatus: Boolean = true,
    val includeNotes: Boolean = false,
    val filterByTicketType: String? = null,
    val filterByCheckInStatus: Boolean? = null // null = all, true = checked in only, false = not checked in only
)

/**
 * Attendee list summary.
 */
data class AttendeeListSummary(
    val eventId: String,
    val totalAttendees: Int,
    val checkedInCount: Int,
    val notCheckedInCount: Int,
    val byTicketType: Map<String, Int>
) {
    val checkInRate: Double
        get() = if (totalAttendees > 0) {
            checkedInCount.toDouble() / totalAttendees.toDouble()
        } else 0.0

    val checkInPercentage: Int
        get() = (checkInRate * 100).toInt()
}
