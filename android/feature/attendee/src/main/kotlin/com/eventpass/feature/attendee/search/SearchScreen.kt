package com.eventpass.feature.attendee.search

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.EventCard
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/** A quick-filter shown under the search field. */
data class SearchFilter(val label: String, val icon: ImageVector)

/** An event result rendered on the search screen. */
data class SearchResult(
    val id: String,
    val title: String,
    val dateText: String,
    val venueText: String,
    val priceText: String,
    val imageUrl: String?,
    val isFavorited: Boolean
)

private val defaultFilters = listOf(
    SearchFilter("Today", Icons.Filled.CalendarMonth),
    SearchFilter("This week", Icons.Filled.DateRange),
    SearchFilter("This month", Icons.Filled.Event),
    SearchFilter("Music", Icons.Filled.MusicNote),
    SearchFilter("Arts & Culture", Icons.Filled.Brush)
)

/**
 * Search (design reference IMG_2824). A prominent search field, a scrollable rail
 * of quick filters, and event results. Stateless — the caller owns [query] and
 * supplies results.
 */
@Composable
fun SearchScreen(
    query: String,
    results: List<SearchResult>,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onFilterClick: (SearchFilter) -> Unit,
    onResultClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
            .statusBarsPadding()
    ) {
        // Close button
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.xl, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(EventPassColors.White)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = EventPassColors.Ink, modifier = Modifier.size(22.dp))
            }
        }

        // Search field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .clip(Radii.CardLarge)
                .background(EventPassColors.White)
                .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = EventPassColors.InkMuted, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(Spacing.md))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("Search events, organizers, locations", style = MaterialTheme.typography.bodyLarge, color = EventPassColors.InkSubtle, maxLines = 1)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = EventPassColors.Ink),
                    singleLine = true,
                    cursorBrush = SolidColor(EventPassColors.Primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Clear",
                    tint = EventPassColors.InkMuted,
                    modifier = Modifier.size(20.dp).clickable { onQueryChange("") }
                )
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        // Filter rail
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            defaultFilters.forEach { filter ->
                FilterItem(filter = filter, onClick = { onFilterClick(filter) })
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        // Results
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            results.forEach { result ->
                EventCard(
                    title = result.title,
                    dateText = result.dateText,
                    venueText = result.venueText,
                    priceText = result.priceText,
                    onClick = { onResultClick(result.id) },
                    imageUrl = result.imageUrl,
                    isFavorited = result.isFavorited,
                    onFavoriteToggle = { onFavoriteToggle(result.id) },
                    onShare = { onShare(result.id) }
                )
            }
            Spacer(Modifier.height(Spacing.xxxl))
        }
    }
}

@Composable
private fun FilterItem(filter: SearchFilter, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(filter.icon, contentDescription = null, tint = EventPassColors.Ink, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(Spacing.xs))
        Text(
            filter.label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
    }
}
