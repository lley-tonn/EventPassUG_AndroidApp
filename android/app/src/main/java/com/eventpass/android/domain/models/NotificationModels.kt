package com.eventpass.android.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Notification model.
 * Migrated from iOS Domain/Models/NotificationModel.swift
 */
data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val data: Map<String, String> = emptyMap(),
    val isRead: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val readAt: LocalDateTime? = null
) {
    /**
     * Deep link URL for navigation.
     */
    val deepLink: String?
        get() = data["deepLink"] ?: data["deep_link"]

    /**
     * Event ID if notification is event-related.
     */
    val eventId: String?
        get() = data["eventId"] ?: data["event_id"]

    /**
     * Ticket ID if notification is ticket-related.
     */
    val ticketId: String?
        get() = data["ticketId"] ?: data["ticket_id"]

    /**
     * Time since notification was created.
     */
    val timeAgo: String
        get() {
            val now = LocalDateTime.now()
            val seconds = java.time.Duration.between(createdAt, now).seconds

            return when {
                seconds < 60 -> "Just now"
                seconds < 3600 -> "${seconds / 60}m ago"
                seconds < 86400 -> "${seconds / 3600}h ago"
                seconds < 604800 -> "${seconds / 86400}d ago"
                else -> "${seconds / 604800}w ago"
            }
        }
}

/**
 * Notification type enum.
 */
enum class NotificationType {
    // Event-related
    EVENT_REMINDER,
    EVENT_UPDATE,
    EVENT_CANCELLED,
    EVENT_RESCHEDULED,
    NEW_EVENT_NEARBY,
    FAVORITE_ORGANIZER_EVENT,

    // Ticket-related
    TICKET_PURCHASE_CONFIRMED,
    TICKET_TRANSFER_RECEIVED,
    TICKET_REFUND_APPROVED,
    TICKET_REFUND_PROCESSED,

    // Organizer-related
    TICKET_SOLD,
    EVENT_GOING_LIVE,
    DAILY_SALES_SUMMARY,
    LOW_TICKET_ALERT,
    PAYOUT_PROCESSED,

    // System
    ACCOUNT_VERIFIED,
    SECURITY_ALERT,
    PROMO_OFFER,
    GENERAL;

    val iconName: String
        get() = when (this) {
            EVENT_REMINDER -> "alarm"
            EVENT_UPDATE -> "update"
            EVENT_CANCELLED -> "event_busy"
            EVENT_RESCHEDULED -> "event"
            NEW_EVENT_NEARBY -> "location_on"
            FAVORITE_ORGANIZER_EVENT -> "star"
            TICKET_PURCHASE_CONFIRMED -> "confirmation_number"
            TICKET_TRANSFER_RECEIVED -> "swap_horiz"
            TICKET_REFUND_APPROVED -> "check_circle"
            TICKET_REFUND_PROCESSED -> "payments"
            TICKET_SOLD -> "point_of_sale"
            EVENT_GOING_LIVE -> "play_circle"
            DAILY_SALES_SUMMARY -> "bar_chart"
            LOW_TICKET_ALERT -> "warning"
            PAYOUT_PROCESSED -> "account_balance"
            ACCOUNT_VERIFIED -> "verified"
            SECURITY_ALERT -> "security"
            PROMO_OFFER -> "local_offer"
            GENERAL -> "notifications"
        }

    val colorHex: Long
        get() = when (this) {
            EVENT_CANCELLED, SECURITY_ALERT, LOW_TICKET_ALERT -> 0xFFF44336 // Red
            EVENT_UPDATE, EVENT_RESCHEDULED -> 0xFFFF9800 // Orange
            TICKET_PURCHASE_CONFIRMED, TICKET_REFUND_PROCESSED, PAYOUT_PROCESSED,
            ACCOUNT_VERIFIED -> 0xFF4CAF50 // Green
            PROMO_OFFER, FAVORITE_ORGANIZER_EVENT -> 0xFF9C27B0 // Purple
            else -> 0xFF2196F3 // Blue
        }
}

/**
 * Notification preferences.
 */
data class NotificationPreferences(
    // Channels
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val smsEnabled: Boolean = false,

    // Event notifications
    val eventReminders: Boolean = true,
    val eventUpdates: Boolean = true,
    val newEventsNearby: Boolean = true,
    val favoriteOrganizerEvents: Boolean = true,

    // Ticket notifications
    val ticketUpdates: Boolean = true,
    val refundUpdates: Boolean = true,

    // Organizer notifications (for organizers)
    val salesNotifications: Boolean = true,
    val dailySummary: Boolean = true,
    val lowTicketAlerts: Boolean = true,
    val payoutNotifications: Boolean = true,

    // Marketing
    val promotions: Boolean = false,
    val recommendations: Boolean = true,

    // Reminder timing
    val reminderHoursBefore: Int = 24, // Default: 24 hours before event
    val secondReminderHoursBefore: Int? = 2 // Optional second reminder
) {
    companion object {
        val default = NotificationPreferences()

        val minimal = NotificationPreferences(
            pushEnabled = true,
            emailEnabled = false,
            smsEnabled = false,
            eventReminders = true,
            eventUpdates = true,
            newEventsNearby = false,
            favoriteOrganizerEvents = false,
            ticketUpdates = true,
            refundUpdates = true,
            salesNotifications = true,
            dailySummary = false,
            lowTicketAlerts = true,
            payoutNotifications = true,
            promotions = false,
            recommendations = false
        )
    }
}

/**
 * Notification channel for delivery method selection.
 */
enum class NotificationChannel {
    PUSH,
    EMAIL,
    SMS,
    IN_APP;

    val displayName: String
        get() = when (this) {
            PUSH -> "Push Notifications"
            EMAIL -> "Email"
            SMS -> "SMS"
            IN_APP -> "In-App"
        }

    val iconName: String
        get() = when (this) {
            PUSH -> "notifications"
            EMAIL -> "email"
            SMS -> "sms"
            IN_APP -> "phone_android"
        }
}

/**
 * Notification frequency settings.
 */
enum class NotificationFrequency {
    INSTANT,
    HOURLY_DIGEST,
    DAILY_DIGEST,
    WEEKLY_DIGEST;

    val displayName: String
        get() = when (this) {
            INSTANT -> "Instant"
            HOURLY_DIGEST -> "Hourly Digest"
            DAILY_DIGEST -> "Daily Digest"
            WEEKLY_DIGEST -> "Weekly Digest"
        }
}
