package com.eventpass.feature.organizer.createevent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/**
 * Create Event — Step 2: Tickets (design reference IMG_2804). A list of editable
 * ticket-type cards plus an "Add Ticket Type" button.
 */
@Composable
fun CreateEventTicketsScreen(
    tickets: List<TicketTypeDraft>,
    onNameChange: (String, String) -> Unit,
    onPriceChange: (String, String) -> Unit,
    onQuantityChange: (String, String) -> Unit,
    onUnlimitedToggle: (String, Boolean) -> Unit,
    onEditAvailability: (String) -> Unit,
    onDelete: (String) -> Unit,
    onAddTicketType: () -> Unit,
    onCancel: () -> Unit,
    onSaveDraft: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    CreateEventScaffold(
        currentStep = 1,
        onCancel = onCancel,
        onSaveDraft = onSaveDraft,
        modifier = modifier,
        footer = {
            WizardFooter(
                primaryLabel = "Continue",
                onPrimary = onContinue,
                primaryEnabled = tickets.isNotEmpty(),
                onBack = onBack
            )
        }
    ) {
        Text(
            "Add ticket types and pricing for your event",
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted
        )
        Spacer(Modifier.height(Spacing.lg))

        tickets.forEach { ticket ->
            TicketTypeCard(
                ticket = ticket,
                onNameChange = { onNameChange(ticket.id, it) },
                onPriceChange = { onPriceChange(ticket.id, it) },
                onQuantityChange = { onQuantityChange(ticket.id, it) },
                onUnlimitedToggle = { onUnlimitedToggle(ticket.id, it) },
                onEditAvailability = { onEditAvailability(ticket.id) },
                onDelete = { onDelete(ticket.id) }
            )
            Spacer(Modifier.height(Spacing.lg))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(Radii.CardLarge)
                .border(1.5.dp, EventPassColors.Primary, Radii.CardLarge)
                .clickable(onClick = onAddTicketType),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(26.dp).clip(CircleShape).background(EventPassColors.Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = EventPassColors.White, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(Spacing.md))
            Text("Add Ticket Type", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = EventPassColors.Primary)
        }
    }
}

@Composable
private fun TicketTypeCard(
    ticket: TicketTypeDraft,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onUnlimitedToggle: (Boolean) -> Unit,
    onEditAvailability: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .border(1.dp, EventPassColors.Success.copy(alpha = 0.5f), Radii.CardLarge)
            .padding(Spacing.lg)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EventPassColors.Success, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(Spacing.sm))
            Text("Ticket Type", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = EventPassColors.Ink, modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = EventPassColors.Error,
                modifier = Modifier.size(22.dp).clickable(onClick = onDelete)
            )
        }

        Spacer(Modifier.height(Spacing.lg))
        FilledTextField(ticket.name, onNameChange, "Ticket name", leadingIcon = Icons.Filled.ConfirmationNumber)

        Spacer(Modifier.height(Spacing.lg))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Price (UGX)", style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
                Spacer(Modifier.height(Spacing.sm))
                FilledTextField(ticket.priceText, onPriceChange, "0", keyboardType = KeyboardType.Number)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Quantity", style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted, modifier = Modifier.weight(1f))
                    Switch(
                        checked = ticket.unlimited,
                        onCheckedChange = onUnlimitedToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EventPassColors.White,
                            checkedTrackColor = EventPassColors.Primary,
                            checkedBorderColor = EventPassColors.Primary
                        )
                    )
                }
                Text("Unlimited", style = MaterialTheme.typography.bodySmall, color = EventPassColors.InkMuted)
                Spacer(Modifier.height(Spacing.xs))
                if (!ticket.unlimited) {
                    FilledTextField(ticket.quantityText, onQuantityChange, "100", keyboardType = KeyboardType.Number)
                }
            }
        }

        Spacer(Modifier.height(Spacing.lg))
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onEditAvailability),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.History, contentDescription = null, tint = EventPassColors.Primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Spacing.sm))
            Text("Availability Window", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = EventPassColors.Primary, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = EventPassColors.Primary, modifier = Modifier.size(20.dp))
        }
    }
}
