package com.eventpass.android.domain.models

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Event model.
 * Migrated from iOS Domain/Models/Event.swift
 */
data class Event(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val organizerId: String,
    val organizerName: String,
    val posterUrl: String? = null,
    val category: EventCategory,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val venue: Venue,
    val ticketTypes: List<TicketType> = emptyList(),
    val status: EventStatus = EventStatus.DRAFT,
    val rating: Double = 0.0,
    val totalRatings: Int = 0,
    val likeCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val ageRestriction: AgeRestriction = AgeRestriction.NONE
) {
    /**
     * Whether the event is happening now.
     */
    val isHappeningNow: Boolean
        get() {
            val now = LocalDateTime.now()
            return now >= startDate && now <= endDate && status == EventStatus.PUBLISHED
        }

    /**
     * Whether the event has expired.
     */
    val isExpired: Boolean
        get() = LocalDateTime.now() > endDate

    /**
     * Check if event status should be auto-updated.
     */
    val shouldAutoUpdateStatus: EventStatus?
        get() {
            val now = LocalDateTime.now()

            // Don't auto-update draft or cancelled events
            if (status != EventStatus.PUBLISHED && status != EventStatus.ONGOING) {
                return null
            }

            // Check if event has ended
            if (now > endDate) {
                return EventStatus.COMPLETED
            }

            // Check if event is currently ongoing
            if (now >= startDate && now <= endDate && status == EventStatus.PUBLISHED) {
                return EventStatus.ONGOING
            }

            return null
        }

    /**
     * Formatted price range.
     */
    val priceRange: String
        get() {
            val prices = ticketTypes.map { it.price }
            val min = prices.minOrNull()
            val max = prices.maxOrNull()

            return when {
                min == null || max == null -> "Free"
                min == 0.0 && max == 0.0 -> "Free"
                min == max -> "UGX ${formatNumber(min.toLong())}"
                else -> "UGX ${formatNumber(min.toLong())} - ${formatNumber(max.toLong())}"
            }
        }

    /**
     * Time category for filtering.
     */
    val timeCategory: TimeCategory?
        get() {
            val now = LocalDateTime.now()
            return when {
                startDate.toLocalDate() == now.toLocalDate() -> TimeCategory.TODAY
                startDate.toLocalDate().isEqual(now.toLocalDate().plusDays(1)) -> TimeCategory.TOMORROW
                ChronoUnit.DAYS.between(now.toLocalDate(), startDate.toLocalDate()) <= 7 -> TimeCategory.THIS_WEEK
                ChronoUnit.DAYS.between(now.toLocalDate(), startDate.toLocalDate()) <= 30 -> TimeCategory.THIS_MONTH
                else -> null
            }
        }

    /**
     * Total capacity from all ticket types.
     */
    val totalCapacity: Int
        get() = ticketTypes.sumOf { it.quantity }

    /**
     * Total tickets sold.
     */
    val ticketsSold: Int
        get() = ticketTypes.sumOf { it.sold }

    /**
     * Whether tickets are sold out.
     */
    val isSoldOut: Boolean
        get() = ticketsSold >= totalCapacity && totalCapacity > 0

    /**
     * Percentage of tickets sold.
     */
    val soldPercentage: Float
        get() = if (totalCapacity > 0) {
            (ticketsSold.toFloat() / totalCapacity.toFloat()) * 100
        } else 0f

    /**
     * Whether event is free (all tickets are free).
     */
    val isFree: Boolean
        get() = ticketTypes.all { it.price == 0.0 }

    /**
     * Sales end date (earliest ticket type end date or event start).
     */
    val salesEndDate: LocalDateTime?
        get() = ticketTypes.minOfOrNull { it.saleEndDate } ?: startDate

    /**
     * Whether ticket sales are currently open.
     */
    val isTicketSalesOpen: Boolean
        get() {
            val now = LocalDateTime.now()
            return ticketTypes.any { ticketType ->
                now >= ticketType.saleStartDate &&
                now <= ticketType.saleEndDate &&
                !ticketType.isSoldOut
            } && now < startDate && status == EventStatus.PUBLISHED
        }

    /**
     * Ticket sales status message.
     */
    val ticketSalesStatusMessage: String
        get() {
            val now = LocalDateTime.now()
            return when {
                status != EventStatus.PUBLISHED -> "Event not published"
                now >= startDate -> "Event has started"
                isSoldOut -> "Sold Out"
                ticketTypes.all { now < it.saleStartDate } -> "Sales not started"
                ticketTypes.all { now > it.saleEndDate } -> "Sales ended"
                else -> "Sales closed"
            }
        }

    private fun formatNumber(number: Long): String {
        return String.format("%,d", number)
    }
}

/**
 * Venue model.
 */
data class Venue(
    val name: String,
    val address: String,
    val city: String = "Kampala",
    val latitude: Double,
    val longitude: Double,
    val country: String = "Uganda"
)

/**
 * Event category enum.
 * Mirrors iOS EventCategory with 16 categories.
 */
enum class EventCategory(
    val displayName: String,
    val iconName: String,
    val categoryColor: Long
) {
    MUSIC("Music", "music_note", 0xFF9C27B0),
    ARTS_CULTURE("Arts & Culture", "palette", 0xFF00BCD4),
    CONCERTS("Concerts", "mic", 0xFFE91E63),
    SPORTS_WELLNESS("Sports & Wellness", "fitness_center", 0xFF4CAF50),
    TECHNOLOGY("Technology", "laptop", 0xFF2196F3),
    FUNDRAISING("Fundraising", "favorite", 0xFFE91E63),
    COMEDY("Comedy", "theater_comedy", 0xFFFF9800),
    POETRY("Poetry", "menu_book", 0xFF795548),
    DRAMA("Drama", "movie", 0xFFF44336),
    EXHIBITIONS("Exhibitions", "museum", 0xFF3F51B5),
    NETWORKING("Networking", "groups", 0xFF009688),
    EDUCATION("Education", "school", 0xFFFFEB3B),
    FOOD("Food & Drinks", "restaurant", 0xFFCC6633),
    NIGHTLIFE("Nightlife", "nightlife", 0xFF9C27B0),
    FESTIVALS("Festivals", "celebration", 0xFFFF9800),
    OTHER("Other", "star", 0xFF9E9E9E);

    companion object {
        fun fromString(value: String): EventCategory {
            return entries.find {
                it.displayName.equals(value, ignoreCase = true) ||
                it.name.equals(value, ignoreCase = true)
            } ?: OTHER
        }
    }
}

/**
 * Time category for event filtering.
 */
enum class TimeCategory(val displayName: String) {
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    THIS_WEEK("This Week"),
    THIS_WEEKEND("This Weekend"),
    THIS_MONTH("This Month"),
    LATER("Later")
}

/**
 * Event status enum.
 */
enum class EventStatus {
    DRAFT,
    PUBLISHED,
    ONGOING,
    COMPLETED,
    CANCELLED;

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

/**
 * Age restriction enum.
 */
enum class AgeRestriction(val age: Int, val displayName: String, val iconName: String) {
    NONE(0, "All Ages", "people"),
    AGE_13_PLUS(13, "13+", "warning"),
    AGE_16_PLUS(16, "16+", "warning"),
    AGE_18_PLUS(18, "18+", "warning"),
    AGE_21_PLUS(21, "21+", "warning");

    companion object {
        fun fromAge(age: Int): AgeRestriction {
            return entries.find { it.age == age } ?: NONE
        }
    }
}
