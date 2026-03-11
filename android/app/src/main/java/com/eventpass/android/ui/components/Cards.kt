package com.eventpass.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eventpass.android.ui.theme.EventPassDimensions

/**
 * App card component with consistent styling.
 * Migrated from iOS UI/Components/UIComponents.swift
 *
 * SwiftUI → Compose mapping:
 * - VStack with overlay → Card with content
 * - .shadow() → CardDefaults.cardElevation
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    padding: Dp = EventPassDimensions.Spacing.md,
    cornerRadius: Dp = EventPassDimensions.CornerRadius.card,
    hasShadow: Boolean = true,
    hasBorder: Boolean = false,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (hasBorder) {
        OutlinedCard(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(cornerRadius),
            border = BorderStroke(1.dp, borderColor),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            onClick = onClick ?: {}
        ) {
            Column(
                modifier = Modifier.padding(padding),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(cornerRadius),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (hasShadow) 4.dp else 0.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            onClick = onClick ?: {}
        ) {
            Column(
                modifier = Modifier.padding(padding),
                content = content
            )
        }
    }
}

/**
 * Elevated card with stronger shadow.
 */
@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    padding: Dp = EventPassDimensions.Spacing.md,
    cornerRadius: Dp = EventPassDimensions.CornerRadius.card,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
}

/**
 * Colored card with custom background.
 */
@Composable
fun ColoredCard(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    padding: Dp = EventPassDimensions.Spacing.md,
    cornerRadius: Dp = EventPassDimensions.CornerRadius.card,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
}

/**
 * Interactive card with selected state.
 */
@Composable
fun SelectableCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    padding: Dp = EventPassDimensions.Spacing.md,
    cornerRadius: Dp = EventPassDimensions.CornerRadius.card,
    selectedBorderColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) selectedBorderColor
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) {
                selectedBorderColor.copy(alpha = 0.05f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
}

/**
 * Card group for visually grouping multiple items.
 */
@Composable
fun CardGroup(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = EventPassDimensions.CornerRadius.card,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(content = content)
    }
}
