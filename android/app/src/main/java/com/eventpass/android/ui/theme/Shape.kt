package com.eventpass.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * EventPass Shape Definitions
 * Consistent corner radii across the app
 */
val EventPassShapes = Shapes(
    // Extra small - for chips, badges
    extraSmall = RoundedCornerShape(4.dp),

    // Small - for buttons, text fields
    small = RoundedCornerShape(8.dp),

    // Medium - for cards, dialogs
    medium = RoundedCornerShape(12.dp),

    // Large - for bottom sheets, larger cards
    large = RoundedCornerShape(16.dp),

    // Extra large - for full-screen dialogs
    extraLarge = RoundedCornerShape(24.dp)
)

/**
 * Custom shape values for specific use cases
 */
object EventPassCorners {
    val None = RoundedCornerShape(0.dp)
    val XSmall = RoundedCornerShape(4.dp)
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
    val XLarge = RoundedCornerShape(24.dp)
    val Full = RoundedCornerShape(50)

    // Specific component shapes
    val Card = RoundedCornerShape(16.dp)
    val Button = RoundedCornerShape(12.dp)
    val TextField = RoundedCornerShape(12.dp)
    val BottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val TopBar = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    val Chip = RoundedCornerShape(8.dp)
    val Badge = RoundedCornerShape(4.dp)
    val Avatar = RoundedCornerShape(50)
    val Poster = RoundedCornerShape(12.dp)
}
