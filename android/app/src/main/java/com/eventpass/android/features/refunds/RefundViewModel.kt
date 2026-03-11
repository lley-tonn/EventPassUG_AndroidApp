package com.eventpass.android.features.refunds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.data.repository.RefundAnalytics
import com.eventpass.android.data.repository.RefundRepository
import com.eventpass.android.domain.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * RefundViewModel.
 * Migrated from iOS pattern for refund management.
 *
 * ViewModel for refund request and management.
 */
@HiltViewModel
class RefundViewModel @Inject constructor(
    private val refundRepository: RefundRepository
) : ViewModel() {

    // MARK: - State

    private val _selectedTicket = MutableStateFlow<Ticket?>(null)
    val selectedTicket: StateFlow<Ticket?> = _selectedTicket.asStateFlow()

    private val _eligibility = MutableStateFlow<RefundEligibilityResult?>(null)
    val eligibility: StateFlow<RefundEligibilityResult?> = _eligibility.asStateFlow()

    private val _selectedReason = MutableStateFlow<RefundReason?>(null)
    val selectedReason: StateFlow<RefundReason?> = _selectedReason.asStateFlow()

    private val _userNote = MutableStateFlow("")
    val userNote: StateFlow<String> = _userNote.asStateFlow()

    private val _refundRequest = MutableStateFlow<RefundRequest?>(null)
    val refundRequest: StateFlow<RefundRequest?> = _refundRequest.asStateFlow()

    // Loading states
    private val _isCheckingEligibility = MutableStateFlow(false)
    val isCheckingEligibility: StateFlow<Boolean> = _isCheckingEligibility.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Lists
    private val _userRefundRequests = MutableStateFlow<List<RefundRequest>>(emptyList())
    val userRefundRequests: StateFlow<List<RefundRequest>> = _userRefundRequests.asStateFlow()

    private val _eventRefundRequests = MutableStateFlow<List<RefundRequest>>(emptyList())
    val eventRefundRequests: StateFlow<List<RefundRequest>> = _eventRefundRequests.asStateFlow()

    // Errors
    private val _showError = MutableStateFlow(false)
    val showError: StateFlow<Boolean> = _showError.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Success
    private val _showSuccess = MutableStateFlow(false)
    val showSuccess: StateFlow<Boolean> = _showSuccess.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        // Observe refund status updates
        viewModelScope.launch {
            refundRepository.refundStatusFlow.collect { updatedRequest ->
                _refundRequest.value = updatedRequest
                // Refresh user requests list if needed
                _userRefundRequests.value = _userRefundRequests.value.map { request ->
                    if (request.id == updatedRequest.id) updatedRequest else request
                }
            }
        }
    }

    // MARK: - User Actions

    fun selectTicket(ticket: Ticket) {
        _selectedTicket.value = ticket
        _eligibility.value = null
        _selectedReason.value = null
        _userNote.value = ""
    }

    fun selectReason(reason: RefundReason) {
        _selectedReason.value = reason
    }

    fun updateUserNote(note: String) {
        _userNote.value = note
    }

    // MARK: - Check Eligibility

    fun checkEligibility(ticket: Ticket, event: Event) {
        viewModelScope.launch {
            _isCheckingEligibility.value = true
            _errorMessage.value = null

            try {
                val result = refundRepository.checkEligibility(ticket, event)
                _eligibility.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isCheckingEligibility.value = false
            }
        }
    }

    // MARK: - Request Refund

    fun requestRefund() {
        val ticket = _selectedTicket.value ?: return
        val reason = _selectedReason.value ?: return

        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            try {
                val request = refundRepository.requestRefund(
                    ticket = ticket,
                    reason = reason,
                    note = _userNote.value.takeIf { it.isNotBlank() }
                )
                _refundRequest.value = request
                _successMessage.value = "Refund request submitted successfully"
                _showSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    // MARK: - Cancel Request

    fun cancelRefundRequest(requestId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                refundRepository.cancelRefundRequest(requestId)
                _successMessage.value = "Refund request cancelled"
                _showSuccess.value = true
                // Refresh list
                loadUserRefundRequests(_userRefundRequests.value.firstOrNull()?.userId ?: return@launch)
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MARK: - Load User Requests

    fun loadUserRefundRequests(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val requests = refundRepository.getUserRefundRequests(userId)
                _userRefundRequests.value = requests
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MARK: - Load Event Requests (Organizer)

    fun loadEventRefundRequests(eventId: String, status: RefundStatus? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val requests = refundRepository.getEventRefundRequests(eventId, status)
                _eventRefundRequests.value = requests
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MARK: - Organizer Actions

    fun approveRefund(requestId: String, approvedAmount: Double? = null, note: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val updated = refundRepository.approveRefund(requestId, approvedAmount, note)
                _refundRequest.value = updated
                _successMessage.value = "Refund approved"
                _showSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rejectRefund(requestId: String, note: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val updated = refundRepository.rejectRefund(requestId, note)
                _refundRequest.value = updated
                _successMessage.value = "Refund rejected"
                _showSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _showError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MARK: - Error/Success Handling

    fun dismissError() {
        _showError.value = false
        _errorMessage.value = null
    }

    fun dismissSuccess() {
        _showSuccess.value = false
        _successMessage.value = null
    }

    // MARK: - Computed Properties

    val canSubmitRequest: Boolean
        get() = _selectedTicket.value != null &&
                _selectedReason.value != null &&
                _eligibility.value?.isEligible == true

    val availableReasons: List<RefundReason>
        get() = RefundReason.userSelectableReasons
}
