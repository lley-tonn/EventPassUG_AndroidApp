package com.eventpass.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.eventpass.android.core.util.DateUtils
import com.eventpass.android.ui.theme.EventPassDimensions
import java.time.LocalDateTime

/**
 * Header bar with date, greeting, and notification badge.
 * Migrated from iOS UI/Components/HeaderBar.swift
 *
 * SwiftUI → Compose mapping:
 * - HStack → Row
 * - VStack → Column
 * - .accessibilityElement(children: .combine) → semantics block
 */
@Composable
fun HeaderBar(
    firstName: String,
    notificationCount: Int,
    onNotificationTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = EventPassDimensions.Spacing.md,
                vertical = EventPassDimensions.Spacing.sm
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Date and greeting
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {
                contentDescription = "${getGreeting()}, $firstName"
            },
            verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs)
        ) {
            Text(
                text = DateUtils.formatHeaderDate(LocalDateTime.now()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "${getGreeting()}, $firstName",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Right side: Notification bell
        NotificationBadge(
            count = notificationCount,
            onClick = onNotificationTap
        )
    }
}

/**
 * Get greeting based on time of day.
 */
private fun getGreeting(): String {
    val hour = LocalDateTime.now().hour
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}

/**
 * Minimal header with just title and optional action.
 */
@Composable
fun SimpleHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = EventPassDimensions.Spacing.md,
                vertical = EventPassDimensions.Spacing.sm
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        action?.invoke()
    }
}
