package com.eventpass.android.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Scanner device model.
 * Migrated from iOS Domain/Models/ScannerModels.swift
 *
 * CRITICAL: Scanner access is strictly event-scoped and temporary
 */
data class ScannerDevice(
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val deviceName: String,
    val platform: DevicePlatform = DevicePlatform.ANDROID,
    val lastActiveAt: LocalDateTime = LocalDateTime.now(),
    val registeredAt: LocalDateTime = LocalDateTime.now()
) {
    enum class DevicePlatform(val displayName: String) {
        IOS("iOS"),
        ANDROID("Android"),
        UNKNOWN("Unknown");

        val iconName: String
            get() = when (this) {
                IOS -> "phone_iphone"
                ANDROID -> "phone_android"
                UNKNOWN -> "help"
            }
    }

    companion object
}

/**
 * Scanner session - authorized scanning session for a SPECIFIC event.
 * CRITICAL: Sessions are event-scoped and expire automatically.
 */
data class ScannerSession(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String,
    val organizerId: String,
    val deviceId: String,
    val status: ScannerSessionStatus = ScannerSessionStatus.PENDING,
    val pairedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime,
    val revokedAt: LocalDateTime? = null,
    val revokedBy: String? = null,
    val lastScanAt: LocalDateTime? = null,
    val scanCount: Int = 0,
    val permissions: ScannerPermissions = ScannerPermissions.default
) {
    /**
     * Check if session is currently valid for scanning.
     */
    val isValid: Boolean
        get() = status == ScannerSessionStatus.ACTIVE && LocalDateTime.now() < expiresAt

    /**
     * Time remaining until expiry in seconds.
     */
    val timeRemainingSeconds: Long
        get() {
            val now = LocalDateTime.now()
            return if (expiresAt > now) {
                java.time.Duration.between(now, expiresAt).seconds
            } else 0
        }

    /**
     * Formatted time remaining.
     */
    val formattedTimeRemaining: String
        get() {
            val seconds = timeRemainingSeconds
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            return when {
                hours > 0 -> "${hours}h ${minutes}m"
                minutes > 0 -> "${minutes}m"
                else -> "Expired"
            }
        }

    companion object
}

enum class ScannerSessionStatus(val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    REVOKED("revoked"),
    EXPIRED("expired");

    val displayName: String
        get() = when (this) {
            PENDING -> "Pending"
            ACTIVE -> "Active"
            REVOKED -> "Revoked"
            EXPIRED -> "Expired"
        }

    val iconName: String
        get() = when (this) {
            PENDING -> "schedule"
            ACTIVE -> "check_circle"
            REVOKED -> "cancel"
            EXPIRED -> "timer_off"
        }

    val colorHex: Long
        get() = when (this) {
            PENDING -> 0xFFFFEB3B // Yellow
            ACTIVE -> 0xFF4CAF50 // Green
            REVOKED -> 0xFFF44336 // Red
            EXPIRED -> 0xFF9E9E9E // Gray
        }
}

/**
 * Scanner permissions - defines what a scanner session is allowed to do.
 * CRITICAL: Scanners have minimal permissions - no dashboard/financial/export access.
 */
data class ScannerPermissions(
    val scanTickets: Boolean = true,
    val viewBasicAttendee: Boolean = true,
    val viewDetailedAttendee: Boolean = false,
    val manualCheckIn: Boolean = false
) {
    // Explicitly denied permissions (documented for clarity)
    val dashboardAccess: Boolean = false
    val financialData: Boolean = false
    val exportAccess: Boolean = false
    val deviceManagement: Boolean = false

    companion object {
        val default = ScannerPermissions(
            scanTickets = true,
            viewBasicAttendee = true,
            viewDetailedAttendee = false,
            manualCheckIn = false
        )

        val extended = ScannerPermissions(
            scanTickets = true,
            viewBasicAttendee = true,
            viewDetailedAttendee = true,
            manualCheckIn = true
        )
    }
}

/**
 * Pairing session - temporary session used to connect a scanner device.
 * CRITICAL: Expires after 5 minutes for security.
 */
data class PairingSession(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String,
    val organizerId: String,
    val qrCodeData: String = generateQrData(UUID.randomUUID().toString()),
    val pairingCode: String = generatePairingCode(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    val usedAt: LocalDateTime? = null,
    val usedByDeviceId: String? = null
) {
    /**
     * Check if pairing session is still valid.
     */
    val isValid: Boolean
        get() = usedAt == null && LocalDateTime.now() < expiresAt

    /**
     * Time remaining until expiry in seconds.
     */
    val timeRemainingSeconds: Long
        get() {
            val now = LocalDateTime.now()
            return if (expiresAt > now) {
                java.time.Duration.between(now, expiresAt).seconds
            } else 0
        }

    /**
     * Formatted time remaining (MM:SS).
     */
    val formattedTimeRemaining: String
        get() {
            val seconds = timeRemainingSeconds.toInt()
            val minutes = seconds / 60
            val secs = seconds % 60
            return String.format("%d:%02d", minutes, secs)
        }

    companion object {
        const val DEFAULT_EXPIRY_MINUTES = 5L

        private fun generateQrData(sessionId: String): String {
            return "eventpass://pair?session=$sessionId"
        }

        private fun generatePairingCode(): String {
            return (100000..999999).random().toString()
        }
    }
}

/**
 * Scan request - payload for validating a ticket scan.
 */
data class ScanRequest(
    val scannerSessionId: String,
    val eventId: String,
    val ticketQR: String,
    val scannedAt: LocalDateTime = LocalDateTime.now(),
    val deviceId: String
)

/**
 * Scan result - result of a ticket scan validation.
 */
data class ScanResult(
    val id: String = UUID.randomUUID().toString(),
    val ticketId: String,
    val status: ScanResultStatus,
    val attendeeName: String? = null,
    val ticketType: String? = null,
    val message: String,
    val scannedAt: LocalDateTime = LocalDateTime.now()
) {
    val isSuccess: Boolean
        get() = status == ScanResultStatus.VALID
}

enum class ScanResultStatus(val value: String) {
    VALID("valid"),
    ALREADY_USED("already_used"),
    INVALID_TICKET("invalid_ticket"),
    WRONG_EVENT("wrong_event"),
    REFUNDED("refunded"),
    EXPIRED("expired"),
    SESSION_INVALID("session_invalid");

    val displayMessage: String
        get() = when (this) {
            VALID -> "Valid Ticket"
            ALREADY_USED -> "Already Scanned"
            INVALID_TICKET -> "Invalid Ticket"
            WRONG_EVENT -> "Wrong Event"
            REFUNDED -> "Ticket Refunded"
            EXPIRED -> "Ticket Expired"
            SESSION_INVALID -> "Session Invalid"
        }

    val iconName: String
        get() = when (this) {
            VALID -> "check_circle"
            ALREADY_USED -> "warning"
            INVALID_TICKET -> "cancel"
            WRONG_EVENT -> "swap_horiz"
            REFUNDED -> "money_off"
            EXPIRED -> "timer_off"
            SESSION_INVALID -> "lock"
        }

    val colorHex: Long
        get() = when (this) {
            VALID -> 0xFF4CAF50 // Green
            ALREADY_USED -> 0xFFFF9800 // Orange
            else -> 0xFFF44336 // Red
        }
}

/**
 * Connected scanner info shown to organizer.
 */
data class ConnectedScanner(
    val id: String,
    val device: ScannerDevice,
    val session: ScannerSession
) {
    val isActive: Boolean
        get() = session.status == ScannerSessionStatus.ACTIVE

    val lastActivity: String
        get() {
            val now = LocalDateTime.now()
            val lastActive = device.lastActiveAt
            val seconds = java.time.Duration.between(lastActive, now).seconds

            return when {
                seconds < 60 -> "Just now"
                seconds < 3600 -> "${seconds / 60}m ago"
                seconds < 86400 -> "${seconds / 3600}h ago"
                else -> "${seconds / 86400}d ago"
            }
        }
}
