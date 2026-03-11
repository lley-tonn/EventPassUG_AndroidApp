package com.eventpass.android.core.util

/**
 * Validation utilities.
 * Migrated from iOS Core/Utilities/Validation.swift
 */
object Validation {

    /**
     * Validate email format.
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
        )
        return email.matches(emailRegex)
    }

    /**
     * Validate password strength.
     * Requirements:
     * - At least 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     */
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }

    /**
     * Get password validation error message.
     */
    fun getPasswordError(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> "Password must contain an uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain a lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain a digit"
            else -> null
        }
    }

    /**
     * Validate phone number (Uganda format).
     * Accepts formats: +256XXXXXXXXX, 0XXXXXXXXX, 256XXXXXXXXX
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        val cleanPhone = phone.replace(Regex("[\\s\\-()]"), "")
        val ugandaRegex = Regex("^(\\+?256|0)?[37][0-9]{8}$")
        return cleanPhone.matches(ugandaRegex)
    }

    /**
     * Format phone number to international format.
     */
    fun formatPhoneNumber(phone: String): String {
        val cleanPhone = phone.replace(Regex("[\\s\\-()]"), "")
        return when {
            cleanPhone.startsWith("+256") -> cleanPhone
            cleanPhone.startsWith("256") -> "+$cleanPhone"
            cleanPhone.startsWith("0") -> "+256${cleanPhone.substring(1)}"
            else -> "+256$cleanPhone"
        }
    }

    /**
     * Validate name (at least 2 characters, letters and spaces only).
     */
    fun isValidName(name: String): Boolean {
        val trimmed = name.trim()
        return trimmed.length >= 2 && trimmed.all { it.isLetter() || it.isWhitespace() }
    }

    /**
     * Validate OTP code (6 digits).
     */
    fun isValidOtp(code: String): Boolean {
        return code.length == 6 && code.all { it.isDigit() }
    }
}
