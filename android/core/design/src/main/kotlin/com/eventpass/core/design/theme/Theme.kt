package com.eventpass.core.design.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.typography.EventPassTypography

private val LightColorScheme = lightColorScheme(
    primary = EventPassColors.Primary,
    onPrimary = EventPassColors.White,
    primaryContainer = EventPassColors.PrimarySoft,
    onPrimaryContainer = EventPassColors.PrimaryDark,
    secondary = EventPassColors.OrganizerAccent,
    onSecondary = EventPassColors.White,
    tertiary = EventPassColors.Info,
    onTertiary = EventPassColors.White,
    error = EventPassColors.Error,
    onError = EventPassColors.White,
    errorContainer = EventPassColors.ErrorSoft,
    onErrorContainer = EventPassColors.Error,
    background = EventPassColors.BackgroundLight,
    onBackground = EventPassColors.Ink,
    surface = EventPassColors.SurfaceLight,
    onSurface = EventPassColors.Ink,
    surfaceVariant = EventPassColors.BackgroundLight,
    onSurfaceVariant = EventPassColors.InkMuted,
    outline = EventPassColors.OutlineLight,
    outlineVariant = EventPassColors.DividerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = EventPassColors.Primary,
    onPrimary = EventPassColors.White,
    primaryContainer = EventPassColors.PrimaryDark,
    onPrimaryContainer = EventPassColors.PrimarySoft,
    secondary = EventPassColors.OrganizerAccent,
    onSecondary = EventPassColors.White,
    tertiary = EventPassColors.Info,
    onTertiary = EventPassColors.White,
    error = EventPassColors.Error,
    onError = EventPassColors.White,
    background = EventPassColors.BackgroundDark,
    onBackground = EventPassColors.White,
    surface = EventPassColors.SurfaceDark,
    onSurface = EventPassColors.White,
    surfaceVariant = EventPassColors.SurfaceDarkElevated,
    onSurfaceVariant = EventPassColors.InkSubtle,
    outline = EventPassColors.OutlineDark,
    outlineVariant = EventPassColors.DividerDark
)

@Composable
fun EventPassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EventPassTypography,
        content = content
    )
}
