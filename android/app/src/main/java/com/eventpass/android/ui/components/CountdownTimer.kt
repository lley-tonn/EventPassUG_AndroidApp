package com.eventpass.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.android.domain.models.Event
import com.eventpass.android.ui.theme.EventPassColors
import com.eventpass.android.ui.theme.EventPassDimensions
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

/**
 * Sales countdown timer styles.
 */
enum class CountdownStyle {
    BADGE,   // Compact badge style
    INLINE,  // Inline with icon
    CARD     // Card with full details
}

/**
 * Sales countdown timer component.
 * Migrated from iOS UI/Components/SalesCountdownTimer.swift
 *
 * SwiftUI → Compose mapping:
 * - Timer.publish → LaunchedEffect with delay
 * - @State private var timeRemaining → remember { mutableLongStateOf }
 * - .onAppear/.onDisappear → LaunchedEffect lifecycle
 */
@Composable
fun SalesCountdownTimer(
    event: Event,
    style: CountdownStyle,
    modifier: Modifier = Modifier
) {
    var timeRemaining by remember { mutableLongStateOf(0L) }

    // Calculate initial time and start timer
    LaunchedEffect(event.id) {
        while (true) {
            timeRemaining = calculateTimeRemaining(event)
            if (timeRemaining <= 0 || !event.isTicketSalesOpen) break
            delay(1000)
        }
    }

    if (event.isTicketSalesOpen) {
        when (style) {
            CountdownStyle.BADGE -> CountdownBadgeView(
                timeRemaining = timeRemaining,
                modifier = modifier
            )
            CountdownStyle.INLINE -> CountdownInlineView(
                timeRemaining = timeRemaining,
                modifier = modifier
            )
            CountdownStyle.CARD -> CountdownCardView(
                timeRemaining = timeRemaining,
                modifier = modifier
            )
        }
    } else {
        SalesClosedView(
            message = event.ticketSalesStatusMessage,
            modifier = modifier
        )
    }
}

/**
 * Badge style countdown.
 */
@Composable
private fun CountdownBadgeView(
    timeRemaining: Long,
    modifier: Modifier = Modifier
) {
    val urgencyColor = getUrgencyColor(timeRemaining)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(EventPassDimensions.CornerRadius.badge),
        color = urgencyColor.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = EventPassDimensions.Spacing.sm, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                modifier = Modifier.padding(end = 2.dp),
                tint = urgencyColor
            )

            Text(
                text = formatShortCountdown(timeRemaining),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = urgencyColor
            )
        }
    }
}

/**
 * Inline style countdown.
 */
@Composable
private fun CountdownInlineView(
    timeRemaining: Long,
    modifier: Modifier = Modifier
) {
    val urgencyColor = getUrgencyColor(timeRemaining)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = urgencyColor
        )

        Text(
            text = "Sales end in",
            style = MaterialTheme.typography.bodyMedium,
            color = urgencyColor
        )

        Text(
            text = formatFullCountdown(timeRemaining),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = urgencyColor
        )
    }
}

/**
 * Card style countdown.
 */
@Composable
private fun CountdownCardView(
    timeRemaining: Long,
    modifier: Modifier = Modifier
) {
    val urgencyColor = getUrgencyColor(timeRemaining)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(EventPassDimensions.CornerRadius.md),
        color = urgencyColor.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, urgencyColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(EventPassDimensions.Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = urgencyColor
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Ticket sales ending soon",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${formatFullCountdown(timeRemaining)} remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Sales closed view.
 */
@Composable
private fun SalesClosedView(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = null,
            tint = EventPassColors.Error
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.Error
        )
    }
}

/**
 * Calculate time remaining until sales close.
 */
private fun calculateTimeRemaining(event: Event): Long {
    val now = LocalDateTime.now()
    val salesEndDate = event.salesEndDate ?: event.startDate

    return if (now.isBefore(salesEndDate)) {
        Duration.between(now, salesEndDate).seconds
    } else {
        0
    }
}

/**
 * Get urgency color based on time remaining.
 */
private fun getUrgencyColor(timeRemaining: Long): Color {
    return when {
        timeRemaining < 3600 -> EventPassColors.Error      // Less than 1 hour
        timeRemaining < 86400 -> EventPassColors.Warning   // Less than 1 day
        else -> EventPassColors.Success
    }
}

/**
 * Format countdown as short string (e.g., "2h 30m").
 */
private fun formatShortCountdown(seconds: Long): String {
    if (seconds <= 0) return "Ended"

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60

    return when {
        hours >= 24 -> "${hours / 24}d ${hours % 24}h"
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}

/**
 * Format countdown as full string (e.g., "2 hours 30 minutes").
 */
private fun formatFullCountdown(seconds: Long): String {
    if (seconds <= 0) return "Ended"

    val days = seconds / 86400
    val hours = (seconds % 86400) / 3600
    val minutes = (seconds % 3600) / 60

    return when {
        days > 0 -> "$days day${if (days > 1) "s" else ""} $hours hour${if (hours != 1L) "s" else ""}"
        hours > 0 -> "$hours hour${if (hours != 1L) "s" else ""} $minutes min${if (minutes != 1L) "s" else ""}"
        minutes > 0 -> "$minutes minute${if (minutes != 1L) "s" else ""}"
        else -> "$seconds second${if (seconds != 1L) "s" else ""}"
    }
}

/**
 * Event countdown timer (time until event starts).
 */
@Composable
fun EventCountdownTimer(
    startDate: LocalDateTime,
    modifier: Modifier = Modifier
) {
    var timeRemaining by remember { mutableLongStateOf(0L) }

    LaunchedEffect(startDate) {
        while (true) {
            val now = LocalDateTime.now()
            timeRemaining = if (now.isBefore(startDate)) {
                Duration.between(now, startDate).seconds
            } else {
                0
            }
            if (timeRemaining <= 0) break
            delay(1000)
        }
    }

    if (timeRemaining > 0) {
        CountdownDisplay(
            days = (timeRemaining / 86400).toInt(),
            hours = ((timeRemaining % 86400) / 3600).toInt(),
            minutes = ((timeRemaining % 3600) / 60).toInt(),
            seconds = (timeRemaining % 60).toInt(),
            modifier = modifier
        )
    }
}

/**
 * Countdown display with individual time units.
 */
@Composable
private fun CountdownDisplay(
    days: Int,
    hours: Int,
    minutes: Int,
    seconds: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm)
    ) {
        if (days > 0) {
            TimeUnit(value = days, label = "Days")
        }
        TimeUnit(value = hours, label = "Hours")
        TimeUnit(value = minutes, label = "Min")
        TimeUnit(value = seconds, label = "Sec")
    }
}

@Composable
private fun TimeUnit(
    value: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = EventPassColors.Primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = EventPassColors.Primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
