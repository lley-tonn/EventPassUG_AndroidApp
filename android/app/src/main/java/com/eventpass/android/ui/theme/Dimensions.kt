package com.eventpass.android.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Design system dimensions.
 * Migrated from iOS AppDesignSystem.swift
 */
object EventPassDimensions {

    // MARK: - Spacing
    object Spacing {
        val xxs = 2.dp
        val xs = 4.dp
        val sm = 8.dp
        val md = 16.dp
        val lg = 24.dp
        val xl = 32.dp
        val xxl = 48.dp
        val xxxl = 64.dp

        // Semantic spacing
        val compact = 6.dp
        val section = 24.dp
        val item = 12.dp
        val edge = 16.dp // Screen edge padding
    }

    // MARK: - Corner Radius
    object CornerRadius {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 24.dp
        val pill = 100.dp

        // Semantic radius
        val card = 12.dp
        val button = 12.dp
        val input = 10.dp
        val badge = 6.dp
    }

    // MARK: - Button Dimensions
    object Button {
        val heightLarge = 56.dp
        val heightMedium = 48.dp
        val heightSmall = 36.dp
        val heightCompact = 32.dp

        val iconSize = 44.dp // Accessibility minimum
        val iconSizeCompact = 32.dp

        val paddingHorizontal = 24.dp
        val paddingVertical = 12.dp

        val minimumTouchTarget = 44.dp
    }

    // MARK: - Input Field Dimensions
    object Input {
        val height = 52.dp
        val heightCompact = 44.dp
        val paddingHorizontal = 16.dp
        val iconSize = 20.dp
    }

    // MARK: - Icon Sizes
    object Icon {
        val xs = 12.dp
        val sm = 16.dp
        val md = 20.dp
        val lg = 24.dp
        val xl = 32.dp
        val xxl = 48.dp
    }

    // MARK: - Card Dimensions
    object Card {
        val posterHeight = 200.dp
        val elevation = 4.dp
        val padding = 12.dp
    }
}

// Type aliases for convenience
typealias AppSpacing = EventPassDimensions.Spacing
typealias AppCornerRadius = EventPassDimensions.CornerRadius
typealias AppButtonDimensions = EventPassDimensions.Button
typealias AppInputDimensions = EventPassDimensions.Input
typealias AppIconSize = EventPassDimensions.Icon
