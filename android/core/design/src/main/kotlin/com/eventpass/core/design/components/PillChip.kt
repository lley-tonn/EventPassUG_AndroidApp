package com.eventpass.core.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

@Composable
fun PillChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    count: Int? = null
) {
    // Visual hierarchy:
    //   Unselected = quiet (white fill + subtle outline + muted fg)
    //   Selected   = loud  (brand-orange fill + white fg)
    // This keeps the strongest visual weight reserved for the primary CTA.
    val bg by animateColorAsState(
        if (selected) EventPassColors.Primary else EventPassColors.White,
        label = "chipBg"
    )
    val fg by animateColorAsState(
        if (selected) EventPassColors.White else EventPassColors.InkMuted,
        label = "chipFg"
    )
    val borderColor by animateColorAsState(
        if (selected) EventPassColors.Primary else EventPassColors.OutlineLight,
        label = "chipBorder"
    )

    Row(
        modifier = modifier
            .clip(Radii.Pill)
            .background(bg)
            .border(1.dp, borderColor, Radii.Pill)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (leadingIcon != null) {
            androidx.compose.material3.Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.size(Spacing.sm))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = fg
        )
        if (count != null) {
            Spacer(Modifier.size(Spacing.sm))
            Row(
                modifier = Modifier
                    .clip(Radii.Pill)
                    .background(fg.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = fg
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun PillChipPreview() {
    EventPassTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                PillChip("All", selected = false, onClick = {})
                PillChip("Active", selected = true, onClick = {}, count = 2)
                PillChip("Expired", selected = false, onClick = {}, count = 0)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                PillChip("Music", selected = false, onClick = {})
                PillChip("Sports", selected = false, onClick = {})
                PillChip("Tech", selected = true, onClick = {})
            }
        }
    }
}
