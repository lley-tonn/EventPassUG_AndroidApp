package com.eventpass.core.design.typography

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.eventpass.core.design.R

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val inter = GoogleFont("Inter")

/** Inter downloaded on-demand via Google Play Services Fonts. */
val InterFontFamily = FontFamily(
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.Light),
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.Bold),
    Font(googleFont = inter, fontProvider = googleFontProvider, weight = FontWeight.ExtraBold),
    Font(
        googleFont = inter,
        fontProvider = googleFontProvider,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    )
)
