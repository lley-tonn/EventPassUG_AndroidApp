package com.eventpass.feature.attendee.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.IconBubbleButton
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * Attendee home header — the iOS layout has a small date label above a big
 * greeting, with three circular icon bubbles (Search, Favorites, Notifications)
 * on the right. The bell shows an unread-count badge.
 */
@Composable
fun HomeHeader(
    dateLabel: String,
    greeting: String,
    unreadCount: Int,
    onSearch: () -> Unit,
    onFavorites: () -> Unit,
    onNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelMedium,
                color = EventPassColors.InkMuted
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = EventPassColors.Ink
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            IconBubbleButton(
                icon = Icons.Filled.Search,
                onClick = onSearch,
                contentDescription = "Search"
            )
            IconBubbleButton(
                icon = Icons.Filled.FavoriteBorder,
                onClick = onFavorites,
                contentDescription = "Favorites",
                iconTint = EventPassColors.Primary
            )
            IconBubbleButton(
                icon = Icons.Filled.Notifications,
                onClick = onNotifications,
                contentDescription = "Notifications",
                badgeCount = unreadCount.takeIf { it > 0 }
            )
        }
    }
}
