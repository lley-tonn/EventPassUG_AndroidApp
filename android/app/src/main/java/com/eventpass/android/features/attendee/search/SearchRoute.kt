package com.eventpass.android.features.attendee.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.eventpass.feature.attendee.search.SearchResult
import com.eventpass.feature.attendee.search.SearchScreen

/**
 * :app-side wrapper for the attendee Search screen. Holds the query locally;
 * results are placeholders until the search repository is wired in.
 */
@Composable
fun SearchRoute(
    onClose: () -> Unit,
    onResultClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    // TODO: query the events repository; for now show a sample result.
    val results = remember {
        listOf(
            SearchResult(
                id = "tis",
                title = "Tech Innovators Summit 2024",
                dateText = "25 Apr 2026",
                venueText = "Kampala",
                priceText = "UGX 50K",
                imageUrl = null,
                isFavorited = false
            )
        )
    }

    SearchScreen(
        query = query,
        results = results,
        onQueryChange = { query = it },
        onClose = onClose,
        onFilterClick = { /* TODO: apply filter */ },
        onResultClick = onResultClick,
        onFavoriteToggle = { /* TODO: toggle favorite */ },
        onShare = { /* TODO: share intent */ }
    )
}
