package com.eventpass.feature.attendee.tickets.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/**
 * Pill-shaped filter chip for the My Tickets screen.
 *
 * Matches the iOS reference (IMG_2770): white fill with a 1px outline, a
 * numeric badge after the label, and — when [showCheck] is true and the chip
 * is selected — a small green check circle on the leading edge. Selected state
 * swaps the outline + badge to [accent].
 */
@Composable
fun TicketFilterChip(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    accent: Color,
    modifier: Modifier = Modifier,
    showCheck: Boolean = false
) {
    val border by animateColorAsState(
        if (selected) accent else EventPassColors.OutlineLight,
        label = "chipBorder"
    )
    val labelColor by animateColorAsState(
        if (selected) EventPassColors.Ink else EventPassColors.InkMuted,
        label = "chipLabel"
    )
    val badgeBg by animateColorAsState(
        if (selected) accent else EventPassColors.OutlineLight.copy(alpha = 0.5f),
        label = "chipBadgeBg"
    )
    val badgeFg by animateColorAsState(
        if (selected) EventPassColors.White else EventPassColors.InkMuted,
        label = "chipBadgeFg"
    )

    Row(
        modifier = modifier
            .clip(Radii.Pill)
            .background(EventPassColors.White)
            .border(if (selected) 1.5.dp else 1.dp, border, Radii.Pill)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showCheck && selected) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(EventPassColors.Success),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = EventPassColors.White,
                    modifier = Modifier.size(10.dp)
                )
            }
            Spacer(Modifier.size(Spacing.xs))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = labelColor
        )
        Spacer(Modifier.size(Spacing.xs))
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(badgeBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = badgeFg
            )
        }
    }
}
