package com.eventpass.android.features.common.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.data.repository.AuthRepository
import com.eventpass.android.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Shared read-model for the profile edit / contact-verification sheets
 * (Edit Profile, Change Email, Add Phone, Email/Phone verification). Exposes
 * the signed-in [User] so each sheet can prefill its fields.
 */
@HiltViewModel
class ProfileContactViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
