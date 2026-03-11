package com.eventpass.android.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.data.repository.AuthRepository
import com.eventpass.android.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Authentication ViewModel.
 * Migrated from iOS Features/Auth/AuthViewModel.swift
 *
 * SwiftUI → Compose state mapping:
 * - @Published → StateFlow
 * - @State → MutableState (in Composable) or StateFlow (in ViewModel)
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Auth state
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Current user
    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Onboarding completion status
    private val _hasCompletedOnboarding = MutableStateFlow(false)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    // OTP timer
    private val _otpTimerSeconds = MutableStateFlow(0)
    val otpTimerSeconds: StateFlow<Int> = _otpTimerSeconds.asStateFlow()

    init {
        viewModelScope.launch {
            _hasCompletedOnboarding.value = authRepository.hasCompletedOnboarding()
        }
    }

    /**
     * Sign in with email and password.
     */
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null
            )

            authRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user
                    )
                }
                .onFailure { error ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Sign up with email and password.
     */
    fun signUpWithEmail(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null
            )

            authRepository.signUpWithEmail(email, password, fullName)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user
                    )
                }
                .onFailure { error ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Send OTP to phone number.
     */
    fun sendOtp(phoneNumber: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null
            )

            authRepository.signInWithPhone(phoneNumber)
                .onSuccess {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        authMode = AuthMode.OTP,
                        phoneNumber = phoneNumber
                    )
                    startOtpTimer()
                }
                .onFailure { error ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Verify OTP code.
     */
    fun verifyOtp(code: String) {
        val phoneNumber = _authState.value.phoneNumber ?: return

        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null
            )

            authRepository.verifyOtp(phoneNumber, code)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user
                    )
                }
                .onFailure { error ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Sign in with Google.
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null
            )

            authRepository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user
                    )
                }
                .onFailure { error ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Sign out.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState()
        }
    }

    /**
     * Complete onboarding.
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            authRepository.setOnboardingCompleted()
            _hasCompletedOnboarding.value = true
        }
    }

    /**
     * Set auth mode.
     */
    fun setAuthMode(mode: AuthMode) {
        _authState.value = _authState.value.copy(authMode = mode)
    }

    /**
     * Clear error.
     */
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    private fun startOtpTimer() {
        _otpTimerSeconds.value = 60
        viewModelScope.launch {
            while (_otpTimerSeconds.value > 0) {
                kotlinx.coroutines.delay(1000)
                _otpTimerSeconds.value -= 1
            }
        }
    }
}

/**
 * Authentication state.
 */
data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val authMode: AuthMode = AuthMode.LOGIN,
    val authMethod: AuthMethod = AuthMethod.EMAIL,
    val phoneNumber: String? = null
)

/**
 * Authentication mode.
 */
enum class AuthMode {
    LOGIN,
    REGISTER,
    OTP
}

/**
 * Authentication method.
 */
enum class AuthMethod {
    EMAIL,
    PHONE,
    GOOGLE,
    APPLE,
    FACEBOOK
}
