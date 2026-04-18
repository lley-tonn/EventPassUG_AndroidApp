package com.eventpass.feature.onboarding

import androidx.lifecycle.ViewModel
import com.eventpass.feature.onboarding.model.InterestCategory
import com.eventpass.feature.onboarding.model.OnboardingState
import com.eventpass.feature.onboarding.model.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

/**
 * Scoped to the onboarding nav graph — collects answers across all 6 steps
 * and emits one final [OnboardingState] when the user taps "Start Exploring".
 *
 * Later phases will inject a preferences repository to persist the result.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun setRole(role: Role) = _state.update { it.copy(role = role) }
    fun setFullName(name: String) = _state.update { it.copy(fullName = name) }
    fun setDateOfBirth(date: LocalDate) = _state.update { it.copy(dateOfBirth = date) }
    fun toggleInterest(interest: InterestCategory) = _state.update { current ->
        val next = current.interests.toMutableSet().apply {
            if (!add(interest)) remove(interest)
        }
        current.copy(interests = next)
    }
    fun setNotifications(enabled: Boolean) = _state.update { it.copy(notificationsEnabled = enabled) }
}
