package com.eventpass.feature.organizer.createevent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/**
 * Create Event — Step 3: Review (iOS reference IMG_2805/2806). Read-only
 * summary of the event and its ticket types, with per-section Edit shortcuts
 * and the final Publish action.
 */
@Composable
fun CreateEventReviewScreen(
    data: EventReviewData,
    onEditDetails: () -> Unit,
    onEditTickets: () -> Unit,
    onCancel: () -> Unit,
    onSaveDraft: () -> Unit,
    onBack: () -> Unit,
    onPublish: () -> Unit,
    modifier: Modifier = Modifier
) {
    CreateEventScaffold(
        currentStep = 2,
        onCancel = onCancel,
        onSaveDraft = onSaveDraft,
        modifier = modifier,
        footer = {
            WizardFooter(
                primaryLabel = "Publish Event",
                onPrimary = onPublish,
                primaryEnabled = true,
                onBack = onBack
            )
        }
    ) {
        Text(
            "Review Your Event",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = EventPassColors.Ink
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            "Double-check everything looks perfect before publishing",
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted
        )

        Spacer(Modifier.height(Spacing.lg))

        // Event summary card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Radii.CardLarge)
                .background(EventPassColors.White)
                .padding(Spacing.lg)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    data.title.ifBlank { "Untitled Event" },
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = EventPassColors.Ink,
                    modifier = Modifier.weight(1f)
                )
                EditPill(onClick = onEditDetails)
            }
            Spacer(Modifier.height(Spacing.md))
            CategoryChip(data.categoryLabel)

            Spacer(Modifier.height(Spacing.lg))
            Box(Modifier.fillMaxWidth().height(0.5.dp).background(EventPassColors.DividerLight))
            Spacer(Modifier.height(Spacing.lg))

            InfoRow(Icons.Filled.CalendarMonth, "When", data.whenText)
            Spacer(Modifier.height(Spacing.lg))
            InfoRow(Icons.Filled.NearMe, "Where", data.venueName, data.venueAddress)

            if (data.about.isNotBlank()) {
                Spacer(Modifier.height(Spacing.lg))
                Box(Modifier.fillMaxWidth().height(0.5.dp).background(EventPassColors.DividerLight))
                Spacer(Modifier.height(Spacing.lg))
                Text("About", style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
                Spacer(Modifier.height(Spacing.xs))
                Text(data.about, style = MaterialTheme.typography.bodyLarge, color = EventPassColors.Ink)
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        // Ticket types
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Ticket Types", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = EventPassColors.Ink)
                Text("${data.tickets.size} types available", style = MaterialTheme.typography.bodyLarge, color = EventPassColors.InkMuted)
            }
            EditPill(onClick = onEditTickets)
        }
        Spacer(Modifier.height(Spacing.md))
        data.tickets.forEach { ticket ->
            TicketReviewCard(ticket)
            Spacer(Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun EditPill(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(Radii.Pill)
            .background(EventPassColors.PrimarySoft)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Edit, contentDescription = null, tint = EventPassColors.Primary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(Spacing.xs))
        Text("Edit", style = MaterialTheme.typography.titleMedium, color = EventPassColors.Primary)
    }
}

@Composable
private fun CategoryChip(label: String) {
    Row(
        modifier = Modifier
            .clip(Radii.Pill)
            .background(EventPassColors.PrimarySoft)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.MusicNote, contentDescription = null, tint = EventPassColors.Primary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(Spacing.xs))
        Text(label, style = MaterialTheme.typography.titleMedium, color = EventPassColors.Primary)
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, sub: String? = null) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = EventPassColors.Primary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
            Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = EventPassColors.Ink)
            if (sub != null) {
                Text(sub, style = MaterialTheme.typography.bodyLarge, color = EventPassColors.InkMuted)
            }
        }
    }
}

@Composable
private fun TicketReviewCard(ticket: TicketReviewItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .padding(Spacing.lg)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ticket.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = EventPassColors.Ink)
                Text(ticket.availableText, style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
            }
            Text(ticket.priceLabel, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = EventPassColors.Primary)
        }
        Spacer(Modifier.height(Spacing.md))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radii.sm))
                .background(EventPassColors.BackgroundLight)
                .padding(Spacing.md)
        ) {
            Text("Sale Period", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = EventPassColors.Ink)
            Spacer(Modifier.height(Spacing.xs))
            Row {
                Text("Starts: ", style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
                Text(ticket.salesStartText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = EventPassColors.Ink)
            }
            Row {
                Text("Ends: ", style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
                Text(ticket.salesEndText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = EventPassColors.Primary)
            }
        }
    }
}
