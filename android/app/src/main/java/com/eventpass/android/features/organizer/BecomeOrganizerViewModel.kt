package com.eventpass.android.features.organizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.core.navigation.TabNavSignals
import com.eventpass.android.data.repository.AuthRepository
import com.eventpass.android.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the Become-an-Organizer flow. Exposes the signed-in [User] so the
 * profile checklist can reflect real verification state, and completes the
 * flow by promoting the user to a verified organizer.
 */
@HiltViewModel
class BecomeOrganizerViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tabNavSignals: TabNavSignals
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Completes registration: activates the verified-organizer role and asks
     * the tab host to land on Dashboard on return. */
    fun completeOrganizerRegistration() {
        tabNavSignals.requestStartTab("dashboard")
        viewModelScope.launch { authRepository.becomeOrganizer() }
    }
}
