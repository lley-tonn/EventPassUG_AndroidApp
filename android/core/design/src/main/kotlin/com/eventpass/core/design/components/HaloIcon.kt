package com.eventpass.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * A tinted circular "halo" around an icon, used everywhere in iOS
 * (date cards, venue cards, role cards, success states, etc.).
 */
@Composable
fun HaloIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = EventPassColors.Primary,
    background: Color = tint.copy(alpha = 0.12f),
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Preview(name = "HaloIcon", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun HaloIconPreview() {
    EventPassTheme {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            HaloIcon(Icons.Filled.CalendarToday)
            HaloIcon(Icons.Filled.Celebration, tint = EventPassColors.Success)
            HaloIcon(Icons.Filled.Notifications, tint = EventPassColors.Warning, size = 56.dp)
        }
    }
}
