package com.eventpass.feature.attendee.tickets.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.core.design.tokens.softShadow

/**
 * Status stripe colour for a ticket card — drives the header bar + badge.
 */
enum class TicketStatus { Active, Expired }

/**
 * Stateless card for a purchased ticket, matching the iOS reference (IMG_2770):
 *  - Top status strip (orange for Active, gray for Expired) with a status label
 *  - Title row
 *  - Ticket-type / price two-column
 *  - Divider
 *  - Date + venue meta rows
 *  - Leading Share pill and trailing chevron
 */
@Composable
fun TicketCard(
    title: String,
    ticketTypeText: String,
    priceText: String,
    dateText: String,
    venueText: String,
    status: TicketStatus,
    onClick: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = when (status) {
        TicketStatus.Active -> EventPassColors.Primary
        TicketStatus.Expired -> EventPassColors.InkSubtle
    }
    val statusLabel = when (status) {
        TicketStatus.Active -> "Active"
        TicketStatus.Expired -> "Expired"
    }
    val statusIcon: ImageVector = when (status) {
        TicketStatus.Active -> Icons.Filled.Check
        TicketStatus.Expired -> Icons.Outlined.ErrorOutline
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .softShadow(elevation = 6.dp, shape = Radii.CardLarge)
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .clickable(onClick = onClick)
    ) {
        StatusStrip(color = accent, label = statusLabel, icon = statusIcon)

        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink,
                maxLines = 2
            )

            Spacer(Modifier.height(Spacing.md))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ticket Type",
                        style = MaterialTheme.typography.labelSmall,
                        color = EventPassColors.InkMuted
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = ticketTypeText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = EventPassColors.Ink
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.labelSmall,
                        color = EventPassColors.InkMuted
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = priceText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = EventPassColors.Primary
                    )
                }
            }

            Spacer(Modifier.height(Spacing.md))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(EventPassColors.DividerLight)
            )
            Spacer(Modifier.height(Spacing.md))

            MetaRow(icon = Icons.Filled.CalendarToday, text = dateText)
            Spacer(Modifier.height(Spacing.sm))
            MetaRow(icon = Icons.Filled.LocationOn, text = venueText)

            Spacer(Modifier.height(Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SharePill(onClick = onShare)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = EventPassColors.InkSubtle,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusStrip(color: Color, label: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(EventPassColors.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EventPassColors.White,
                modifier = Modifier.size(10.dp)
            )
        }
        Spacer(Modifier.width(Spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.White
        )
    }
}

@Composable
private fun MetaRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EventPassColors.InkMuted,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(Spacing.sm))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = EventPassColors.InkMuted
        )
    }
}

@Composable
private fun SharePill(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(Radii.Pill)
            .background(EventPassColors.PrimarySoft)
            .border(1.dp, EventPassColors.PrimaryLight.copy(alpha = 0.4f), Radii.Pill)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = null,
            tint = EventPassColors.Primary,
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(Spacing.xs))
        Text(
            text = "Share",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Primary
        )
    }
}
