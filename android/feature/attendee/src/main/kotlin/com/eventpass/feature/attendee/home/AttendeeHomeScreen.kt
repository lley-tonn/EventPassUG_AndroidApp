package com.eventpass.feature.attendee.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.EventCard
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.attendee.home.components.CategoryItem
import com.eventpass.feature.attendee.home.components.CategoryRail
import com.eventpass.feature.attendee.home.components.HomeHeader

/**
 * Summary data for one event card. `:feature:attendee` stays free of domain
 * deps — callers in `:app` map from the domain `Event` into this record.
 */
data class EventCardData(
    val id: String,
    val title: String,
    val dateText: String,
    val venueText: String,
    val priceText: String,
    val imageUrl: String?,
    val isHappeningNow: Boolean,
    val rating: Double?,
    val ratingCount: Int?
)

/**
 * Attendee home screen — matches iOS reference IMG_2757.
 *
 * Stateless: every piece of state (header meta, category rail, event list,
 * favorites) is hoisted to the caller. The `:app` NavHost wires up the VM.
 */
@Composable
fun AttendeeHomeScreen(
    dateLabel: String,
    greeting: String,
    unreadCount: Int,
    categories: List<CategoryItem>,
    selectedCategoryId: String?,
    events: List<EventCardData>,
    favoritedIds: Set<String>,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCategorySelect: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onEventShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
    ) {
        HomeHeader(
            dateLabel = dateLabel,
            greeting = greeting,
            unreadCount = unreadCount,
            onSearch = onSearchClick,
            onFavorites = onFavoritesClick,
            onNotifications = onNotificationsClick
        )

        Spacer(Modifier.height(Spacing.sm))

        CategoryRail(
            items = categories,
            selectedId = selectedCategoryId,
            onSelect = onCategorySelect
        )

        Spacer(Modifier.height(Spacing.lg))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = Spacing.xl,
                end = Spacing.xl,
                bottom = Spacing.xxxl
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            items(events, key = { it.id }) { event ->
                EventCard(
                    title = event.title,
                    dateText = event.dateText,
                    venueText = event.venueText,
                    priceText = event.priceText,
                    onClick = { onEventClick(event.id) },
                    imageUrl = event.imageUrl,
                    isHappeningNow = event.isHappeningNow,
                    isFavorited = favoritedIds.contains(event.id),
                    onFavoriteToggle = { onFavoriteToggle(event.id) },
                    onShare = { onEventShare(event.id) },
                    rating = event.rating,
                    ratingCount = event.ratingCount
                )
            }
        }
    }
}
