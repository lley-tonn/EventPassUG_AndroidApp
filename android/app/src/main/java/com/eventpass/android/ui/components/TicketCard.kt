package com.eventpass.android.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eventpass.android.core.util.DateUtils
import com.eventpass.android.domain.models.Ticket
import com.eventpass.android.domain.models.TicketScanStatus
import com.eventpass.android.ui.theme.EventPassColors
import com.eventpass.android.ui.theme.EventPassDimensions

/**
 * Ticket card component.
 * Displays a purchased ticket with event details and scan status.
 */
@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(EventPassDimensions.CornerRadius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EventPassDimensions.Spacing.md)
        ) {
            // Event poster thumbnail
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(EventPassDimensions.CornerRadius.sm))
            ) {
                if (ticket.eventPosterUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ticket.eventPosterUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Scan status badge
                TicketStatusBadge(
                    status = ticket.scanStatus,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.width(EventPassDimensions.Spacing.md))

            // Ticket details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs)
            ) {
                Text(
                    text = ticket.eventTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = DateUtils.formatDateTime(ticket.eventDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Venue
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = ticket.eventVenue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Ticket type and number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ticket.ticketType.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = EventPassColors.Primary,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = ticket.ticketNumber,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Ticket status badge.
 */
@Composable
fun TicketStatusBadge(
    status: TicketScanStatus,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (status) {
        TicketScanStatus.UNUSED -> EventPassColors.Success to Icons.Default.QrCode2
        TicketScanStatus.SCANNED -> EventPassColors.Warning to Icons.Default.CheckCircle
        TicketScanStatus.EXPIRED -> EventPassColors.Gray400 to null
    }

    if (icon != null) {
        Box(
            modifier = modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.displayName,
                modifier = Modifier.size(12.dp),
                tint = Color.White
            )
        }
    }
}

/**
 * Large ticket card for detail view.
 */
@Composable
fun TicketDetailCard(
    ticket: Ticket,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(EventPassDimensions.CornerRadius.xl),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(EventPassDimensions.Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // QR Code
            TicketQRCode(
                ticketId = ticket.id,
                ticketNumber = ticket.ticketNumber,
                size = 200.dp
            )

            Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.lg))

            // Ticket type badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(EventPassColors.Primary.copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = ticket.ticketType.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = EventPassColors.Primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.md))

            // Event title
            Text(
                text = ticket.eventTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.md))

            // Event details
            Column(
                verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm)
            ) {
                DetailRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Date & Time",
                    value = DateUtils.formatDateTime(ticket.eventDate)
                )

                DetailRow(
                    icon = Icons.Default.LocationOn,
                    label = "Venue",
                    value = "${ticket.eventVenue}, ${ticket.eventVenueCity}"
                )
            }

            Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.lg))

            // Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(EventPassDimensions.CornerRadius.sm))
                    .background(
                        when (ticket.scanStatus) {
                            TicketScanStatus.UNUSED -> EventPassColors.Success.copy(alpha = 0.1f)
                            TicketScanStatus.SCANNED -> EventPassColors.Warning.copy(alpha = 0.1f)
                            TicketScanStatus.EXPIRED -> EventPassColors.Gray400.copy(alpha = 0.1f)
                        }
                    )
                    .padding(EventPassDimensions.Spacing.md),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (ticket.scanStatus) {
                        TicketScanStatus.UNUSED -> Icons.Default.QrCode2
                        TicketScanStatus.SCANNED -> Icons.Default.CheckCircle
                        TicketScanStatus.EXPIRED -> Icons.Default.CalendarToday
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = when (ticket.scanStatus) {
                        TicketScanStatus.UNUSED -> EventPassColors.Success
                        TicketScanStatus.SCANNED -> EventPassColors.Warning
                        TicketScanStatus.EXPIRED -> EventPassColors.Gray400
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = ticket.scanStatus.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = when (ticket.scanStatus) {
                        TicketScanStatus.UNUSED -> EventPassColors.Success
                        TicketScanStatus.SCANNED -> EventPassColors.Warning
                        TicketScanStatus.EXPIRED -> EventPassColors.Gray400
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
