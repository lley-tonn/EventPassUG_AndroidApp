package com.eventpass.feature.organizer.createevent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.PillChip
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/**
 * Create Event — Step 1: Details (iOS reference IMG_2801/2802/2803). Poster,
 * title, description, category, start/end date-time, and venue/address/city.
 */
@Composable
fun CreateEventDetailsScreen(
    state: EventDetailsState,
    onPickPoster: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (EventCategoryOption) -> Unit,
    onEditStart: () -> Unit,
    onEditEnd: () -> Unit,
    onVenueQueryChange: (String) -> Unit,
    onVenueClear: () -> Unit,
    onVenueSelected: (VenueSuggestion) -> Unit,
    onAddressChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSaveDraft: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    CreateEventScaffold(
        currentStep = 0,
        onCancel = onCancel,
        onSaveDraft = onSaveDraft,
        modifier = modifier,
        footer = {
            WizardFooter(
                primaryLabel = "Continue",
                onPrimary = onContinue,
                primaryEnabled = state.isValid
            )
        }
    ) {
        WizardLabel("Event Poster")
        PosterPicker(onClick = onPickPoster)

        Spacer(Modifier.height(Spacing.xl))
        WizardLabel("Event Title")
        FilledTextField(state.title, onTitleChange, "Enter event title")

        Spacer(Modifier.height(Spacing.xl))
        WizardLabel("Description")
        FilledTextField(
            state.description, onDescriptionChange,
            "Describe your event, what attendees can expect...",
            minLines = 4
        )

        Spacer(Modifier.height(Spacing.xl))
        WizardLabel("Category")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            EventCategoryOption.entries.forEach { option ->
                PillChip(
                    text = option.label,
                    selected = option == state.category,
                    onClick = { onCategoryChange(option) },
                    leadingIcon = option.icon
                )
            }
        }

        Spacer(Modifier.height(Spacing.xl))
        DateTimeRow("Start Date & Time", state.startDateText, state.startTimeText, onEditStart)
        Spacer(Modifier.height(Spacing.xl))
        DateTimeRow("End Date & Time", state.endDateText, state.endTimeText, onEditEnd)

        Spacer(Modifier.height(Spacing.xl))
        WizardLabel("Venue Name")
        FilledTextField(
            value = state.venueQuery,
            onValueChange = onVenueQueryChange,
            placeholder = "Search for a venue...",
            leadingIcon = Icons.Filled.Search
        )
        if (state.venueQuery.isNotEmpty() && state.venueSuggestion != null) {
            Spacer(Modifier.height(Spacing.sm))
            VenueSuggestionRow(state.venueSuggestion, onClick = { onVenueSelected(state.venueSuggestion) })
        }

        Spacer(Modifier.height(Spacing.xl))
        WizardLabel("Address")
        FilledTextField(state.address, onAddressChange, "Street address", leadingIcon = Icons.Filled.Place)

        Spacer(Modifier.height(Spacing.xl))
        WizardLabel("City")
        FilledTextField(state.city, onCityChange, "City", leadingIcon = Icons.Filled.Apartment)
    }
}

@Composable
private fun PosterPicker(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(Radii.CardLarge)
            .background(EventPassColors.BackgroundLight)
            .border(1.dp, EventPassColors.DividerLight, Radii.CardLarge)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = EventPassColors.InkSubtle, modifier = Modifier.size(52.dp))
        Spacer(Modifier.height(Spacing.sm))
        Text("Tap to select poster", style = MaterialTheme.typography.bodyLarge, color = EventPassColors.InkMuted)
    }
}

@Composable
private fun DateTimeRow(label: String, dateText: String, timeText: String, onEdit: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        WizardLabel(label, modifier = Modifier.weight(1f).padding(bottom = 0.dp))
        DateTimePill(dateText, onEdit)
        Spacer(Modifier.width(Spacing.sm))
        DateTimePill(timeText, onEdit)
    }
}

@Composable
private fun VenueSuggestionRow(suggestion: VenueSuggestion, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.Card)
            .background(EventPassColors.White)
            .border(1.dp, EventPassColors.DividerLight, Radii.Card)
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(EventPassColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = EventPassColors.White, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(suggestion.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = EventPassColors.Ink)
            Text(suggestion.address, style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
        }
    }
}
