package com.eventpass.android.core.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Date utilities.
 * Migrated from iOS Core/Utilities/DateUtilities.swift
 */
object DateUtils {

    private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
    private val shortDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a", Locale.getDefault())

    /**
     * Format date as full date string (e.g., "Monday, January 15, 2024").
     */
    fun formatFullDate(dateTime: LocalDateTime): String {
        return dateTime.format(fullDateFormatter)
    }

    /**
     * Format date as short date string (e.g., "Jan 15, 2024").
     */
    fun formatShortDate(dateTime: LocalDateTime): String {
        return dateTime.format(shortDateFormatter)
    }

    /**
     * Format time (e.g., "7:30 PM").
     */
    fun formatTime(dateTime: LocalDateTime): String {
        return dateTime.format(timeFormatter)
    }

    /**
     * Format date and time (e.g., "Jan 15, 2024 at 7:30 PM").
     */
    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(dateTimeFormatter)
    }

    /**
     * Get relative time string (e.g., "in 2 days", "3 hours ago").
     */
    fun getRelativeTimeString(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(now, dateTime)

        return when {
            duration.isNegative -> getPastRelativeTime(duration.abs())
            else -> getFutureRelativeTime(duration)
        }
    }

    private fun getPastRelativeTime(duration: Duration): String {
        return when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            duration.toDays() < 7 -> "${duration.toDays()} days ago"
            duration.toDays() < 30 -> "${duration.toDays() / 7} weeks ago"
            duration.toDays() < 365 -> "${duration.toDays() / 30} months ago"
            else -> "${duration.toDays() / 365} years ago"
        }
    }

    private fun getFutureRelativeTime(duration: Duration): String {
        return when {
            duration.toMinutes() < 1 -> "Now"
            duration.toMinutes() < 60 -> "In ${duration.toMinutes()} minutes"
            duration.toHours() < 24 -> "In ${duration.toHours()} hours"
            duration.toDays() < 7 -> "In ${duration.toDays()} days"
            duration.toDays() < 30 -> "In ${duration.toDays() / 7} weeks"
            duration.toDays() < 365 -> "In ${duration.toDays() / 30} months"
            else -> "In ${duration.toDays() / 365} years"
        }
    }

    /**
     * Format date range (e.g., "Jan 15 - Jan 17, 2024").
     */
    fun formatDateRange(start: LocalDateTime, end: LocalDateTime): String {
        return if (start.toLocalDate() == end.toLocalDate()) {
            // Same day
            "${formatShortDate(start)}, ${formatTime(start)} - ${formatTime(end)}"
        } else if (start.year == end.year && start.month == end.month) {
            // Same month
            "${start.dayOfMonth} - ${end.dayOfMonth} ${start.format(DateTimeFormatter.ofPattern("MMM yyyy"))}"
        } else if (start.year == end.year) {
            // Same year
            "${start.format(DateTimeFormatter.ofPattern("MMM d"))} - ${end.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}"
        } else {
            // Different years
            "${formatShortDate(start)} - ${formatShortDate(end)}"
        }
    }

    /**
     * Check if date is today.
     */
    fun isToday(dateTime: LocalDateTime): Boolean {
        return dateTime.toLocalDate() == LocalDateTime.now().toLocalDate()
    }

    /**
     * Check if date is tomorrow.
     */
    fun isTomorrow(dateTime: LocalDateTime): Boolean {
        return dateTime.toLocalDate() == LocalDateTime.now().plusDays(1).toLocalDate()
    }

    /**
     * Check if date is this week.
     */
    fun isThisWeek(dateTime: LocalDateTime): Boolean {
        val now = LocalDateTime.now()
        val weekEnd = now.plusDays(7 - now.dayOfWeek.value.toLong())
        return dateTime.isBefore(weekEnd) && dateTime.isAfter(now)
    }

    /**
     * Format header date (e.g., "Monday, January 15").
     */
    fun formatHeaderDate(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
        return dateTime.format(formatter)
    }
}
