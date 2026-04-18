package com.eventpass.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.core.design.tokens.softShadow

/**
 * Circular bubble icon button used in the screen headers on the iOS reference
 * (search / favorites / notifications on Attendee Home, back / share on details screens).
 *
 * Supports an optional numeric badge (e.g. notification count).
 */
@Composable
fun IconBubbleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    background: Color = EventPassColors.White,
    iconTint: Color = EventPassColors.Ink,
    contentDescription: String? = null,
    badgeCount: Int? = null,
    elevated: Boolean = true
) {
    Box(modifier = modifier.size(size + 10.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size)
                .let { if (elevated) it.softShadow(elevation = 8.dp, shape = CircleShape) else it }
                .clip(CircleShape)
                .background(background)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(size * 0.45f)
            )
        }
        if (badgeCount != null && badgeCount > 0) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-2).dp, y = 2.dp)
                    .clip(CircleShape)
                    .background(EventPassColors.Primary)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                    color = EventPassColors.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun IconBubbleButtonPreview() {
    EventPassTheme {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubbleButton(icon = Icons.Filled.Search, onClick = {})
            IconBubbleButton(icon = Icons.Filled.Favorite, onClick = {})
            IconBubbleButton(icon = Icons.Filled.Notifications, onClick = {}, badgeCount = 3)
            IconBubbleButton(icon = Icons.Filled.Notifications, onClick = {}, badgeCount = 128)
        }
    }
}
