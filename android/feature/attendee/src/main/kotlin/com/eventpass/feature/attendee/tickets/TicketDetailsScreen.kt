package com.eventpass.feature.attendee.tickets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.core.design.tokens.softShadow
import com.eventpass.feature.attendee.tickets.components.TicketStatus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Data record for the Ticket Details screen — keeps `:feature:attendee` free
 * of domain types. The `:app` wrapper maps a domain `Ticket` into this.
 */
data class TicketDetailsData(
    val title: String,
    val organizerText: String,
    val ticketType: String,
    val priceText: String,
    val startText: String,
    val endText: String,
    val venueName: String,
    val venueAddress: String,
    val venueLat: Double,
    val venueLng: Double,
    val description: String,
    val purchasedOnText: String,
    val ticketIdShort: String,
    val ticketIdFull: String,
    val status: TicketStatus
)

/**
 * Ticket Details — matches design reference (IMG_2771–IMG_2773).
 *
 * Stateless: caller supplies the data record, a pre-rendered QR [ImageBitmap]
 * (rendering bitmaps in a composable would be wasteful on every recompose),
 * and the action callbacks. Whole screen is one vertical scroll.
 */
@Composable
fun TicketDetailsScreen(
    data: TicketDetailsData,
    qrBitmap: ImageBitmap?,
    onShare: () -> Unit,
    onDone: () -> Unit,
    onOpenInMaps: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxxl)
        ) {
            Spacer(Modifier.height(72.dp)) // room for floating top bar

            StatusPill(status = data.status)

            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = data.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = EventPassColors.Ink
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = data.organizerText,
                style = MaterialTheme.typography.bodySmall,
                color = EventPassColors.InkMuted
            )

            Spacer(Modifier.height(Spacing.lg))

            TicketTypePriceCard(ticketType = data.ticketType, priceText = data.priceText)

            Spacer(Modifier.height(Spacing.lg))

            DateTimeBlock(startText = data.startText, endText = data.endText)

            Spacer(Modifier.height(Spacing.lg))
            Divider()
            Spacer(Modifier.height(Spacing.lg))

            VenueBlock(
                venueName = data.venueName,
                venueAddress = data.venueAddress,
                lat = data.venueLat,
                lng = data.venueLng
            )

            Spacer(Modifier.height(Spacing.md))
            OpenInMapsButton(onClick = onOpenInMaps)

            Spacer(Modifier.height(Spacing.lg))
            Divider()
            Spacer(Modifier.height(Spacing.lg))

            SectionHeading("About the Event")
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = data.description,
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )

            Spacer(Modifier.height(Spacing.lg))
            Divider()
            Spacer(Modifier.height(Spacing.lg))

            SectionHeading("Your Ticket QR Code")
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Show this QR code at the entrance",
                style = MaterialTheme.typography.bodySmall,
                color = EventPassColors.InkMuted
            )
            Spacer(Modifier.height(Spacing.md))

            QrBlock(qr = qrBitmap, ticketIdShort = data.ticketIdShort)

            Spacer(Modifier.height(Spacing.xl))
            Divider()
            Spacer(Modifier.height(Spacing.lg))

            SectionHeading("Purchase Information")
            Spacer(Modifier.height(Spacing.md))
            InfoRow(label = "Purchased on", value = data.purchasedOnText)
            Spacer(Modifier.height(Spacing.sm))
            InfoRow(label = "Ticket ID", value = data.ticketIdFull)
        }

        TopActionBar(onShare = onShare, onDone = onDone)
    }
}

// MARK: - Top action bar

@Composable
private fun TopActionBar(onShare: () -> Unit, onDone: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SmallPillButton(
            text = "Share",
            leadingIcon = Icons.Filled.Share,
            onClick = onShare
        )
        SmallPillButton(text = "Done", onClick = onDone)
    }
}

@Composable
private fun SmallPillButton(
    text: String,
    onClick: () -> Unit,
    leadingIcon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .softShadow(elevation = 4.dp, shape = Radii.Pill)
            .clip(Radii.Pill)
            .background(EventPassColors.White)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = EventPassColors.Ink,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
    }
}

// MARK: - Status pill

@Composable
private fun StatusPill(status: TicketStatus) {
    val (label, color) = when (status) {
        TicketStatus.Active -> "ACTIVE" to EventPassColors.Primary
        TicketStatus.Expired -> "EXPIRED" to EventPassColors.InkSubtle
    }
    Row(
        modifier = Modifier
            .clip(Radii.Pill)
            .background(color)
            .padding(horizontal = Spacing.md, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(EventPassColors.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = EventPassColors.White,
                modifier = Modifier.size(10.dp)
            )
        }
        Spacer(Modifier.width(Spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = EventPassColors.White
        )
    }
}

// MARK: - Ticket type / price

@Composable
private fun TicketTypePriceCard(ticketType: String, priceText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.Card)
            .background(EventPassColors.White)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Ticket Type",
                style = MaterialTheme.typography.labelSmall,
                color = EventPassColors.InkMuted
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = ticketType,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
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
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Primary
            )
        }
    }
}

// MARK: - Date & Time / Venue blocks

@Composable
private fun DateTimeBlock(startText: String, endText: String) {
    IconLeadingBlock(
        icon = Icons.Filled.CalendarMonth,
        title = "Date & Time"
    ) {
        Text(
            text = startText,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
        Text(
            text = "to",
            style = MaterialTheme.typography.bodySmall,
            color = EventPassColors.InkMuted
        )
        Text(
            text = endText,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
    }
}

@Composable
private fun VenueBlock(
    venueName: String,
    venueAddress: String,
    lat: Double,
    lng: Double
) {
    IconLeadingBlock(
        icon = Icons.Filled.LocationOn,
        title = "Venue"
    ) {
        Text(
            text = venueName,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
        Text(
            text = venueAddress,
            style = MaterialTheme.typography.bodySmall,
            color = EventPassColors.InkMuted
        )
    }
    Spacer(Modifier.height(Spacing.md))
    MapPreview(lat = lat, lng = lng, label = venueName)
}

@Composable
private fun IconLeadingBlock(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EventPassColors.Primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.wrapContentHeight()) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = EventPassColors.InkMuted
            )
            Spacer(Modifier.height(2.dp))
            content()
        }
    }
}

// MARK: - Map

@Composable
private fun MapPreview(lat: Double, lng: Double, label: String) {
    val target = LatLng(lat, lng)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(target, 15f)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(Radii.CardLarge)
            .background(EventPassColors.OutlineLight.copy(alpha = 0.3f))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                scrollGesturesEnabled = false,
                zoomGesturesEnabled = false,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false
            ),
            properties = MapProperties(isMyLocationEnabled = false)
        ) {
            Marker(state = MarkerState(position = target), title = label)
        }
    }
    LaunchedEffect(lat, lng) {
        cameraState.animate(CameraUpdateFactory.newLatLngZoom(target, 15f))
    }
}

@Composable
private fun OpenInMapsButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Map,
            contentDescription = null,
            tint = EventPassColors.Primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(Spacing.xs))
        Text(
            text = "Open in Maps",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Primary
        )
    }
}

// MARK: - QR

@Composable
private fun QrBlock(qr: ImageBitmap?, ticketIdShort: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .softShadow(elevation = 6.dp, shape = Radii.CardLarge)
                .clip(Radii.CardLarge)
                .background(EventPassColors.White)
                .padding(Spacing.lg),
            contentAlignment = Alignment.Center
        ) {
            if (qr != null) {
                Image(
                    bitmap = qr,
                    contentDescription = "Ticket QR code",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "Generating QR…",
                    style = MaterialTheme.typography.bodySmall,
                    color = EventPassColors.InkMuted
                )
            }
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = ticketIdShort,
            style = MaterialTheme.typography.labelSmall,
            color = EventPassColors.InkSubtle
        )
    }
}

// MARK: - Misc helpers

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = EventPassColors.Ink
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = EventPassColors.InkMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(EventPassColors.DividerLight)
    )
}

