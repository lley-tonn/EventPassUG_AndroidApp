package com.eventpass.android.features.cancellation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.data.repository.CancellationProgress
import com.eventpass.android.data.repository.CancellationRepository
import com.eventpass.android.domain.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * EventCancellationViewModel.
 * Migrated from iOS Features/Cancellation/EventCancellationViewModel.swift
 *
 * ViewModel for the multi-step event cancellation flow.
 */
@HiltViewModel
class EventCancellationViewModel @Inject constructor(
    private val cancellationRepository: CancellationRepository
) : ViewModel() {

    // MARK: - State

    private val _currentStep = MutableStateFlow(CancellationStep.REASON)
    val currentStep: StateFlow<CancellationStep> = _currentStep.asStateFlow()

    private val _selectedReason = MutableStateFlow<CancellationReason?>(null)
    val selectedReason: StateFlow<CancellationReason?> = _selectedReason.asStateFlow()

    private val _reasonNote = MutableStateFlow("")
    val reasonNote: StateFlow<String> = _reasonNote.asStateFlow()

    private val _selectedCompensationType = MutableStateFlow(CompensationType.FULL_REFUND)
    val selectedCompensationType: StateFlow<CompensationType> = _selectedCompensationType.asStateFlow()

    private val _refundPercentage = MutableStateFlow(1.0)
    val refundPercentage: StateFlow<Double> = _refundPercentage.asStateFlow()

    // Loading/Processing
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _processingProgress = MutableStateFlow(0.0)
    val processingProgress: StateFlow<Double> = _processingProgress.asStateFlow()

    private val _processingMessage = MutableStateFlow("")
    val processingMessage: StateFlow<String> = _processingMessage.asStateFlow()

    // Results
    private val _cancellation = MutableStateFlow<EventCancellation?>(null)
    val cancellation: StateFlow<EventCancellation?> = _cancellation.asStateFlow()

    private val _impact = MutableStateFlow<CancellationImpact?>(null)
    val impact: StateFlow<CancellationImpact?> = _impact.asStateFlow()

    private val _notificationPreview = MutableStateFlow<NotificationPreview?>(null)
    val notificationPreview: StateFlow<NotificationPreview?> = _notificationPreview.asStateFlow()

    private val _completedCancellation = MutableStateFlow<EventCancellation?>(null)
    val completedCancellation: StateFlow<EventCancellation?> = _completedCancellation.asStateFlow()

    // Errors
    private val _showError = MutableStateFlow(false)
    val showError: StateFlow<Boolean> = _showError.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Event being cancelled
    private var event: Event? = null

    init {
        // Observe cancellation status updates
        viewModelScope.launch {
            cancellationRepository.cancellationStatusFlow.collect { updatedCancellation ->
                _cancellation.value = updatedCancellation
            }
        }

        // Observe processing progress
        viewModelScope.launch {
            cancellationRepository.processingProgressFlow.collect { progress ->
                _processingProgress.value = progress.progress
                _processingMessage.value = progress.message
            }
        }
    }

    // MARK: - Initialization

    fun initialize(event: Event) {
        this.event = event
    }

    // MARK: - Actions

    fun selectReason(reason: CancellationReason) {
        _selectedReason.value = reason
    }

    fun updateReasonNote(note: String) {
        _reasonNote.value = note
    }

    fun selectCompensationType(type: CompensationType) {
        _selectedCompensationType.value = type
    }

    fun updateRefundPercentage(percentage: Double) {
        _refundPercentage.value = percentage.coerceIn(0.0, 1.0)
    }

    // MARK: - Navigation

    fun proceed() {
        val currentStepValue = _currentStep.value
        val steps = CancellationStep.entries

        when (currentStepValue) {
            CancellationStep.REASON -> {
                if (_selectedReason.value != null) {
                    calculateImpact()
                    _currentStep.value = CancellationStep.IMPACT
                }
            }
            CancellationStep.IMPACT -> {
                _currentStep.value = CancellationStep.COMPENSATION
            }
            CancellationStep.COMPENSATION -> {
                updateNotificationPreview()
                _currentStep.value = CancellationStep.NOTIFICATION
            }
            CancellationStep.NOTIFICATION -> {
                _currentStep.value = CancellationStep.FINANCIAL
            }
            CancellationStep.FINANCIAL -> {
                _currentStep.value = CancellationStep.CONFIRMATION
            }
            CancellationStep.CONFIRMATION -> {
                // Final step - handled by confirm button
            }
        }
    }

    fun goBack() {
        val steps = CancellationStep.entries
        val currentIndex = steps.indexOf(_currentStep.value)
        if (currentIndex > 0) {
            _currentStep.value = steps[currentIndex - 1]
        }
    }

    // MARK: - Impact Calculation

    private fun calculateImpact() {
        val eventValue = event ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val impactResult = cancellationRepository.calculateImpact(eventValue.id)
                _impact.value = impactResult
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MARK: - Notification Preview

    private fun updateNotificationPreview() {
        val cancellationValue = _cancellation.value ?: return
        _notificationPreview.value = cancellationRepository.previewNotification(cancellationValue)
    }

    // MARK: - Create Cancellation

    fun createCancellation(initiatedBy: String) {
        val eventValue = event ?: return
        val reason = _selectedReason.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val newCancellation = cancellationRepository.createCancellation(
                    event = eventValue,
                    reason = reason,
                    note = _reasonNote.value.takeIf { it.isNotBlank() },
                    initiatedBy = initiatedBy
                )
                _cancellation.value = newCancellation
                _impact.value = newCancellation.impact
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MARK: - Confirmation

    fun confirmCancellation(confirmationCode: String, confirmedBy: String) {
        val cancellationValue = _cancellation.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val confirmed = cancellationRepository.confirmCancellation(
                    cancellationId = cancellationValue.id,
                    confirmationCode = confirmationCode,
                    confirmedBy = confirmedBy
                )
                _cancellation.value = confirmed
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MARK: - Processing

    fun startProcessing() {
        val cancellationValue = _cancellation.value ?: return

        viewModelScope.launch {
            _isProcessing.value = true
            _processingProgress.value = 0.0
            _processingMessage.value = "Starting cancellation process..."
            _errorMessage.value = null

            try {
                val completed = cancellationRepository.processCancellation(cancellationValue.id)
                _completedCancellation.value = completed
                _cancellation.value = completed
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isProcessing.value = false
            }
        }
    }

    // MARK: - Retry Failed Refunds

    fun retryFailedRefunds() {
        val cancellationValue = _cancellation.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val updated = cancellationRepository.retryFailedRefunds(cancellationValue.id)
                _cancellation.value = updated
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MARK: - Error Handling

    fun dismissError() {
        _showError.value = false
        _errorMessage.value = null
    }

    // MARK: - Computed Properties

    val canProceed: Boolean
        get() = when (_currentStep.value) {
            CancellationStep.REASON -> _selectedReason.value != null
            CancellationStep.IMPACT -> _impact.value != null
            CancellationStep.COMPENSATION -> true
            CancellationStep.NOTIFICATION -> true
            CancellationStep.FINANCIAL -> true
            CancellationStep.CONFIRMATION -> true
        }

    val stepProgress: Float
        get() = (_currentStep.value.stepNumber.toFloat() / CancellationStep.totalSteps.toFloat())
}
