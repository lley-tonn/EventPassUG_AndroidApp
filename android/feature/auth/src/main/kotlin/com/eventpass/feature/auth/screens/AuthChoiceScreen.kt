package com.eventpass.feature.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.HaloIcon
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.core.design.tokens.softShadow
import androidx.compose.foundation.clickable

/**
 * Post-onboarding entry screen. Matches iOS reference IMG_2756.
 *
 * Three choices:
 *   1. Sign In — quiet card with halo person icon + chevron
 *   2. Host Your Own Events — loud orange-gradient hero card (attendees still tap to
 *      continue; this is just an emphasis choice mirroring iOS)
 *   3. Browse Events — quiet card for guest mode
 */
@Composable
fun AuthChoiceScreen(
    onSignIn: () -> Unit,
    onHostEvents: () -> Unit,
    onBrowseAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(Spacing.xxxl))

        // Ticket logo
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ConfirmationNumber,
                contentDescription = null,
                tint = EventPassColors.Primary,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        Text(
            text = "Welcome to EventPass",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = EventPassColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "Choose how you'd like to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(Spacing.xxxl))

        ChoiceCard(
            icon = Icons.Filled.Person,
            title = "Sign In",
            subtitle = "Already have an account?",
            onClick = onSignIn
        )

        Spacer(Modifier.height(Spacing.lg))

        HeroChoiceCard(
            icon = Icons.Filled.Campaign,
            title = "Host Your Own Events",
            subtitle = "Create and manage events, sell tickets, and grow your audience",
            onClick = onHostEvents
        )

        Spacer(Modifier.height(Spacing.lg))

        ChoiceCard(
            icon = Icons.Filled.Visibility,
            title = "Browse Events",
            subtitle = "Explore without signing in",
            iconTint = EventPassColors.InkMuted,
            iconBackground = EventPassColors.DividerLight,
            onClick = onBrowseAsGuest
        )

        Spacer(Modifier.height(Spacing.xxl))
    }
}

@Composable
private fun ChoiceCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = EventPassColors.Primary,
    iconBackground: Color = EventPassColors.Primary.copy(alpha = 0.12f)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(elevation = 4.dp, shape = Radii.Card)
            .clip(Radii.Card)
            .background(EventPassColors.White)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HaloIcon(
            icon = icon,
            size = 44.dp,
            tint = iconTint,
            background = iconBackground
        )
        Spacer(Modifier.size(Spacing.lg))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = EventPassColors.Ink
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = EventPassColors.InkMuted
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = EventPassColors.InkSubtle
        )
    }
}

@Composable
private fun HeroChoiceCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(EventPassColors.PrimaryLight, EventPassColors.Primary)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(
                elevation = 10.dp,
                shape = Radii.CardLarge,
                color = EventPassColors.Primary.copy(alpha = 0.28f)
            )
            .clip(Radii.CardLarge)
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.xl, vertical = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(EventPassColors.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EventPassColors.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.White.copy(alpha = 0.92f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun AuthChoicePreview() {
    EventPassTheme {
        AuthChoiceScreen(
            onSignIn = {},
            onHostEvents = {},
            onBrowseAsGuest = {}
        )
    }
}
