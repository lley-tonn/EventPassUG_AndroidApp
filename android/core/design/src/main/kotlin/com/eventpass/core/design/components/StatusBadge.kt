package com.eventpass.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

enum class BadgeTone {
    Neutral, Primary, Success, Warning, Error, Live
}

@Composable
fun StatusBadge(
    text: String,
    tone: BadgeTone = BadgeTone.Neutral,
    modifier: Modifier = Modifier,
    showDot: Boolean = false
) {
    val (bg, fg) = when (tone) {
        BadgeTone.Primary -> EventPassColors.PrimarySoft to EventPassColors.Primary
        BadgeTone.Success -> EventPassColors.SuccessSoft to EventPassColors.Success
        BadgeTone.Warning -> EventPassColors.WarningSoft to EventPassColors.Warning
        BadgeTone.Error -> EventPassColors.ErrorSoft to EventPassColors.Error
        BadgeTone.Live -> EventPassColors.HappeningNow to Color.White
        BadgeTone.Neutral -> EventPassColors.DividerLight to EventPassColors.InkMuted
    }

    Row(
        modifier = modifier
            .clip(Radii.Pill)
            .background(bg)
            .padding(horizontal = Spacing.md, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showDot) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(fg)
            )
            Spacer(Modifier.size(Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = fg
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun StatusBadgePreview() {
    EventPassTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            StatusBadge("Happening now", tone = BadgeTone.Live, showDot = true)
            StatusBadge("Active", tone = BadgeTone.Success, showDot = true)
            StatusBadge("Pending", tone = BadgeTone.Warning)
            StatusBadge("Expired", tone = BadgeTone.Neutral)
            StatusBadge("Published", tone = BadgeTone.Primary, showDot = true)
        }
    }
}
