package com.eventpass.android.features.attendee.tickets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventpass.android.domain.models.Ticket
import com.eventpass.feature.attendee.tickets.MyTicketsScreen as MyTicketsContent
import com.eventpass.feature.attendee.tickets.TicketCardData
import com.eventpass.feature.attendee.tickets.TicketListFilter
import com.eventpass.feature.attendee.tickets.components.TicketStatus
import java.time.format.DateTimeFormatter

/**
 * :app-side wrapper — owns the VM + domain→UI mapping, delegates to the
 * stateless `:feature:attendee` screen.
 */
@Composable
fun MyTicketsScreen(
    onTicketClick: (String) -> Unit = {},
    viewModel: MyTicketsViewModel = hiltViewModel()
) {
    val filtered by viewModel.filteredTickets.collectAsState()
    val counts by viewModel.ticketCounts.collectAsState()
    val filter by viewModel.selectedFilter.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadTickets() }

    MyTicketsContent(
        tickets = filtered.map(Ticket::toCardData),
        selectedFilter = filter.toListFilter(),
        allCount = counts.all,
        activeCount = counts.active,
        expiredCount = counts.expired,
        onFilterSelect = { viewModel.setFilter(it.toVmFilter()) },
        onTicketClick = onTicketClick,
        onTicketShare = { /* TODO: wire share intent */ }
    )
}

private val TicketDateFormat = DateTimeFormatter.ofPattern("d MMM yyyy 'at' HH:mm")

private fun Ticket.toCardData(): TicketCardData = TicketCardData(
    id = id,
    title = eventTitle,
    ticketTypeText = ticketType.name,
    priceText = formattedPrice,
    dateText = eventDate.format(TicketDateFormat),
    venueText = eventVenue,
    status = if (isExpired) TicketStatus.Expired else TicketStatus.Active
)

private fun TicketFilter.toListFilter(): TicketListFilter = when (this) {
    TicketFilter.ALL -> TicketListFilter.All
    TicketFilter.ACTIVE -> TicketListFilter.Active
    TicketFilter.EXPIRED -> TicketListFilter.Expired
}

private fun TicketListFilter.toVmFilter(): TicketFilter = when (this) {
    TicketListFilter.All -> TicketFilter.ALL
    TicketListFilter.Active -> TicketFilter.ACTIVE
    TicketListFilter.Expired -> TicketFilter.EXPIRED
}
