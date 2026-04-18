package com.eventpass.feature.attendee.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * Horizontal rail of top-level browse categories (Today, This week, etc).
 * Each item is a tappable square icon on a muted tint with its label below.
 * Selected item swaps to the brand orange fill.
 */
data class CategoryItem(
    val id: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun CategoryRail(
    items: List<CategoryItem>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        items(items, key = { it.id }) { item ->
            CategoryTile(
                item = item,
                selected = item.id == selectedId,
                onClick = { onSelect(item.id) }
            )
        }
    }
}

@Composable
private fun CategoryTile(
    item: CategoryItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        if (selected) EventPassColors.Primary else EventPassColors.White,
        label = "catBg"
    )
    val fg by animateColorAsState(
        if (selected) EventPassColors.White else EventPassColors.Ink,
        label = "catFg"
    )
    Column(
        modifier = Modifier.width(68.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(bg)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = fg,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium,
            color = EventPassColors.Ink,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
