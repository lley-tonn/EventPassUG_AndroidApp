package com.eventpass.feature.organizer.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
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

/** An event shown in the scanner-devices event picker. */
data class ScannerEvent(
    val id: String,
    val title: String,
    val dateText: String,
    val statusLabel: String,
    val posterUrl: String?
)

/**
 * Scanner Devices (design reference IMG_2807). An event picker — the organizer
 * chooses an event to manage its authorized scanning devices.
 */
@Composable
fun ScannerDevicesScreen(
    events: List<ScannerEvent>,
    onBack: () -> Unit,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
    ) {
        Spacer(Modifier.height(Spacing.sm))
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(EventPassColors.White)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = EventPassColors.Ink, modifier = Modifier.size(22.dp))
        }

        Spacer(Modifier.height(Spacing.lg))
        Text(
            "Scanner Devices",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = EventPassColors.Ink
        )

        Spacer(Modifier.height(Spacing.lg))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Radii.Card)
                .background(EventPassColors.InfoSoft)
                .padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = EventPassColors.Info, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Spacing.md))
            Text(
                "Select an event to manage its scanner devices",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted
            )
        }

        Spacer(Modifier.height(Spacing.lg))
        Text(
            "ACTIVE & UPCOMING",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.InkMuted
        )
        Spacer(Modifier.height(Spacing.sm))

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            events.forEach { event ->
                ScannerEventRow(event = event, onClick = { onEventClick(event.id) })
            }
        }
        Spacer(Modifier.height(Spacing.xxxl))
    }
}

@Composable
private fun ScannerEventRow(event: ScannerEvent, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(Radii.sm))
                .background(EventPassColors.DividerLight)
        )
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = EventPassColors.Ink, maxLines = 1)
            Spacer(Modifier.height(2.dp))
            Text(event.dateText, style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
            Spacer(Modifier.height(Spacing.xs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EventPassColors.Info))
                Spacer(Modifier.width(Spacing.xs))
                Text(event.statusLabel, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = EventPassColors.Info)
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = EventPassColors.InkSubtle, modifier = Modifier.size(22.dp))
    }
}
