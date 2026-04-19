package com.eventpass.feature.attendee.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.eventpass.core.design.components.EventCard
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.attendee.home.components.CategoryItem
import com.eventpass.feature.attendee.home.components.CategoryRail
import com.eventpass.feature.attendee.home.components.HomeHeader
import com.eventpass.feature.attendee.home.components.InlineSearchBar

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
 * favorites, search) is hoisted to the caller.
 *
 * Search is inline: when [isSearchActive] flips true the header morphs into
 * [InlineSearchBar] in place (no navigation, no new screen). The event list
 * below updates live from the caller-supplied (already filtered) [events].
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
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchActivate: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchCancel: () -> Unit,
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
        AnimatedContent(
            targetState = isSearchActive,
            transitionSpec = {
                (fadeIn() + slideInVertically { -it / 3 })
                    .togetherWith(fadeOut() + slideOutVertically { -it / 3 })
            },
            label = "headerSearchMorph"
        ) { active ->
            if (active) {
                InlineSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onCancel = onSearchCancel
                )
            } else {
                HomeHeader(
                    dateLabel = dateLabel,
                    greeting = greeting,
                    unreadCount = unreadCount,
                    onSearch = onSearchActivate,
                    onFavorites = onFavoritesClick,
                    onNotifications = onNotificationsClick
                )
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        CategoryRail(
            items = categories,
            selectedId = selectedCategoryId,
            onSelect = onCategorySelect
        )

        Spacer(Modifier.height(Spacing.lg))

        if (isSearchActive && searchQuery.isNotBlank() && events.isEmpty()) {
            EmptyResults(query = searchQuery)
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
}

@Composable
private fun EmptyResults(query: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No events match \u201C$query\u201D",
                style = MaterialTheme.typography.titleMedium,
                color = EventPassColors.Ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Try a different word, category, or venue.",
                style = MaterialTheme.typography.bodySmall,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
