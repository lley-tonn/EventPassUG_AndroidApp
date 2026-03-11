package com.eventpass.android.features.organizer.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.core.state.EventChannel
import com.eventpass.android.core.state.UiState
import com.eventpass.android.domain.models.ScanResult
import com.eventpass.android.domain.models.ScanResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Local scanner session state for the ViewModel.
 * Tracks scanning statistics during an active scanning session.
 */
data class LocalScannerSession(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String,
    val organizerId: String,
    val startTime: LocalDateTime = LocalDateTime.now(),
    val endTime: LocalDateTime? = null,
    val isActive: Boolean = true,
    val totalScans: Int = 0,
    val successfulScans: Int = 0,
    val failedScans: Int = 0,
    val scanRate: Double = 0.0
)

/**
 * ViewModel for QR Code Scanner Screen.
 * Handles ticket scanning, validation, and session management.
 */
@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {

    // MARK: - State

    private val _scannerState = MutableStateFlow<UiState<LocalScannerSession>>(UiState.Idle)
    val scannerState: StateFlow<UiState<LocalScannerSession>> = _scannerState.asStateFlow()

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private val _lastScanResult = MutableStateFlow<ScanResult?>(null)
    val lastScanResult: StateFlow<ScanResult?> = _lastScanResult.asStateFlow()

    // MARK: - Events

    private val _scanEvents = EventChannel<ScannerEvent>()
    val scanEvents = _scanEvents.events

    // MARK: - Actions

    /**
     * Start scanner session for an event.
     */
    fun startSession(eventId: String) {
        viewModelScope.launch {
            _scannerState.value = UiState.Loading

            // Create mock session
            delay(500)
            val session = LocalScannerSession(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                organizerId = "current-organizer-id",
                startTime = LocalDateTime.now(),
                endTime = null,
                isActive = true,
                totalScans = 0,
                successfulScans = 0,
                failedScans = 0,
                scanRate = 0.0
            )

            _scannerState.value = UiState.Success(session)
            _uiState.update { it.copy(isScanning = true) }
        }
    }

    /**
     * Process scanned QR code.
     */
    fun processQRCode(qrData: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            // Simulate validation delay
            delay(300)

            // Parse and validate ticket
            val result = validateTicket(qrData)
            _lastScanResult.value = result

            // Update session stats
            updateSessionStats(result)

            // Send event for UI feedback
            when (result.status) {
                ScanResultStatus.VALID -> {
                    _scanEvents.send(ScannerEvent.ValidTicket(result))
                }
                ScanResultStatus.ALREADY_USED -> {
                    _scanEvents.send(ScannerEvent.AlreadyScanned(result))
                }
                ScanResultStatus.INVALID_TICKET -> {
                    _scanEvents.send(ScannerEvent.InvalidTicket(result))
                }
                ScanResultStatus.EXPIRED -> {
                    _scanEvents.send(ScannerEvent.ExpiredTicket(result))
                }
                ScanResultStatus.WRONG_EVENT -> {
                    _scanEvents.send(ScannerEvent.WrongEvent(result))
                }
                ScanResultStatus.REFUNDED, ScanResultStatus.SESSION_INVALID -> {
                    _scanEvents.send(ScannerEvent.ScanError(result.message))
                }
            }

            // Add to scan history
            _uiState.update { state ->
                state.copy(
                    isProcessing = false,
                    scanHistory = listOf(result) + state.scanHistory.take(49)
                )
            }
        }
    }

    /**
     * Toggle flashlight.
     */
    fun toggleFlashlight() {
        _uiState.update { it.copy(isFlashlightOn = !it.isFlashlightOn) }
    }

    /**
     * Pause scanning.
     */
    fun pauseScanning() {
        _uiState.update { it.copy(isScanning = false) }
    }

    /**
     * Resume scanning.
     */
    fun resumeScanning() {
        _uiState.update { it.copy(isScanning = true) }
    }

    /**
     * End scanner session.
     */
    fun endSession() {
        viewModelScope.launch {
            val currentSession = (_scannerState.value as? UiState.Success)?.data
            if (currentSession != null) {
                val endedSession = currentSession.copy(
                    endTime = LocalDateTime.now(),
                    isActive = false
                )
                _scannerState.value = UiState.Success(endedSession)
            }
            _uiState.update { it.copy(isScanning = false) }
        }
    }

    /**
     * Clear last scan result.
     */
    fun clearLastResult() {
        _lastScanResult.value = null
    }

    // MARK: - Private Methods

    private fun validateTicket(qrData: String): ScanResult {
        // Parse ticket data from QR code
        // Format: eventpass://ticket/{ticketId}?eventId={eventId}

        val ticketId = extractTicketId(qrData)
        if (ticketId == null) {
            return ScanResult(
                ticketId = "",
                status = ScanResultStatus.INVALID_TICKET,
                message = "Invalid QR code format",
                scannedAt = LocalDateTime.now()
            )
        }

        // Simulate various validation scenarios
        val random = (0..100).random()
        return when {
            random < 80 -> ScanResult(
                ticketId = ticketId,
                status = ScanResultStatus.VALID,
                message = "Ticket validated successfully",
                attendeeName = "John Doe",
                ticketType = "Regular",
                scannedAt = LocalDateTime.now()
            )
            random < 90 -> ScanResult(
                ticketId = ticketId,
                status = ScanResultStatus.ALREADY_USED,
                message = "Ticket was already scanned",
                scannedAt = LocalDateTime.now()
            )
            else -> ScanResult(
                ticketId = ticketId,
                status = ScanResultStatus.INVALID_TICKET,
                message = "Ticket not found",
                scannedAt = LocalDateTime.now()
            )
        }
    }

    private fun extractTicketId(qrData: String): String? {
        return try {
            if (qrData.startsWith("eventpass://ticket/")) {
                qrData.removePrefix("eventpass://ticket/").split("?").firstOrNull()
            } else {
                qrData // Assume raw ticket ID
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun updateSessionStats(result: ScanResult) {
        val currentSession = (_scannerState.value as? UiState.Success)?.data ?: return

        val updatedSession = currentSession.copy(
            totalScans = currentSession.totalScans + 1,
            successfulScans = if (result.status == ScanResultStatus.VALID) {
                currentSession.successfulScans + 1
            } else {
                currentSession.successfulScans
            },
            failedScans = if (result.status != ScanResultStatus.VALID) {
                currentSession.failedScans + 1
            } else {
                currentSession.failedScans
            }
        )

        _scannerState.value = UiState.Success(updatedSession)
    }
}

/**
 * UI State for Scanner Screen.
 */
data class ScannerUiState(
    val isScanning: Boolean = false,
    val isProcessing: Boolean = false,
    val isFlashlightOn: Boolean = false,
    val scanHistory: List<ScanResult> = emptyList()
)

/**
 * Scanner events for haptic/visual feedback.
 */
sealed class ScannerEvent {
    data class ValidTicket(val result: ScanResult) : ScannerEvent()
    data class AlreadyScanned(val result: ScanResult) : ScannerEvent()
    data class InvalidTicket(val result: ScanResult) : ScannerEvent()
    data class ExpiredTicket(val result: ScanResult) : ScannerEvent()
    data class WrongEvent(val result: ScanResult) : ScannerEvent()
    data class ScanError(val message: String) : ScannerEvent()
}
