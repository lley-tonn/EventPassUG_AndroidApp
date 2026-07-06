package com.eventpass.android.data.repository

import com.eventpass.android.domain.models.User
import kotlinx.coroutines.flow.Flow

/**
 * Authentication repository interface.
 */
interface AuthRepository {

    /**
     * Current authenticated user, null if not logged in.
     */
    val currentUser: Flow<User?>

    /**
     * Whether the user is authenticated.
     */
    val isAuthenticated: Flow<Boolean>

    /**
     * Sign in with email and password.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<User>

    /**
     * Sign in with phone number (triggers OTP).
     */
    suspend fun signInWithPhone(phoneNumber: String): Result<Unit>

    /**
     * Verify OTP code.
     */
    suspend fun verifyOtp(phoneNumber: String, code: String): Result<User>

    /**
     * Sign up with email and password.
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String
    ): Result<User>

    /**
     * Sign in with Google.
     */
    suspend fun signInWithGoogle(idToken: String): Result<User>

    /**
     * Sign out current user.
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Update user profile.
     */
    suspend fun updateProfile(user: User): Result<User>

    /**
     * Check if user has completed onboarding.
     */
    suspend fun hasCompletedOnboarding(): Boolean

    /**
     * Mark onboarding as completed.
     */
    suspend fun setOnboardingCompleted()

    /**
     * Switch user role (attendee/organizer).
     */
    suspend fun switchRole(role: com.eventpass.android.domain.models.UserRole): Result<User>

    /**
     * Promote the current user to a verified organizer (completes the
     * Become-an-Organizer flow) and activate the organizer role.
     */
    suspend fun becomeOrganizer(): Result<User>

    // --- Contact / identity verification (mock) --------------------------------

    /** Mark the current user's email as verified. */
    suspend fun verifyEmail(): Result<User>

    /** Change the current user's email (resets email-verified state). */
    suspend fun changeEmail(newEmail: String): Result<User>

    /** Attach a phone number to the current user (unverified until confirmed). */
    suspend fun addPhoneNumber(phoneNumber: String): Result<User>

    /** Mark the current user's phone number as verified. */
    suspend fun verifyPhoneNumber(): Result<User>

    /** Submit an identity document and mark the user as identity-verified. */
    suspend fun submitIdentityVerification(documentNumber: String): Result<User>

    /**
     * Request password reset.
     */
    suspend fun requestPasswordReset(email: String): Result<Unit>

    /**
     * Delete user account.
     */
    suspend fun deleteAccount(): Result<Unit>
}
