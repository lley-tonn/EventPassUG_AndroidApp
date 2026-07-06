package com.eventpass.feature.attendee.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.attendee.tickets.components.TicketCard
import com.eventpass.feature.attendee.tickets.components.TicketFilterChip
import com.eventpass.feature.attendee.tickets.components.TicketStatus

/**
 * Which pill in the filter rail is currently selected.
 */
enum class TicketListFilter { All, Active, Expired }

/**
 * UI-side ticket record for the list. Keeps `:feature:attendee` free of
 * domain types — the `:app` wrapper maps from domain `Ticket`.
 */
data class TicketCardData(
    val id: String,
    val title: String,
    val ticketTypeText: String,
    val priceText: String,
    val dateText: String,
    val venueText: String,
    val status: TicketStatus
)

/**
 * My Tickets screen — matches design reference IMG_2770.
 *
 * Stateless: the caller owns [selectedFilter] + [tickets] (already filtered
 * to the active pill) and supplies the total counts for each pill badge.
 */
@Composable
fun MyTicketsScreen(
    tickets: List<TicketCardData>,
    selectedFilter: TicketListFilter,
    allCount: Int,
    activeCount: Int,
    expiredCount: Int,
    onFilterSelect: (TicketListFilter) -> Unit,
    onTicketClick: (String) -> Unit,
    onTicketShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
    ) {
        Text(
            text = "My Tickets",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = EventPassColors.Ink,
            modifier = Modifier.padding(
                start = Spacing.xl,
                end = Spacing.xl,
                top = Spacing.lg,
                bottom = Spacing.md
            )
        )

        Row(
            modifier = Modifier.padding(horizontal = Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            TicketFilterChip(
                label = "All",
                count = allCount,
                selected = selectedFilter == TicketListFilter.All,
                onClick = { onFilterSelect(TicketListFilter.All) },
                accent = EventPassColors.Primary
            )
            TicketFilterChip(
                label = "Active",
                count = activeCount,
                selected = selectedFilter == TicketListFilter.Active,
                onClick = { onFilterSelect(TicketListFilter.Active) },
                accent = EventPassColors.Success,
                showCheck = true
            )
            TicketFilterChip(
                label = "Expired",
                count = expiredCount,
                selected = selectedFilter == TicketListFilter.Expired,
                onClick = { onFilterSelect(TicketListFilter.Expired) },
                accent = EventPassColors.InkMuted
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        if (tickets.isEmpty()) {
            EmptyState(filter = selectedFilter)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = Spacing.xl,
                    end = Spacing.xl,
                    bottom = Spacing.xxxl
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                items(tickets, key = { it.id }) { ticket ->
                    TicketCard(
                        title = ticket.title,
                        ticketTypeText = ticket.ticketTypeText,
                        priceText = ticket.priceText,
                        dateText = ticket.dateText,
                        venueText = ticket.venueText,
                        status = ticket.status,
                        onClick = { onTicketClick(ticket.id) },
                        onShare = { onTicketShare(ticket.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(filter: TicketListFilter) {
    val (title, subtitle) = when (filter) {
        TicketListFilter.All -> "No tickets yet" to "Your purchased tickets will show up here."
        TicketListFilter.Active -> "No active tickets" to "Tickets for upcoming events will appear here."
        TicketListFilter.Expired -> "No expired tickets" to "Tickets from past events will appear here."
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ConfirmationNumber,
                contentDescription = null,
                tint = EventPassColors.InkSubtle,
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = EventPassColors.Ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
