package com.eventpass.android.domain.models

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.UUID

/**
 * User model.
 * Migrated from iOS Domain/Models/User.swift
 */
data class User(
    val id: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val role: UserRole,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null,
    val dateJoined: LocalDateTime = LocalDateTime.now(),
    val favoriteEventIds: List<String> = emptyList(),
    val followedOrganizerIds: List<String> = emptyList(),

    // Contact Verification
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,

    // Auth Providers
    val authProviders: List<String> = emptyList(), // "email", "google.com", "apple.com", "phone"

    // National ID Verification
    val isVerified: Boolean = false,
    val nationalIdNumber: String? = null,
    val nationalIdFrontImageUrl: String? = null,
    val nationalIdBackImageUrl: String? = null,
    val verificationDate: LocalDateTime? = null,
    val verificationDocumentType: VerificationDocumentType? = null,

    // Contact Preferences
    val primaryContactMethod: ContactMethod? = null,

    // Pending contact changes (awaiting verification)
    val pendingEmail: String? = null,
    val pendingPhoneNumber: String? = null,

    // User Preferences
    val favoriteEventTypes: List<String> = emptyList(),
    val hasCompletedOnboarding: Boolean = false,

    // Age & Location for Personalization
    val dateOfBirth: LocalDate? = null,
    val city: String? = null,
    val country: String? = null,
    val location: UserLocation? = null,
    val allowLocationTracking: Boolean = false,

    // Interaction Tracking for Recommendations
    val viewedEventIds: List<String> = emptyList(),
    val likedEventIds: List<String> = emptyList(),
    val purchasedEventIds: List<String> = emptyList(),

    // Notification Preferences
    val notificationPreferences: UserNotificationPreferences = UserNotificationPreferences(),

    // User Interests for Recommendations
    val interests: UserInterests = UserInterests(),

    // Dual-Role Support (single account supports both roles)
    val isAttendeeRole: Boolean = true,
    val isOrganizerRole: Boolean = false,
    val isVerifiedOrganizer: Boolean = false,
    val currentActiveRole: UserRole = role,

    // Organizer-specific data
    val organizerProfile: OrganizerProfile? = null
) {
    /**
     * Full name computed property.
     */
    val fullName: String
        get() = "$firstName $lastName"

    /**
     * Computed age from date of birth (privacy-safe: never store raw age).
     */
    val age: Int?
        get() = dateOfBirth?.let {
            Period.between(it, LocalDate.now()).years
        }

    /**
     * Check if user needs verification for organizer actions.
     */
    val needsVerificationForOrganizerActions: Boolean
        get() = isOrganizer && !isVerified

    /**
     * Role capabilities - dual-role support.
     */
    val availableRoles: List<UserRole>
        get() {
            val roles = mutableListOf<UserRole>()
            if (isAttendeeRole) roles.add(UserRole.ATTENDEE)
            if (isOrganizerRole) roles.add(UserRole.ORGANIZER)
            return roles.ifEmpty { listOf(role) }
        }

    val isAttendee: Boolean
        get() = isAttendeeRole || role == UserRole.ATTENDEE

    val isOrganizer: Boolean
        get() = isOrganizerRole || role == UserRole.ORGANIZER

    val hasBothRoles: Boolean
        get() = isAttendeeRole && isOrganizerRole

    val canBecomeOrganizer: Boolean
        get() = !isOrganizerRole

    companion object {
        val attendeeSample = User(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            role = UserRole.ATTENDEE
        )

        val organizerSample = User(
            firstName = "Jane",
            lastName = "Smith",
            email = "jane.smith@example.com",
            role = UserRole.ORGANIZER
        )
    }
}

/**
 * User role enum.
 */
enum class UserRole {
    ATTENDEE,
    ORGANIZER;

    val displayName: String
        get() = when (this) {
            ATTENDEE -> "Attendee"
            ORGANIZER -> "Organizer"
        }
}

/**
 * Contact method enum.
 */
enum class ContactMethod {
    EMAIL,
    PHONE;

    val displayName: String
        get() = when (this) {
            EMAIL -> "Email"
            PHONE -> "Phone"
        }
}

/**
 * Verification document type.
 */
enum class VerificationDocumentType(val value: String) {
    NATIONAL_ID("national_id"),
    PASSPORT("passport");

    val displayName: String
        get() = when (this) {
            NATIONAL_ID -> "National ID"
            PASSPORT -> "Passport"
        }
}

/**
 * User location model.
 */
data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val city: String? = null,
    val country: String? = null,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)

/**
 * User notification preferences.
 */
data class UserNotificationPreferences(
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val smsEnabled: Boolean = false,
    val eventReminders: Boolean = true,
    val ticketUpdates: Boolean = true,
    val promotions: Boolean = false,
    val organizerUpdates: Boolean = true,
    val priceAlerts: Boolean = true
) {
    companion object {
        val default = UserNotificationPreferences()
    }
}

/**
 * User interests for recommendations.
 */
data class UserInterests(
    val categories: List<EventCategory> = emptyList(),
    val priceRange: PriceRange = PriceRange.ANY,
    val preferredDays: List<String> = emptyList(), // "Monday", "Friday", etc.
    val preferredTimes: List<String> = emptyList(), // "morning", "evening", "night"
    val maxDistance: Int? = null // km
) {
    companion object {
        val default = UserInterests()
    }
}

/**
 * Price range preference.
 */
enum class PriceRange {
    FREE,
    BUDGET, // Under 50,000 UGX
    MODERATE, // 50,000 - 150,000 UGX
    PREMIUM, // 150,000 - 500,000 UGX
    LUXURY, // Over 500,000 UGX
    ANY
}
