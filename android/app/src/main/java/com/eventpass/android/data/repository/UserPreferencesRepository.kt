package com.eventpass.android.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * User preferences data class.
 */
data class UserPreferences(
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en"
)

/**
 * User preferences repository interface.
 * Handles app settings and user preferences storage.
 */
interface UserPreferencesRepository {

    /**
     * Get all user preferences as flow.
     */
    fun getUserPreferences(): Flow<UserPreferences>

    /**
     * Whether dark mode is enabled.
     */
    val isDarkMode: Flow<Boolean>

    /**
     * Whether notifications are enabled.
     */
    val notificationsEnabled: Flow<Boolean>

    /**
     * User's preferred language.
     */
    val language: Flow<String>

    /**
     * Set dark mode preference.
     */
    suspend fun setDarkMode(enabled: Boolean)

    /**
     * Set notifications preference.
     */
    suspend fun setNotificationsEnabled(enabled: Boolean)

    /**
     * Set preferred language.
     */
    suspend fun setLanguage(languageCode: String)

    /**
     * Clear all preferences.
     */
    suspend fun clearAll()
}
