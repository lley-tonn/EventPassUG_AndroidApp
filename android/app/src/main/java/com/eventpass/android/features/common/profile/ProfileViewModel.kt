package com.eventpass.android.features.common.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.core.state.ActionState
import com.eventpass.android.core.state.EventChannel
import com.eventpass.android.core.state.UiState
import com.eventpass.android.data.repository.AuthRepository
import com.eventpass.android.data.repository.UserPreferencesRepository
import com.eventpass.android.domain.models.User
import com.eventpass.android.domain.models.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Profile Screen.
 * Handles user profile display, editing, and settings.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // MARK: - State

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _updateState = MutableStateFlow<ActionState>(ActionState.Idle)
    val updateState: StateFlow<ActionState> = _updateState.asStateFlow()

    // MARK: - Events

    private val _profileEvents = EventChannel<ProfileEvent>()
    val profileEvents = _profileEvents.events

    init {
        loadPreferences()
    }

    // MARK: - Actions

    /**
     * Load user preferences.
     */
    private fun loadPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.getUserPreferences().collect { prefs ->
                _uiState.update {
                    it.copy(
                        isDarkMode = prefs.isDarkMode,
                        notificationsEnabled = prefs.notificationsEnabled
                    )
                }
            }
        }
    }

    /**
     * Toggle edit mode.
     */
    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }

    /**
     * Update profile field.
     */
    fun updateField(field: ProfileField, value: String) {
        _uiState.update { state ->
            when (field) {
                ProfileField.FIRST_NAME -> state.copy(editFirstName = value)
                ProfileField.LAST_NAME -> state.copy(editLastName = value)
                ProfileField.PHONE -> state.copy(editPhone = value)
                ProfileField.CITY -> state.copy(editCity = value)
            }
        }
    }

    /**
     * Save profile changes.
     */
    fun saveProfile() {
        viewModelScope.launch {
            _updateState.value = ActionState.Loading

            // TODO: Call repository to update profile
            kotlinx.coroutines.delay(1000)

            _updateState.value = ActionState.Success
            _uiState.update { it.copy(isEditing = false) }
            _profileEvents.send(ProfileEvent.ProfileUpdated)
        }
    }

    /**
     * Cancel editing.
     */
    fun cancelEditing() {
        _uiState.update { it.copy(isEditing = false) }
    }

    /**
     * Switch user role.
     */
    fun switchRole(role: UserRole) {
        viewModelScope.launch {
            authRepository.switchRole(role)
            _profileEvents.send(ProfileEvent.RoleSwitched(role))
        }
    }

    /**
     * Toggle dark mode.
     */
    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_uiState.value.isDarkMode
            userPreferencesRepository.setDarkMode(newValue)
            _uiState.update { it.copy(isDarkMode = newValue) }
        }
    }

    /**
     * Toggle notifications.
     */
    fun toggleNotifications() {
        viewModelScope.launch {
            val newValue = !_uiState.value.notificationsEnabled
            userPreferencesRepository.setNotificationsEnabled(newValue)
            _uiState.update { it.copy(notificationsEnabled = newValue) }
        }
    }

    /**
     * Sign out.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _profileEvents.send(ProfileEvent.SignedOut)
        }
    }

    /**
     * Delete account.
     */
    fun deleteAccount() {
        viewModelScope.launch {
            _updateState.value = ActionState.Loading

            // TODO: Call repository to delete account
            kotlinx.coroutines.delay(1000)

            authRepository.signOut()
            _updateState.value = ActionState.Success
            _profileEvents.send(ProfileEvent.AccountDeleted)
        }
    }

    /**
     * Get follower count.
     */
    fun getFollowerCount(): Int {
        // TODO: Get from repository
        return 1234
    }
}

/**
 * UI State for Profile Screen.
 */
data class ProfileUiState(
    val isEditing: Boolean = false,
    val editFirstName: String = "",
    val editLastName: String = "",
    val editPhone: String = "",
    val editCity: String = "",
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val showDeleteConfirmation: Boolean = false
)

/**
 * Profile field for editing.
 */
enum class ProfileField {
    FIRST_NAME,
    LAST_NAME,
    PHONE,
    CITY
}

/**
 * Profile events.
 */
sealed class ProfileEvent {
    data object ProfileUpdated : ProfileEvent()
    data class RoleSwitched(val role: UserRole) : ProfileEvent()
    data object SignedOut : ProfileEvent()
    data object AccountDeleted : ProfileEvent()
}
