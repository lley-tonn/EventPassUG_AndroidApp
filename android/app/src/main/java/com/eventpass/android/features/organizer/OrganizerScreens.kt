package com.eventpass.android.features.organizer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.eventpass.feature.organizer.createevent.CreateEventDetailsScreen
import com.eventpass.feature.organizer.createevent.CreateEventReviewScreen
import com.eventpass.feature.organizer.createevent.CreateEventTicketsScreen
import com.eventpass.feature.organizer.createevent.EventDetailsState
import com.eventpass.feature.organizer.createevent.EventReviewData
import com.eventpass.feature.organizer.createevent.TicketReviewItem
import com.eventpass.feature.organizer.createevent.TicketTypeDraft
import com.eventpass.feature.organizer.createevent.VenueSuggestion
import com.eventpass.feature.organizer.home.EventStatusTab
import com.eventpass.feature.organizer.home.OrganizerEventSummary
import com.eventpass.feature.organizer.home.OrganizerHomeScreen
import com.eventpass.feature.organizer.scanner.ScanTicketScreen
import com.eventpass.feature.organizer.scanner.ScannerDevicesScreen
import com.eventpass.feature.organizer.scanner.ScannerEvent
import java.util.UUID

/**
 * :app-side wrapper for the organizer Home tab. Uses placeholder events until
 * the events repository is wired in.
 */
@Composable
fun OrganizerHomeRoute(
    greeting: String,
    onCreateEvent: () -> Unit,
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onEventClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(EventStatusTab.PUBLISHED) }

    // TODO: source events + counts from the events repository.
    val allEvents = remember {
        listOf(
            OrganizerEventSummary(
                id = "tobo",
                title = "Tobo",
                dateText = "25 Apr 2026 at 17:00",
                posterUrl = null,
                ticketsSold = 0,
                likes = 0
            )
        )
    }
    val counts = mapOf(
        EventStatusTab.PUBLISHED to allEvents.size,
        EventStatusTab.DRAFT to 0,
        EventStatusTab.ONGOING to 0,
        EventStatusTab.COMPLETED to 0
    )
    val events = if (selectedTab == EventStatusTab.PUBLISHED) allEvents else emptyList()

    OrganizerHomeScreen(
        dateLabel = "Sat, Apr 18",
        greeting = greeting,
        unreadCount = 2,
        counts = counts,
        selectedTab = selectedTab,
        events = events,
        onTabSelected = { selectedTab = it },
        onSearch = onSearch,
        onNotifications = onNotifications,
        onCreateEvent = onCreateEvent,
        onEventClick = onEventClick
    )
}

/**
 * :app-side host for the 3-step Create Event wizard. Owns all form state and
 * advances through Details → Tickets → Review internally.
 */
@Composable
fun CreateEventFlow(
    onClose: () -> Unit,
    onPublished: () -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var details by remember { mutableStateOf(EventDetailsState()) }
    val tickets = remember {
        mutableStateListOf(TicketTypeDraft(id = UUID.randomUUID().toString()))
    }

    fun updateTicket(id: String, transform: (TicketTypeDraft) -> TicketTypeDraft) {
        val index = tickets.indexOfFirst { it.id == id }
        if (index >= 0) tickets[index] = transform(tickets[index])
    }

    when (step) {
        0 -> CreateEventDetailsScreen(
            state = details,
            onPickPoster = { /* TODO: image picker */ },
            onTitleChange = { details = details.copy(title = it) },
            onDescriptionChange = { details = details.copy(description = it) },
            onCategoryChange = { details = details.copy(category = it) },
            onEditStart = { /* TODO: date/time picker */ },
            onEditEnd = { /* TODO: date/time picker */ },
            onVenueQueryChange = {
                details = details.copy(
                    venueQuery = it,
                    venueSuggestion = if (it.isBlank()) null
                    else VenueSuggestion(it, "$it, 1123 København K, Denmark")
                )
            },
            onVenueClear = { details = details.copy(venueQuery = "", venueSuggestion = null) },
            onVenueSelected = { suggestion ->
                details = details.copy(
                    venueQuery = suggestion.name,
                    address = suggestion.name,
                    city = suggestion.address.substringAfter(", ").substringBeforeLast(","),
                    venueSuggestion = null
                )
            },
            onAddressChange = { details = details.copy(address = it) },
            onCityChange = { details = details.copy(city = it) },
            onCancel = onClose,
            onSaveDraft = onClose,
            onContinue = { step = 1 }
        )

        1 -> CreateEventTicketsScreen(
            tickets = tickets,
            onNameChange = { id, v -> updateTicket(id) { it.copy(name = v) } },
            onPriceChange = { id, v -> updateTicket(id) { it.copy(priceText = v) } },
            onQuantityChange = { id, v -> updateTicket(id) { it.copy(quantityText = v) } },
            onUnlimitedToggle = { id, v -> updateTicket(id) { it.copy(unlimited = v) } },
            onEditAvailability = { /* TODO: availability window sheet */ },
            onDelete = { id -> tickets.removeAll { it.id == id } },
            onAddTicketType = { tickets.add(TicketTypeDraft(id = UUID.randomUUID().toString(), name = "New Ticket Type")) },
            onCancel = onClose,
            onSaveDraft = onClose,
            onBack = { step = 0 },
            onContinue = { step = 2 }
        )

        else -> CreateEventReviewScreen(
            data = details.toReviewData(tickets),
            onEditDetails = { step = 0 },
            onEditTickets = { step = 1 },
            onCancel = onClose,
            onSaveDraft = onClose,
            onBack = { step = 1 },
            onPublish = onPublished
        )
    }
}

private fun EventDetailsState.toReviewData(tickets: List<TicketTypeDraft>): EventReviewData =
    EventReviewData(
        title = title,
        categoryLabel = category.label,
        whenText = "$startDateText at $startTimeText\nto\n$endDateText at $endTimeText",
        venueName = venueQuery.ifBlank { "Venue" },
        venueAddress = address,
        about = description,
        tickets = tickets.map { t ->
            TicketReviewItem(
                name = t.name,
                priceLabel = t.priceLabel,
                availableText = if (t.unlimited) "Unlimited available" else "${t.quantityText} available",
                salesStartText = "$startDateText at $startTimeText",
                salesEndText = "$endDateText at $endTimeText (Event starts)"
            )
        }
    )

/** :app-side wrapper for the Scanner Devices event picker. */
@Composable
fun ScannerDevicesRoute(
    onBack: () -> Unit,
    onEventClick: (String) -> Unit
) {
    // TODO: source published/active events from the events repository.
    val events = remember {
        listOf(
            ScannerEvent("smf", "Summer Music Festival", "18 Apr 2026 at 15:46", "Published", null),
            ScannerEvent("tis", "Tech Innovators Summit 2024", "25 Apr 2026 at 16:46", "Published", null),
            ScannerEvent("crfe", "Charity Run for Education", "20 Apr 2026 at 16:46", "Published", null),
            ScannerEvent("cae", "Contemporary Art Exhibition", "3 May 2026 at 16:46", "Published", null)
        )
    }
    ScannerDevicesScreen(events = events, onBack = onBack, onEventClick = onEventClick)
}

/** :app-side wrapper for the Scan Ticket camera. */
@Composable
fun ScanTicketRoute(onCancel: () -> Unit) {
    ScanTicketScreen(
        onCancel = onCancel,
        onScanned = {
            // TODO: validate the scanned ticket + show result
            onCancel()
        }
    )
}
