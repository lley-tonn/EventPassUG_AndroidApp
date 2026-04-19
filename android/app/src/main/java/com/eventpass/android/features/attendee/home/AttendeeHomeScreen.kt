package com.eventpass.android.features.attendee.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MicExternalOn
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventpass.android.domain.models.Event
import com.eventpass.feature.attendee.home.AttendeeHomeScreen as AttendeeHomeContent
import com.eventpass.feature.attendee.home.EventCardData
import com.eventpass.feature.attendee.home.components.CategoryItem
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * :app-side wrapper — owns the VM + domain mapping, delegates UI to
 * `:feature:attendee`'s stateless [AttendeeHomeContent].
 *
 * Search is inline (no navigation): this wrapper owns the active/query state,
 * debounces the query by ~300ms, and fuzzy-filters the already-loaded event
 * list before handing it to the UI.
 */
@Composable
fun AttendeeHomeScreen(
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

    val allEvents: List<Event> = when (val s = eventsState) {
        is com.eventpass.android.core.state.UiState.Success -> s.data
        else -> emptyList()
    }

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(300)
        debouncedQuery = searchQuery
    }

    val displayedEvents: List<EventCardData> = remember(allEvents, debouncedQuery, isSearchActive) {
        val source = if (isSearchActive && debouncedQuery.isNotBlank()) {
            allEvents.filter { it.matchesQuery(debouncedQuery) }
        } else {
            allEvents
        }
        source.map(Event::toCardData)
    }

    AttendeeHomeContent(
        dateLabel = today.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
        greeting = greetingFor(LocalDateTime.now(), uiState.currentUser?.fullName),
        unreadCount = 0,
        categories = categories,
        selectedCategoryId = null,
        events = displayedEvents,
        favoritedIds = uiState.likedEventIds,
        isSearchActive = isSearchActive,
        searchQuery = searchQuery,
        onSearchActivate = { isSearchActive = true },
        onSearchQueryChange = { searchQuery = it },
        onSearchCancel = {
            searchQuery = ""
            debouncedQuery = ""
            isSearchActive = false
        },
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

/**
 * Token-wise fuzzy match over the fields attendees search by: title,
 * category name, venue name, venue city. Each whitespace-separated token in
 * the query must either appear as a substring (partial match) or be found as
 * a subsequence in at least one haystack field (typo tolerance — "musik" hits
 * "music", "cncrt" hits "concert").
 */
private fun Event.matchesQuery(raw: String): Boolean {
    val tokens = raw.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (tokens.isEmpty()) return true
    val haystacks = listOf(
        title.lowercase(),
        category.displayName.lowercase(),
        venue.name.lowercase(),
        venue.city.lowercase()
    )
    return tokens.all { token ->
        haystacks.any { it.contains(token) || isSubsequence(token, it) }
    }
}

private fun isSubsequence(needle: String, haystack: String): Boolean {
    if (needle.isEmpty()) return true
    var i = 0
    for (c in haystack) {
        if (c == needle[i]) {
            i++
            if (i == needle.length) return true
        }
    }
    return false
}

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
    CategoryItem("arts", "Arts & Culture", Icons.Filled.Brush),
    CategoryItem("concerts", "Concerts", Icons.Filled.MicExternalOn),
    CategoryItem("sports", "Sports & Wellness", Icons.AutoMirrored.Filled.DirectionsRun),
    CategoryItem("tech", "Technology", Icons.Filled.Computer),
    CategoryItem("fundraising", "Fundraising", Icons.Filled.VolunteerActivism),
    CategoryItem("comedy", "Comedy", Icons.Filled.TheaterComedy),
    CategoryItem("poetry", "Poetry", Icons.AutoMirrored.Filled.MenuBook),
    CategoryItem("drama", "Drama", Icons.Filled.Movie),
    CategoryItem("exhibitions", "Exhibitions", Icons.Filled.Museum),
    CategoryItem("networking", "Networking", Icons.Filled.Groups),
    CategoryItem("education", "Education", Icons.Filled.School),
    CategoryItem("food", "Food & Drinks", Icons.Filled.Restaurant),
    CategoryItem("nightlife", "Nightlife", Icons.Filled.ModeNight),
    CategoryItem("festivals", "Festivals", Icons.Filled.Celebration),
    CategoryItem("wellness", "Wellness", Icons.Filled.Favorite),
    CategoryItem("other", "Others", Icons.Filled.MoreHoriz)
)
