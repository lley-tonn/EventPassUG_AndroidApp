package com.eventpass.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eventpass.android.ui.theme.EventPassColors
import com.eventpass.android.ui.theme.EventPassDimensions

/**
 * Category tile for filtering events.
 * Migrated from iOS UI/Components/CategoryTile.swift
 *
 * SwiftUI → Compose mapping:
 * - VStack → Column
 * - Image(systemName:) → Icon with ImageVector
 * - .clipShape(Circle()) → Modifier.clip(CircleShape)
 * - .accessibilityElement(children: .combine) → semantics block
 */
@Composable
fun CategoryTile(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                role = Role.Button,
                onClick = onTap
            )
            .semantics {
                contentDescription = "$title category"
                selected = isSelected
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm)
    ) {
        // Icon circle
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) EventPassColors.Primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .size(50.dp),
            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(70.dp)
        )
    }
}

/**
 * Category tile with custom icon content.
 */
@Composable
fun CategoryTileWithContent(
    title: String,
    isSelected: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                role = Role.Button,
                onClick = onTap
            )
            .semantics {
                contentDescription = "$title category"
                selected = isSelected
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm)
    ) {
        // Custom icon content
        iconContent()

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(70.dp)
        )
    }
}
