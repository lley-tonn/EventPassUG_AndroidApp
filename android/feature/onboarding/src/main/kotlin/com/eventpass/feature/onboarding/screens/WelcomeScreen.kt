package com.eventpass.feature.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.onboarding.components.OnboardingScaffold

/**
 * Step 1/6 — welcome splash with the layered-halo ticket logo.
 * Matches iOS IMG_2750.
 */
@Composable
fun WelcomeScreen(
    onContinue: () -> Unit
) {
    OnboardingScaffold(
        currentStep = 0,
        totalSteps = 6,
        primaryLabel = "Get Started",
        onPrimary = onContinue,
        primaryEnabled = true,
        onBack = null
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            LayeredHaloLogo()
            Spacer(Modifier.height(Spacing.xxxl))
            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.titleLarge,
                color = EventPassColors.InkMuted
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "EventPass",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = EventPassColors.Primary
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "Discover, book, and experience\nthe best events in Uganda",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1.2f))
        }
    }
}

/** Three nested circles with a centered ticket icon — the EventPass hero mark. */
@Composable
private fun LayeredHaloLogo() {
    Box(contentAlignment = Alignment.Center) {
        HaloRing(size = 220.dp, color = EventPassColors.Primary.copy(alpha = 0.08f))
        HaloRing(size = 170.dp, color = EventPassColors.Primary.copy(alpha = 0.14f))
        HaloRing(size = 120.dp, color = EventPassColors.Primary.copy(alpha = 0.22f))
        Icon(
            imageVector = Icons.Filled.ConfirmationNumber,
            contentDescription = "EventPass",
            tint = Color.White,
            modifier = Modifier.size(54.dp)
        )
    }
}

@Composable
private fun HaloRing(size: androidx.compose.ui.unit.Dp, color: Color) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun WelcomeScreenPreview() {
    EventPassTheme {
        WelcomeScreen(onContinue = {})
    }
}
