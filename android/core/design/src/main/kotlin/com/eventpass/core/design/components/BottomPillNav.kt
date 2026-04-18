package com.eventpass.core.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.core.design.tokens.softShadow

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Floating pill-shaped bottom navigation bar used across Attendee and Organizer main flows.
 *
 * Matches iOS: a white pill floating above the content with subtle shadow,
 * selected item shows a coloured dot + tinted icon + label.
 */
@Composable
fun BottomPillNav(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
    accent: androidx.compose.ui.graphics.Color = EventPassColors.Primary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
            .softShadow(elevation = 12.dp, shape = Radii.Pill)
            .clip(Radii.Pill)
            .background(EventPassColors.White)
            .padding(horizontal = Spacing.xs, vertical = Spacing.xs)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            val selected = item.route == selectedRoute
            val tint by animateColorAsState(
                if (selected) accent else EventPassColors.InkMuted,
                label = "navTint"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(Radii.Pill)
                    .clickable { onItemSelected(item) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                    if (selected) {
                        Text(
                            text = item.label,
                            color = tint,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(androidx.compose.ui.graphics.Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun BottomPillNavPreviewAttendee() {
    EventPassTheme {
        BottomPillNav(
            items = listOf(
                BottomNavItem("Home", Icons.Filled.Home, "home"),
                BottomNavItem("Discover", Icons.Filled.Search, "discover"),
                BottomNavItem("Tickets", Icons.Filled.ConfirmationNumber, "tickets"),
                BottomNavItem("Calendar", Icons.Filled.CalendarToday, "calendar"),
                BottomNavItem("Profile", Icons.Filled.Person, "profile")
            ),
            selectedRoute = "home",
            onItemSelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun BottomPillNavPreviewOrganizer() {
    EventPassTheme {
        BottomPillNav(
            items = listOf(
                BottomNavItem("Home", Icons.Filled.Home, "home"),
                BottomNavItem("Events", Icons.Filled.CalendarToday, "events"),
                BottomNavItem("Tickets", Icons.Filled.ConfirmationNumber, "tickets"),
                BottomNavItem("Profile", Icons.Filled.Person, "profile")
            ),
            selectedRoute = "events",
            onItemSelected = {},
            accent = EventPassColors.OrganizerAccent
        )
    }
}
