package com.eventpass.android.features.common.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.data.repository.AuthRepository
import com.eventpass.android.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Shared model for the profile edit / contact-verification sheets (Edit
 * Profile, Change Email, Add Phone, Email/Phone verification, National ID).
 * Exposes the signed-in [User] and mock verification actions that mutate it so
 * the Become-an-Organizer flow can be completed end-to-end.
 */
@HiltViewModel
class ProfileContactViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun verifyEmail() {
        viewModelScope.launch { authRepository.verifyEmail() }
    }

    fun changeEmail(newEmail: String) {
        viewModelScope.launch { authRepository.changeEmail(newEmail) }
    }

    fun addPhone(phoneNumber: String) {
        viewModelScope.launch { authRepository.addPhoneNumber(phoneNumber) }
    }

    fun verifyPhone() {
        viewModelScope.launch { authRepository.verifyPhoneNumber() }
    }

    fun submitIdentity(documentNumber: String) {
        viewModelScope.launch { authRepository.submitIdentityVerification(documentNumber) }
    }
}
