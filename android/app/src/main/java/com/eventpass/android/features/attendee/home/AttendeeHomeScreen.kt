package com.eventpass.android.features.attendee.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventpass.android.domain.models.Event
import com.eventpass.feature.attendee.home.AttendeeHomeScreen as AttendeeHomeContent
import com.eventpass.feature.attendee.home.EventCardData
import com.eventpass.feature.attendee.home.components.CategoryItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * :app-side wrapper — owns the VM + domain mapping, delegates UI to
 * `:feature:attendee`'s stateless [AttendeeHomeContent].
 */
@Composable
fun AttendeeHomeScreen(
    onSearchClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onEventClick: (String) -> Unit = {},
    viewModel: AttendeeHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val eventsState by viewModel.eventsState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadEvents() }

    val categories = remember { defaultHomeCategories() }
    val today = remember { LocalDate.now() }

    val events = when (val s = eventsState) {
        is com.eventpass.android.core.state.UiState.Success -> s.data.map(Event::toCardData)
        else -> emptyList()
    }

    AttendeeHomeContent(
        dateLabel = today.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
        greeting = greetingFor(LocalDateTime.now(), uiState.currentUser?.fullName),
        unreadCount = 0,
        categories = categories,
        selectedCategoryId = null,
        events = events,
        favoritedIds = uiState.likedEventIds,
        onSearchClick = onSearchClick,
        onFavoritesClick = onFavoritesClick,
        onNotificationsClick = onNotificationsClick,
        onCategorySelect = { /* TODO: map to VM filters */ },
        onEventClick = onEventClick,
        onFavoriteToggle = viewModel::toggleEventLike,
        onEventShare = { /* TODO: wire share intent */ }
    )
}

private val EventDateFormat = DateTimeFormatter.ofPattern("d MMM yyyy 'at' HH:mm")

private fun Event.toCardData(): EventCardData = EventCardData(
    id = id,
    title = title,
    dateText = startDate.format(EventDateFormat),
    venueText = venue.name,
    priceText = priceRange,
    imageUrl = posterUrl,
    isHappeningNow = isHappeningNow,
    rating = rating.takeIf { it > 0 },
    ratingCount = totalRatings.takeIf { it > 0 }
)

private fun greetingFor(now: LocalDateTime, name: String?): String {
    val label = when (now.hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Hello"
    }
    val who = name?.substringBefore(' ') ?: "Guest"
    return "$label, $who!"
}

private fun defaultHomeCategories() = listOf(
    CategoryItem("today", "Today", Icons.Filled.Today),
    CategoryItem("week", "This week", Icons.Filled.DateRange),
    CategoryItem("month", "This month", Icons.Filled.CalendarMonth),
    CategoryItem("music", "Music", Icons.Filled.MusicNote),
    CategoryItem("arts", "Arts & Culture", Icons.Filled.Brush)
)
