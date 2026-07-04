package com.eventpass.feature.organizer.createevent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.ui.graphics.vector.ImageVector

/** Event categories offered on the Create Event details step. */
enum class EventCategoryOption(val label: String, val icon: ImageVector) {
    MUSIC("Music", Icons.Filled.MusicNote),
    ARTS("Arts & Culture", Icons.Filled.Brush),
    CONCERTS("Concerts", Icons.Filled.Mic),
    SPORTS("Sports", Icons.Filled.DirectionsRun)
}

/** A venue autocomplete suggestion. */
data class VenueSuggestion(val name: String, val address: String)

/** Step 1 (Details) form state. */
data class EventDetailsState(
    val posterUrl: String? = null,
    val title: String = "",
    val description: String = "",
    val category: EventCategoryOption = EventCategoryOption.MUSIC,
    val startDateText: String = "19 Apr 2026",
    val startTimeText: String = "17:00",
    val endDateText: String = "19 Apr 2026",
    val endTimeText: String = "18:00",
    val venueQuery: String = "",
    val venueSuggestion: VenueSuggestion? = null,
    val address: String = "",
    val city: String = ""
) {
    val isValid: Boolean
        get() = title.isNotBlank() && description.isNotBlank() &&
            venueQuery.isNotBlank() && address.isNotBlank() && city.isNotBlank()
}

/** A single ticket type drafted on Step 2. */
data class TicketTypeDraft(
    val id: String,
    val name: String = "General Admission",
    val priceText: String = "0",
    val quantityText: String = "100",
    val unlimited: Boolean = false
) {
    val priceLabel: String
        get() = priceText.toLongOrNull()?.let { if (it == 0L) "Free" else "UGX $it" } ?: "UGX $priceText"
}

/** Read-only summary passed to Step 3 (Review). */
data class EventReviewData(
    val title: String,
    val categoryLabel: String,
    val whenText: String,
    val venueName: String,
    val venueAddress: String,
    val about: String,
    val tickets: List<TicketReviewItem>
)

data class TicketReviewItem(
    val name: String,
    val priceLabel: String,
    val availableText: String,
    val salesStartText: String,
    val salesEndText: String
)
