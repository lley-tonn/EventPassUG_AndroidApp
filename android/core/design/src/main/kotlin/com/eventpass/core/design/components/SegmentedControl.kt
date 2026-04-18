package com.eventpass.core.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

data class SegmentedOption(val label: String, val count: Int? = null)

/**
 * iOS-style segmented control — a rounded-pill track with a sliding selection.
 * Used on `My Tickets` (All / Active / Expired) and Organizer Home (Published / Draft / Ongoing).
 */
@Composable
fun SegmentedControl(
    options: List<SegmentedOption>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(Radii.Pill)
            .background(EventPassColors.DividerLight.copy(alpha = 0.6f))
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val bg by animateColorAsState(
                if (selected) EventPassColors.White else androidx.compose.ui.graphics.Color.Transparent,
                label = "segBg"
            )
            val fg by animateColorAsState(
                if (selected) EventPassColors.Ink else EventPassColors.InkMuted,
                label = "segFg"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(Radii.Pill)
                    .background(bg)
                    .clickable { onSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                val label = option.count?.let { "${option.label}  $it" } ?: option.label
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = fg
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun SegmentedControlPreview() {
    EventPassTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.md)
        ) {
            SegmentedControl(
                options = listOf(
                    SegmentedOption("All"),
                    SegmentedOption("Active", count = 2),
                    SegmentedOption("Expired", count = 0)
                ),
                selectedIndex = 1,
                onSelected = {}
            )
            SegmentedControl(
                options = listOf(
                    SegmentedOption("National ID"),
                    SegmentedOption("Passport")
                ),
                selectedIndex = 0,
                onSelected = {}
            )
        }
    }
}
