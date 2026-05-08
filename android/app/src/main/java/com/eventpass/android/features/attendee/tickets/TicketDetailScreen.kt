package com.eventpass.android.features.attendee.tickets

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventpass.android.core.state.UiState
import com.eventpass.android.core.util.QRCodeGenerator
import com.eventpass.android.domain.models.Ticket
import com.eventpass.feature.attendee.tickets.TicketDetailsData
import com.eventpass.feature.attendee.tickets.TicketDetailsScreen as TicketDetailsContent
import com.eventpass.feature.attendee.tickets.components.TicketStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

/**
 * :app-side wrapper for the Ticket Details screen — loads the ticket via
 * Hilt + SavedStateHandle, generates the QR bitmap off the main thread,
 * and delegates the UI to the stateless `:feature:attendee` screen.
 */
@Composable
fun TicketDetailScreen(
    onDone: () -> Unit,
    viewModel: TicketDetailViewModel = hiltViewModel()
) {
    val state by viewModel.ticketState.collectAsState()

    when (val s = state) {
        is UiState.Success -> Loaded(ticket = s.data, onDone = onDone)
        is UiState.Error -> ErrorState(message = s.message)
        UiState.Loading, UiState.Idle -> LoadingState()
    }
}

@Composable
private fun Loaded(ticket: Ticket, onDone: () -> Unit) {
    val context = LocalContext.current
    val qr by produceState<ImageBitmap?>(initialValue = null, ticket.qrCodeData) {
        value = withContext(Dispatchers.Default) {
            QRCodeGenerator.generateQRCode(ticket.qrCodeData, size = 768)?.asImageBitmap()
        }
    }

    TicketDetailsContent(
        data = ticket.toDetailsData(),
        qrBitmap = qr,
        onShare = {
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "I'm going to ${ticket.eventTitle} at ${ticket.eventVenue} — Ticket ${ticket.ticketNumber}"
                )
            }
            context.startActivity(Intent.createChooser(send, "Share ticket"))
        },
        onDone = onDone,
        onOpenInMaps = {
            val uri = Uri.parse(
                "geo:${ticket.venueLatitude},${ticket.venueLongitude}" +
                    "?q=${Uri.encode(ticket.eventVenue)}"
            )
            val intent = Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps")
            val fallback = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(if (intent.resolveActivity(context.packageManager) != null) intent else fallback)
        }
    )
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}

private val LongDateFormat = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy 'at' HH:mm")
private val ShortDateFormat = DateTimeFormatter.ofPattern("d MMM yyyy 'at' HH:mm")

private fun Ticket.toDetailsData(): TicketDetailsData = TicketDetailsData(
    title = eventTitle,
    organizerText = "by $eventOrganizerName",
    ticketType = ticketType.name,
    priceText = formattedPrice,
    startText = eventDate.format(LongDateFormat),
    endText = eventEndDate.format(LongDateFormat),
    venueName = eventVenue,
    venueAddress = listOf(eventVenueAddress, eventVenueCity)
        .filter { it.isNotBlank() }
        .joinToString(", "),
    venueLat = venueLatitude,
    venueLng = venueLongitude,
    description = eventDescription,
    purchasedOnText = purchaseDate.format(ShortDateFormat),
    ticketIdShort = qrCodeData.takeLast(12),
    ticketIdFull = ticketNumber,
    status = if (isExpired) TicketStatus.Expired else TicketStatus.Active
)
