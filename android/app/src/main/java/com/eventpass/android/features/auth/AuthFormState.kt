package com.eventpass.android.features.auth

/**
 * Form state for authentication screens.
 * Handles input values and validation errors.
 *
 * SwiftUI → Compose mapping:
 * - @Published var email = "" → MutableStateFlow<AuthFormState>
 * - var emailError: String? → Included in data class
 */
data class AuthFormState(
    // Input fields
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phoneNumber: String = "",
    val otpCode: String = "",

    // Validation errors
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val phoneError: String? = null,
    val otpError: String? = null
) {
    /**
     * Check if login form is valid.
     */
    val isLoginValid: Boolean
        get() = isValidEmail(email) && password.length >= 6

    /**
     * Check if register form is valid.
     */
    val isRegisterValid: Boolean
        get() = fullName.isNotBlank() &&
                isValidEmail(email) &&
                password.length >= 6 &&
                password == confirmPassword

    /**
     * Check if phone number is valid.
     */
    val isPhoneValid: Boolean
        get() = isValidPhone(phoneNumber)

    /**
     * Check if OTP code is valid.
     */
    val isOtpValid: Boolean
        get() = otpCode.length == 6 && otpCode.all { it.isDigit() }

    /**
     * Validate email format.
     */
    fun validateEmail(): AuthFormState {
        val error = when {
            email.isBlank() -> null
            !isValidEmail(email) -> "Invalid email format"
            else -> null
        }
        return copy(emailError = error)
    }

    /**
     * Validate password.
     */
    fun validatePassword(): AuthFormState {
        val error = when {
            password.isBlank() -> null
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        return copy(passwordError = error)
    }

    /**
     * Validate password confirmation.
     */
    fun validateConfirmPassword(): AuthFormState {
        val error = when {
            confirmPassword.isBlank() -> null
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
        return copy(confirmPasswordError = error)
    }

    /**
     * Validate full name.
     */
    fun validateFullName(): AuthFormState {
        val error = when {
            fullName.isBlank() -> "Full name is required"
            else -> null
        }
        return copy(fullNameError = error)
    }

    /**
     * Validate phone number.
     */
    fun validatePhone(): AuthFormState {
        val error = when {
            phoneNumber.isBlank() -> null
            !isValidPhone(phoneNumber) -> "Invalid phone number"
            else -> null
        }
        return copy(phoneError = error)
    }

    /**
     * Validate all login fields.
     */
    fun validateLogin(): AuthFormState {
        return validateEmail().validatePassword()
    }

    /**
     * Validate all register fields.
     */
    fun validateRegister(): AuthFormState {
        return validateFullName()
            .validateEmail()
            .validatePassword()
            .validateConfirmPassword()
    }

    /**
     * Check if there are any validation errors.
     */
    val hasErrors: Boolean
        get() = listOfNotNull(
            fullNameError,
            emailError,
            passwordError,
            confirmPasswordError,
            phoneError,
            otpError
        ).isNotEmpty()

    /**
     * Clear all fields and errors.
     */
    fun clear(): AuthFormState = AuthFormState()

    companion object {
        private val EMAIL_REGEX = Regex(
            "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        )

        fun isValidEmail(email: String): Boolean {
            return EMAIL_REGEX.matches(email)
        }

        fun isValidPhone(phone: String): Boolean {
            val cleaned = phone.filter { it.isDigit() || it == '+' }
            return cleaned.length in 10..15
        }
    }
}

/**
 * Sealed class for form events.
 */
sealed class AuthFormEvent {
    data class FullNameChanged(val value: String) : AuthFormEvent()
    data class EmailChanged(val value: String) : AuthFormEvent()
    data class PasswordChanged(val value: String) : AuthFormEvent()
    data class ConfirmPasswordChanged(val value: String) : AuthFormEvent()
    data class PhoneChanged(val value: String) : AuthFormEvent()
    data class OtpChanged(val value: String) : AuthFormEvent()
    data object Submit : AuthFormEvent()
    data object ClearErrors : AuthFormEvent()
}
