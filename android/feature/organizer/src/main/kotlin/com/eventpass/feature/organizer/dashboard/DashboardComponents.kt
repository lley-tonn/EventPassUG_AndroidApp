package com.eventpass.feature.organizer.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/** Section header: small tinted icon, bold title, optional trailing slot (chevron/pill). */
@Composable
fun DashboardSectionHeader(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(Spacing.sm))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.Ink,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = EventPassColors.InkSubtle,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * A metric tile: leading tinted icon + label on one line, then a large value and
 * optional caption. [trailing] renders at the top-right (e.g. a trend badge).
 */
@Composable
fun StatCard(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = EventPassColors.Ink,
    caption: String? = null,
    background: Color = EventPassColors.White,
    minHeight: Dp = 104.dp,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clip(Radii.Card)
            .background(background)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(Spacing.lg)
            .height(minHeight)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            if (trailing != null) trailing()
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = valueColor
        )
        if (caption != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )
        }
    }
}

/** Small green trend badge, e.g. "↗ +12%". */
@Composable
fun TrendBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radii.xs))
            .background(EventPassColors.SuccessSoft)
            .padding(horizontal = Spacing.sm, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.Success
        )
    }
}

/** White pill with a coloured status dot, e.g. "● Scanner". */
@Composable
fun StatusPill(text: String, dotColor: Color = EventPassColors.Success) {
    Row(
        modifier = Modifier
            .clip(Radii.Pill)
            .background(EventPassColors.White)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(dotColor)
        )
        Spacer(Modifier.width(Spacing.sm))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
    }
}

/** An action row inside Quick Actions: rounded tinted icon square, title + subtitle, chevron. */
@Composable
fun QuickActionRow(
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Radii.Card)
            .background(EventPassColors.White)
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(Radii.sm))
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = EventPassColors.InkSubtle,
            modifier = Modifier.size(22.dp)
        )
    }
}

/** Two equal-width cells with the standard dashboard gutter. */
@Composable
fun StatRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        content = content
    )
}

/** Vertical stack with the standard dashboard gutter. */
@Composable
fun DashboardColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        content = content
    )
}
