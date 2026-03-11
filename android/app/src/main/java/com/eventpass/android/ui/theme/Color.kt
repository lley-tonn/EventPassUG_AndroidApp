package com.eventpass.android.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * EventPass Color Palette
 * Migrated from iOS AppDesignSystem.swift
 */
object EventPassColors {
    // Primary Brand Colors
    val Primary = Color(0xFFFF7A00)           // Orange - Main brand color
    val PrimaryDark = Color(0xFFE66D00)       // Darker orange for pressed states
    val PrimaryLight = Color(0xFFFFA040)      // Lighter orange for backgrounds

    // Role-specific Colors
    val AttendeePrimary = Color(0xFFFF7A00)   // Orange for attendee role
    val OrganizerPrimary = Color(0xFF6366F1)  // Indigo for organizer role

    // Status Colors
    val Success = Color(0xFF22C55E)           // Green
    val Warning = Color(0xFFF59E0B)           // Amber
    val Error = Color(0xFFEF4444)             // Red
    val Info = Color(0xFF3B82F6)              // Blue

    // Special Status Colors
    val HappeningNow = Color(0xFF7CFC66)      // Bright green for live events
    val Premium = Color(0xFFFFD700)           // Gold for premium/VIP
    val SoldOut = Color(0xFF9CA3AF)           // Gray for sold out

    // Neutral Colors
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Gray50 = Color(0xFFFAFAFA)
    val Gray100 = Color(0xFFF4F4F5)
    val Gray200 = Color(0xFFE4E4E7)
    val Gray300 = Color(0xFFD4D4D8)
    val Gray400 = Color(0xFFA1A1AA)
    val Gray500 = Color(0xFF71717A)
    val Gray600 = Color(0xFF52525B)
    val Gray700 = Color(0xFF3F3F46)
    val Gray800 = Color(0xFF27272A)
    val Gray900 = Color(0xFF18181B)

    // Background Colors
    val BackgroundLight = Color(0xFFFAFAFA)
    val BackgroundDark = Color(0xFF121212)
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceDark = Color(0xFF1E1E1E)
    val CardLight = Color(0xFFFFFFFF)
    val CardDark = Color(0xFF2D2D2D)
}

// Light Theme Colors
val md_theme_light_primary = EventPassColors.Primary
val md_theme_light_onPrimary = EventPassColors.White
val md_theme_light_primaryContainer = EventPassColors.PrimaryLight
val md_theme_light_onPrimaryContainer = EventPassColors.PrimaryDark
val md_theme_light_secondary = EventPassColors.OrganizerPrimary
val md_theme_light_onSecondary = EventPassColors.White
val md_theme_light_secondaryContainer = Color(0xFFE0E1FF)
val md_theme_light_onSecondaryContainer = Color(0xFF1D1B4B)
val md_theme_light_tertiary = EventPassColors.Info
val md_theme_light_onTertiary = EventPassColors.White
val md_theme_light_tertiaryContainer = Color(0xFFDBE1FF)
val md_theme_light_onTertiaryContainer = Color(0xFF001849)
val md_theme_light_error = EventPassColors.Error
val md_theme_light_onError = EventPassColors.White
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = EventPassColors.BackgroundLight
val md_theme_light_onBackground = EventPassColors.Gray900
val md_theme_light_surface = EventPassColors.SurfaceLight
val md_theme_light_onSurface = EventPassColors.Gray900
val md_theme_light_surfaceVariant = EventPassColors.Gray100
val md_theme_light_onSurfaceVariant = EventPassColors.Gray700
val md_theme_light_outline = EventPassColors.Gray400
val md_theme_light_outlineVariant = EventPassColors.Gray200
val md_theme_light_inverseSurface = EventPassColors.Gray900
val md_theme_light_inverseOnSurface = EventPassColors.Gray100
val md_theme_light_inversePrimary = EventPassColors.PrimaryLight

// Dark Theme Colors
val md_theme_dark_primary = EventPassColors.Primary
val md_theme_dark_onPrimary = EventPassColors.White
val md_theme_dark_primaryContainer = EventPassColors.PrimaryDark
val md_theme_dark_onPrimaryContainer = EventPassColors.PrimaryLight
val md_theme_dark_secondary = Color(0xFFA5A7FF)
val md_theme_dark_onSecondary = Color(0xFF2E2D5E)
val md_theme_dark_secondaryContainer = Color(0xFF454477)
val md_theme_dark_onSecondaryContainer = Color(0xFFE0E1FF)
val md_theme_dark_tertiary = Color(0xFFB3C5FF)
val md_theme_dark_onTertiary = Color(0xFF002A76)
val md_theme_dark_tertiaryContainer = Color(0xFF003FA5)
val md_theme_dark_onTertiaryContainer = Color(0xFFDBE1FF)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = EventPassColors.BackgroundDark
val md_theme_dark_onBackground = EventPassColors.Gray100
val md_theme_dark_surface = EventPassColors.SurfaceDark
val md_theme_dark_onSurface = EventPassColors.Gray100
val md_theme_dark_surfaceVariant = EventPassColors.Gray800
val md_theme_dark_onSurfaceVariant = EventPassColors.Gray300
val md_theme_dark_outline = EventPassColors.Gray500
val md_theme_dark_outlineVariant = EventPassColors.Gray700
val md_theme_dark_inverseSurface = EventPassColors.Gray100
val md_theme_dark_inverseOnSurface = EventPassColors.Gray900
val md_theme_dark_inversePrimary = EventPassColors.Primary
