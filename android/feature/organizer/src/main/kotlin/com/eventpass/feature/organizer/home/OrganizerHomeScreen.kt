package com.eventpass.feature.organizer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.IconBubbleButton
import com.eventpass.core.design.components.PillChip
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/** A single event summary rendered on the organizer home list. */
data class OrganizerEventSummary(
    val id: String,
    val title: String,
    val dateText: String,
    val posterUrl: String?,
    val ticketsSold: Int,
    val likes: Int
)

/** The four lifecycle buckets an organizer's events fall into. */
enum class EventStatusTab(val label: String) {
    PUBLISHED("Published"),
    DRAFT("Draft"),
    ONGOING("Ongoing"),
    COMPLETED("Completed")
}

/**
 * Organizer Home (design reference IMG_2800 / IMG_2809). A greeting header, the
 * primary "Create New Event" CTA, status filter pills with counts, and either
 * the filtered event list or an empty state.
 */
@Composable
fun OrganizerHomeScreen(
    dateLabel: String,
    greeting: String,
    unreadCount: Int,
    counts: Map<EventStatusTab, Int>,
    selectedTab: EventStatusTab,
    events: List<OrganizerEventSummary>,
    onTabSelected: (EventStatusTab) -> Unit,
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onCreateEvent: () -> Unit,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(dateLabel, style = MaterialTheme.typography.labelMedium, color = EventPassColors.InkMuted)
                Spacer(Modifier.height(2.dp))
                Text(
                    greeting,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = EventPassColors.Ink
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                IconBubbleButton(
                    icon = Icons.Filled.Search,
                    onClick = onSearch,
                    background = EventPassColors.DividerLight,
                    contentDescription = "Search"
                )
                IconBubbleButton(
                    icon = Icons.Filled.Notifications,
                    onClick = onNotifications,
                    background = EventPassColors.DividerLight,
                    contentDescription = "Notifications",
                    badgeCount = unreadCount.takeIf { it > 0 }
                )
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        // Create New Event CTA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .height(64.dp)
                .clip(Radii.CardLarge)
                .background(EventPassColors.Primary)
                .clickable(onClick = onCreateEvent),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PlusBadge()
            Spacer(Modifier.width(Spacing.md))
            Text(
                "Create New Event",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.White
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        // Status filter pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            EventStatusTab.entries.forEach { tab ->
                PillChip(
                    text = tab.label,
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    count = counts[tab] ?: 0
                )
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        if (events.isEmpty()) {
            EmptyState(selectedTab = selectedTab, onCreateEvent = onCreateEvent)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                events.forEach { event ->
                    OrganizerEventRow(event = event, onClick = { onEventClick(event.id) })
                }
                Spacer(Modifier.height(Spacing.xxxl))
            }
        }
    }
}

@Composable
private fun PlusBadge() {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(50))
            .background(EventPassColors.White),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = EventPassColors.Primary, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun OrganizerEventRow(event: OrganizerEventSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(88.dp, 72.dp)
                .clip(Radii.Card)
                .background(EventPassColors.DividerLight)
        )
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                event.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(event.dateText, style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
            Spacer(Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ConfirmationNumber, contentDescription = null, tint = EventPassColors.InkSubtle, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(Spacing.xs))
                Text("${event.ticketsSold}", style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
                Spacer(Modifier.width(Spacing.md))
                Icon(Icons.Filled.FavoriteBorder, contentDescription = null, tint = EventPassColors.InkSubtle, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(Spacing.xs))
                Text("${event.likes}", style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = EventPassColors.InkSubtle,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun EmptyState(selectedTab: EventStatusTab, onCreateEvent: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.EventBusy,
            contentDescription = null,
            tint = EventPassColors.InkSubtle,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(Spacing.lg))
        Text(
            "No ${selectedTab.label} Events",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.Ink
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            "Create and publish your first event",
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.xl))
        Row(
            modifier = Modifier
                .clip(Radii.Button)
                .background(EventPassColors.Primary)
                .clickable(onClick = onCreateEvent)
                .padding(horizontal = Spacing.xxl, vertical = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlusBadge()
            Spacer(Modifier.width(Spacing.md))
            Text(
                "Create Event",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.White
            )
        }
    }
}
