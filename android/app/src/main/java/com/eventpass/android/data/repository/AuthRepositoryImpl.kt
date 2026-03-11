package com.eventpass.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eventpass.android.domain.models.User
import com.eventpass.android.domain.models.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of AuthRepository.
 * Will be replaced with actual Firebase/backend implementation.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    override val currentUser: Flow<User?> = _currentUser

    override val isAuthenticated: Flow<Boolean> = _currentUser.map { it != null }

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            // Simulate network delay
            delay(1000)

            // Mock successful login
            val user = User(
                id = UUID.randomUUID().toString(),
                firstName = "Test",
                lastName = "User",
                email = email,
                role = UserRole.ATTENDEE,
                phoneNumber = null,
                profileImageUrl = null,
                isVerified = true
            )
            _currentUser.value = user
            saveUserId(user.id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithPhone(phoneNumber: String): Result<Unit> {
        return try {
            delay(1000)
            // Mock OTP sent
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, code: String): Result<User> {
        return try {
            delay(1000)

            // Mock OTP verification (accept any 6-digit code)
            if (code.length != 6) {
                return Result.failure(Exception("Invalid OTP code"))
            }

            val user = User(
                id = UUID.randomUUID().toString(),
                firstName = "Phone",
                lastName = "User",
                email = null,
                role = UserRole.ATTENDEE,
                phoneNumber = phoneNumber,
                profileImageUrl = null,
                isVerified = true
            )
            _currentUser.value = user
            saveUserId(user.id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String
    ): Result<User> {
        return try {
            delay(1000)

            // Split fullName into firstName and lastName
            val nameParts = fullName.trim().split(" ", limit = 2)
            val firstName = nameParts.getOrElse(0) { "User" }
            val lastName = nameParts.getOrElse(1) { "" }

            val user = User(
                id = UUID.randomUUID().toString(),
                firstName = firstName,
                lastName = lastName,
                email = email,
                role = UserRole.ATTENDEE,
                phoneNumber = null,
                profileImageUrl = null,
                isVerified = false
            )
            _currentUser.value = user
            saveUserId(user.id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            delay(1000)

            val user = User(
                id = UUID.randomUUID().toString(),
                firstName = "Google",
                lastName = "User",
                email = "google.user@gmail.com",
                role = UserRole.ATTENDEE,
                phoneNumber = null,
                profileImageUrl = null,
                isVerified = true
            )
            _currentUser.value = user
            saveUserId(user.id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            _currentUser.value = null
            dataStore.edit { prefs ->
                prefs.remove(KEY_USER_ID)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(user: User): Result<User> {
        return try {
            delay(500)
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasCompletedOnboarding(): Boolean {
        return dataStore.data.first()[KEY_ONBOARDING_COMPLETED] ?: false
    }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = true
        }
    }

    override suspend fun switchRole(role: UserRole): Result<User> {
        return try {
            val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
            val updatedUser = user.copy(currentActiveRole = role)
            _currentUser.value = updatedUser
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            delay(1000)
            // Mock password reset email sent
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            delay(1000)
            _currentUser.value = null
            dataStore.edit { prefs ->
                prefs.clear()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
        }
    }
}
