package com.eventpass.feature.onboarding.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MicExternalOn
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Interests the user selects during onboarding. Matches the iOS reference (IMG_2753):
 * Music · Festivals · Concerts · Nightlife · Sports · Arts & Culture · Comedy
 * Food & Drinks · Networking · Technology · Education · Wellness.
 */
enum class InterestCategory(val label: String, val icon: ImageVector) {
    Music("Music", Icons.Filled.MusicNote),
    Festivals("Festivals", Icons.Filled.Celebration),
    Concerts("Concerts", Icons.Filled.MicExternalOn),
    Nightlife("Nightlife", Icons.Filled.ModeNight),
    Sports("Sports", Icons.AutoMirrored.Filled.DirectionsRun),
    ArtsCulture("Arts & Culture", Icons.Filled.Brush),
    Comedy("Comedy", Icons.Filled.TheaterComedy),
    FoodDrinks("Food & Drinks", Icons.Filled.Restaurant),
    Networking("Networking", Icons.Filled.Groups),
    Technology("Technology", Icons.Filled.Computer),
    Education("Education", Icons.Filled.School),
    Wellness("Wellness", Icons.Filled.Favorite)
}
