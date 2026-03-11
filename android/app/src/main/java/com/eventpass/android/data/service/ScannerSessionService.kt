package com.eventpass.android.data.service

import android.util.Log
import com.eventpass.android.domain.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URI
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Scanner Session Service Protocol.
 * Migrated from iOS Data/Services/ScannerSessionService.swift
 *
 * Service for managing BYOS scanner device pairing and sessions.
 * CRITICAL: All scanner access is event-scoped, temporary, and revocable.
 */
interface ScannerSessionServiceProtocol {
    // Pairing (Organizer side)
    suspend fun createPairingSession(eventId: String, organizerId: String): PairingSession
    suspend fun cancelPairingSession(pairingId: String)

    // Connection (Scanner side)
    suspend fun connectWithQR(qrData: String, deviceId: String, deviceName: String): ScannerSession
    suspend fun connectWithCode(code: String, deviceId: String, deviceName: String): ScannerSession

    // Session Management
    suspend fun getActiveSessions(eventId: String): List<ScannerSession>
    suspend fun getConnectedScanners(eventId: String): List<ConnectedScanner>
    suspend fun revokeSession(sessionId: String, organizerId: String)
    suspend fun revokeAllSessions(eventId: String, organizerId: String)
    suspend fun renameDevice(deviceId: String, newName: String)

    // Scanning
    suspend fun validateScan(request: ScanRequest): ScanResult

    // Session State
    fun getCurrentSession(): ScannerSession?
    fun clearCurrentSession()
    suspend fun refreshSession(): ScannerSession?

    // Expiry
    suspend fun expireSessionsForEndedEvent(eventId: String)
}

// MARK: - Scanner Errors

sealed class ScannerError : Exception() {
    object InvalidQRCode : ScannerError() {
        private fun readResolve(): Any = InvalidQRCode
        override val message: String = "Invalid QR code. Please scan the pairing QR from the organizer."
    }

    object InvalidPairingCode : ScannerError() {
        private fun readResolve(): Any = InvalidPairingCode
        override val message: String = "Invalid pairing code. Please check and try again."
    }

    object PairingSessionNotFound : ScannerError() {
        private fun readResolve(): Any = PairingSessionNotFound
        override val message: String = "Pairing session not found. Ask the organizer to generate a new code."
    }

    object PairingSessionExpired : ScannerError() {
        private fun readResolve(): Any = PairingSessionExpired
        override val message: String = "Pairing session has expired. Ask the organizer to generate a new code."
    }

    object SessionNotFound : ScannerError() {
        private fun readResolve(): Any = SessionNotFound
        override val message: String = "Scanner session not found."
    }

    object DeviceNotFound : ScannerError() {
        private fun readResolve(): Any = DeviceNotFound
        override val message: String = "Device not found."
    }

    object Unauthorized : ScannerError() {
        private fun readResolve(): Any = Unauthorized
        override val message: String = "You are not authorized to perform this action."
    }

    object NetworkError : ScannerError() {
        private fun readResolve(): Any = NetworkError
        override val message: String = "Network error. Please check your connection."
    }

    data class ScanFailed(val reason: String) : ScannerError() {
        override val message: String = "Scan failed: $reason"
    }
}

// MARK: - Scanner Analytics Event

data class ScannerAnalyticsEvent(
    val name: String,
    val eventId: String,
    val sessionId: String?,
    val deviceId: String?,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun paired(eventId: String, sessionId: String, deviceId: String) = ScannerAnalyticsEvent(
            name = "scanner_paired",
            eventId = eventId,
            sessionId = sessionId,
            deviceId = deviceId
        )

        fun revoked(eventId: String, sessionId: String) = ScannerAnalyticsEvent(
            name = "scanner_revoked",
            eventId = eventId,
            sessionId = sessionId,
            deviceId = null
        )

        fun scanSuccess(eventId: String, sessionId: String, ticketId: String) = ScannerAnalyticsEvent(
            name = "scan_success",
            eventId = eventId,
            sessionId = sessionId,
            deviceId = null
        )

        fun scanInvalid(eventId: String, sessionId: String, reason: String) = ScannerAnalyticsEvent(
            name = "scan_invalid_$reason",
            eventId = eventId,
            sessionId = sessionId,
            deviceId = null
        )

        fun sessionExpired(eventId: String, sessionId: String) = ScannerAnalyticsEvent(
            name = "session_expired",
            eventId = eventId,
            sessionId = sessionId,
            deviceId = null
        )
    }
}

// MARK: - Scanner Session Service Implementation

@Singleton
class ScannerSessionService @Inject constructor() : ScannerSessionServiceProtocol {

    // MARK: - State

    private val _currentSession = MutableStateFlow<ScannerSession?>(null)
    val currentSessionFlow: StateFlow<ScannerSession?> = _currentSession.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnectedFlow: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _connectedEventTitle = MutableStateFlow<String?>(null)
    val connectedEventTitleFlow: StateFlow<String?> = _connectedEventTitle.asStateFlow()

    // MARK: - Private State

    private val pairingSessions = mutableMapOf<String, PairingSession>()
    private val scannerSessions = mutableMapOf<String, ScannerSession>()
    private val devices = mutableMapOf<String, ScannerDevice>()

    init {
        loadMockData()
    }

    // MARK: - Pairing Session Management (Organizer Side)

    /**
     * Creates a new pairing session for an event.
     * CRITICAL: Pairing sessions expire after 5 minutes.
     */
    override suspend fun createPairingSession(eventId: String, organizerId: String): PairingSession {
        // Simulate network delay
        delay(300)

        val session = PairingSession(
            eventId = eventId,
            organizerId = organizerId
        )

        pairingSessions[session.id] = session

        // Track analytics
        trackAnalytics(ScannerAnalyticsEvent(
            name = "pairing_session_created",
            eventId = eventId,
            sessionId = session.id,
            deviceId = null
        ))

        return session
    }

    /**
     * Cancels an active pairing session.
     */
    override suspend fun cancelPairingSession(pairingId: String) {
        pairingSessions.remove(pairingId)
    }

    // MARK: - Scanner Connection (Scanner Phone Side)

    /**
     * Connects a device using QR code data.
     * CRITICAL: Validates pairing session before creating scanner session.
     */
    override suspend fun connectWithQR(qrData: String, deviceId: String, deviceName: String): ScannerSession {
        // Parse QR data: eventpass://pair?session={sessionId}&event={eventId}
        val uri = try {
            URI(qrData)
        } catch (e: Exception) {
            throw ScannerError.InvalidQRCode
        }

        if (uri.scheme != "eventpass" || uri.host != "pair") {
            throw ScannerError.InvalidQRCode
        }

        val queryParams = uri.query?.split("&")?.associate {
            val (key, value) = it.split("=")
            key to value
        } ?: throw ScannerError.InvalidQRCode

        val sessionId = queryParams["session"] ?: throw ScannerError.InvalidQRCode
        val eventId = queryParams["event"] ?: throw ScannerError.InvalidQRCode

        return connectWithPairingSession(
            sessionId = sessionId,
            eventId = eventId,
            deviceId = deviceId,
            deviceName = deviceName
        )
    }

    /**
     * Connects a device using numeric pairing code.
     */
    override suspend fun connectWithCode(code: String, deviceId: String, deviceName: String): ScannerSession {
        // Find pairing session with matching code
        val pairingSession = pairingSessions.values.find { it.pairingCode == code && it.isValid }
            ?: throw ScannerError.InvalidPairingCode

        return connectWithPairingSession(
            sessionId = pairingSession.id,
            eventId = pairingSession.eventId,
            deviceId = deviceId,
            deviceName = deviceName
        )
    }

    /**
     * Internal method to complete pairing.
     */
    private suspend fun connectWithPairingSession(
        sessionId: String,
        eventId: String,
        deviceId: String,
        deviceName: String
    ): ScannerSession {
        // Simulate network delay
        delay(500)

        // Validate pairing session exists and is valid
        val pairingSession = pairingSessions[sessionId]
            ?: throw ScannerError.PairingSessionNotFound

        if (!pairingSession.isValid) {
            throw ScannerError.PairingSessionExpired
        }

        // Mark pairing session as used
        val updatedPairing = pairingSession.copy(
            usedAt = LocalDateTime.now(),
            usedByDeviceId = deviceId
        )
        pairingSessions[sessionId] = updatedPairing

        // Register or update device
        registerDevice(deviceId, deviceName)

        // Create scanner session - expires when event ends
        // For mock, we set expiry to 8 hours from now
        val scannerSession = ScannerSession(
            eventId = eventId,
            organizerId = pairingSession.organizerId,
            deviceId = deviceId,
            status = ScannerSessionStatus.ACTIVE,
            pairedAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusHours(8),
            scanCount = 0
        )

        scannerSessions[scannerSession.id] = scannerSession

        // Update current session state
        _currentSession.value = scannerSession
        _isConnected.value = true
        _connectedEventTitle.value = "Sample Event" // In production, fetch from event service

        // Track analytics
        trackAnalytics(ScannerAnalyticsEvent.paired(eventId, scannerSession.id, deviceId))

        return scannerSession
    }

    /**
     * Registers or updates a scanner device.
     */
    private fun registerDevice(deviceId: String, deviceName: String): ScannerDevice {
        val existingDevice = devices[deviceId]
        if (existingDevice != null) {
            val updated = existingDevice.copy(lastActiveAt = LocalDateTime.now())
            devices[deviceId] = updated
            return updated
        }

        val newDevice = ScannerDevice(
            deviceId = deviceId,
            deviceName = deviceName,
            platform = ScannerDevice.DevicePlatform.ANDROID
        )
        devices[deviceId] = newDevice
        return newDevice
    }

    // MARK: - Session Management

    /**
     * Gets all active scanner sessions for an event.
     */
    override suspend fun getActiveSessions(eventId: String): List<ScannerSession> {
        delay(200)

        return scannerSessions.values
            .filter { it.eventId == eventId && it.status == ScannerSessionStatus.ACTIVE }
            .sortedByDescending { it.pairedAt }
    }

    /**
     * Gets connected scanners with device info for an event.
     */
    override suspend fun getConnectedScanners(eventId: String): List<ConnectedScanner> {
        delay(200)

        val sessions = scannerSessions.values.filter { it.eventId == eventId }

        return sessions.mapNotNull { session ->
            val device = devices[session.deviceId] ?: return@mapNotNull null
            ConnectedScanner(id = session.id, device = device, session = session)
        }.sortedByDescending { it.session.pairedAt }
    }

    /**
     * Revokes a scanner session.
     * CRITICAL: Revocation is instant and permanent.
     */
    override suspend fun revokeSession(sessionId: String, organizerId: String) {
        delay(300)

        val session = scannerSessions[sessionId]
            ?: throw ScannerError.SessionNotFound

        // Verify organizer owns this session
        if (session.organizerId != organizerId) {
            throw ScannerError.Unauthorized
        }

        // Revoke the session
        val revoked = session.copy(
            status = ScannerSessionStatus.REVOKED,
            revokedAt = LocalDateTime.now(),
            revokedBy = organizerId
        )
        scannerSessions[sessionId] = revoked

        // If this was the current session, clear it
        if (_currentSession.value?.id == sessionId) {
            _currentSession.value = null
            _isConnected.value = false
            _connectedEventTitle.value = null
        }

        // Track analytics
        trackAnalytics(ScannerAnalyticsEvent.revoked(session.eventId, sessionId))
    }

    /**
     * Revokes all scanner sessions for an event.
     */
    override suspend fun revokeAllSessions(eventId: String, organizerId: String) {
        val sessions = scannerSessions.values.filter {
            it.eventId == eventId && it.status == ScannerSessionStatus.ACTIVE
        }

        for (session in sessions) {
            revokeSession(session.id, organizerId)
        }
    }

    /**
     * Renames a scanner device.
     */
    override suspend fun renameDevice(deviceId: String, newName: String) {
        delay(200)

        val device = devices[deviceId]
            ?: throw ScannerError.DeviceNotFound

        val renamed = device.copy(deviceName = newName)
        devices[deviceId] = renamed
    }

    // MARK: - Scanning

    /**
     * Validates a ticket scan request.
     * CRITICAL: Validates session, event match, and ticket status.
     */
    override suspend fun validateScan(request: ScanRequest): ScanResult {
        delay(400)

        // 1. Validate scanner session
        val session = scannerSessions[request.scannerSessionId]
        if (session == null) {
            return ScanResult(
                ticketId = UUID.randomUUID().toString(),
                status = ScanResultStatus.SESSION_INVALID,
                message = "Scanner session not found"
            )
        }

        if (!session.isValid) {
            return ScanResult(
                ticketId = UUID.randomUUID().toString(),
                status = ScanResultStatus.SESSION_INVALID,
                message = "Scanner session has expired or been revoked"
            )
        }

        // 2. Validate event match
        if (session.eventId != request.eventId) {
            trackAnalytics(ScannerAnalyticsEvent.scanInvalid(request.eventId, session.id, "wrong_event"))
            return ScanResult(
                ticketId = UUID.randomUUID().toString(),
                status = ScanResultStatus.WRONG_EVENT,
                message = "This ticket is for a different event"
            )
        }

        // 3. Parse and validate ticket QR
        val ticketId = parseTicketQR(request.ticketQR)
        if (ticketId == null) {
            trackAnalytics(ScannerAnalyticsEvent.scanInvalid(request.eventId, session.id, "invalid_ticket"))
            return ScanResult(
                ticketId = UUID.randomUUID().toString(),
                status = ScanResultStatus.INVALID_TICKET,
                message = "Invalid ticket QR code"
            )
        }

        // 4. Validate ticket (mock implementation)
        val result = validateTicket(ticketId, request.eventId, session)

        // 5. Update session stats
        if (result.isSuccess) {
            val updatedSession = session.copy(
                scanCount = session.scanCount + 1,
                lastScanAt = LocalDateTime.now()
            )
            scannerSessions[session.id] = updatedSession

            if (_currentSession.value?.id == session.id) {
                _currentSession.value = updatedSession
            }

            trackAnalytics(ScannerAnalyticsEvent.scanSuccess(request.eventId, session.id, ticketId))
        }

        // Update device last active
        devices[request.deviceId]?.let { device ->
            devices[request.deviceId] = device.copy(lastActiveAt = LocalDateTime.now())
        }

        return result
    }

    /**
     * Parses ticket QR code data.
     */
    private fun parseTicketQR(qrData: String): String? {
        // Format: eventpass://ticket?id={ticketId}&event={eventId}
        val uri = try {
            URI(qrData)
        } catch (e: Exception) {
            return null
        }

        if (uri.scheme != "eventpass" || uri.host != "ticket") {
            return null
        }

        val queryParams = uri.query?.split("&")?.associate {
            val parts = it.split("=")
            if (parts.size == 2) parts[0] to parts[1] else null to null
        }?.filterKeys { it != null }?.mapKeys { it.key!! } ?: return null

        return queryParams["id"]
    }

    /**
     * Validates a ticket (mock implementation).
     */
    private fun validateTicket(ticketId: String, eventId: String, session: ScannerSession): ScanResult {
        // Mock validation - in production, this would call the ticket service

        // Simulate different scenarios based on random
        val scenario = (0..10).random()

        return when (scenario) {
            0 -> ScanResult(
                ticketId = ticketId,
                status = ScanResultStatus.ALREADY_USED,
                attendeeName = "John Mukasa",
                ticketType = "VIP",
                message = "Scanned at 2:30 PM"
            )
            1 -> ScanResult(
                ticketId = ticketId,
                status = ScanResultStatus.REFUNDED,
                message = "This ticket has been refunded"
            )
            else -> {
                // Most scans succeed
                val names = listOf("Sarah Nakamya", "David Ochieng", "Grace Atwine", "Peter Ssempala", "Mary Kirabo")
                val types = listOf("General Admission", "VIP", "VVIP", "Early Bird")
                ScanResult(
                    ticketId = ticketId,
                    status = ScanResultStatus.VALID,
                    attendeeName = names.random(),
                    ticketType = types.random(),
                    message = "Welcome to the event!"
                )
            }
        }
    }

    // MARK: - Session State

    /**
     * Returns the current scanner session.
     */
    override fun getCurrentSession(): ScannerSession? {
        return _currentSession.value
    }

    /**
     * Clears the current session (logout).
     */
    override fun clearCurrentSession() {
        _currentSession.value = null
        _isConnected.value = false
        _connectedEventTitle.value = null
    }

    /**
     * Refreshes the current session status.
     */
    override suspend fun refreshSession(): ScannerSession? {
        val session = _currentSession.value ?: return null

        delay(300)

        // Check if session is still valid
        val updatedSession = scannerSessions[session.id]
        if (updatedSession != null && updatedSession.isValid) {
            _currentSession.value = updatedSession
            return updatedSession
        }

        // Session expired or revoked
        clearCurrentSession()
        return null
    }

    // MARK: - Expiry Management

    /**
     * Expires all sessions for an event that has ended.
     */
    override suspend fun expireSessionsForEndedEvent(eventId: String) {
        var expiredCount = 0

        scannerSessions.forEach { (id, session) ->
            if (session.eventId == eventId && session.status == ScannerSessionStatus.ACTIVE) {
                scannerSessions[id] = session.copy(status = ScannerSessionStatus.EXPIRED)
                expiredCount++
                trackAnalytics(ScannerAnalyticsEvent.sessionExpired(eventId, id))
            }
        }

        Log.d("ScannerSessionService", "Expired $expiredCount sessions for event $eventId")
    }

    // MARK: - Analytics

    private fun trackAnalytics(event: ScannerAnalyticsEvent) {
        Log.d("ScannerAnalytics", "${event.name} - eventId: ${event.eventId}, sessionId: ${event.sessionId}")
    }

    // MARK: - Mock Data

    private fun loadMockData() {
        // Load mock devices
        ScannerDevice.mockDevices.forEach { device ->
            devices[device.deviceId] = device
        }

        // Load mock sessions
        ScannerSession.mockSessions.forEach { session ->
            scannerSessions[session.id] = session
        }
    }
}

// MARK: - Mock Data Extensions

private val ScannerDevice.Companion.mockDevices: List<ScannerDevice>
    get() = listOf(
        ScannerDevice(
            deviceId = "mock-device-1",
            deviceName = "Organizer's Phone",
            platform = ScannerDevice.DevicePlatform.ANDROID
        ),
        ScannerDevice(
            deviceId = "mock-device-2",
            deviceName = "Staff Scanner",
            platform = ScannerDevice.DevicePlatform.ANDROID
        )
    )

private val ScannerSession.Companion.mockSessions: List<ScannerSession>
    get() = emptyList()
