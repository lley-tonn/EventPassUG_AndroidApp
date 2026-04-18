package com.eventpass.core.design.tokens

import androidx.compose.ui.graphics.Color

/**
 * EventPass brand colors — derived from the iOS reference screenshots.
 * Every hue used in the UI should resolve to one of these tokens.
 */
object EventPassColors {
    // Brand
    val Primary = Color(0xFFFF7A00)
    val PrimaryDark = Color(0xFFE66D00)
    val PrimaryLight = Color(0xFFFFA040)
    val PrimarySoft = Color(0xFFFFE8D2)       // halo / chip background tint

    // Role accents
    val AttendeePrimary = Primary
    val OrganizerAccent = Color(0xFF6366F1)

    // Status
    val Success = Color(0xFF22C55E)
    val SuccessSoft = Color(0xFFDFF7E6)
    val Warning = Color(0xFFF59E0B)
    val WarningSoft = Color(0xFFFEF3C7)
    val Error = Color(0xFFEF4444)
    val ErrorSoft = Color(0xFFFEE2E2)
    val Info = Color(0xFF3B82F6)
    val InfoSoft = Color(0xFFDBEAFE)

    // Special badges seen in iOS
    val HappeningNow = Color(0xFF34C759)      // iOS system green
    val Premium = Color(0xFFFFD700)
    val SoldOut = Color(0xFF9CA3AF)

    // Neutrals — iOS-flavoured grey ramp
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Ink = Color(0xFF111827)               // primary text
    val InkMuted = Color(0xFF6B7280)          // secondary text
    val InkSubtle = Color(0xFF9CA3AF)         // tertiary text / captions

    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceLightElevated = Color(0xFFFFFFFF)
    val BackgroundLight = Color(0xFFF2F2F7)   // iOS systemGroupedBackground
    val DividerLight = Color(0xFFE5E7EB)
    val OutlineLight = Color(0xFFD1D5DB)

    val SurfaceDark = Color(0xFF1C1C1E)
    val SurfaceDarkElevated = Color(0xFF2C2C2E)
    val BackgroundDark = Color(0xFF000000)
    val DividerDark = Color(0xFF2C2C2E)
    val OutlineDark = Color(0xFF3A3A3C)
}
