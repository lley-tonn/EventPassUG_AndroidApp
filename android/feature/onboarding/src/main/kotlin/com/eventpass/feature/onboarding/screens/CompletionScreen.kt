package com.eventpass.feature.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.eventpass.feature.onboarding.components.OnboardingScaffold
import com.eventpass.feature.onboarding.model.InterestCategory
import com.eventpass.feature.onboarding.model.Role

/**
 * Step 6/6 — confirm completion. Matches iOS IMG_2755.
 * Large green check halo, welcome line, and a summary card of what we collected.
 */
@Composable
fun CompletionScreen(
    role: Role?,
    fullName: String,
    interestCount: Int,
    notificationsEnabled: Boolean,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    OnboardingScaffold(
        currentStep = 5,
        totalSteps = 6,
        primaryLabel = "Start Exploring",
        onPrimary = onFinish,
        primaryEnabled = true,
        onBack = onBack
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Spacing.md))
            GreenCheckHalo()
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = "You're all set!",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Welcome, ${fullName.trim().substringBefore(' ').ifBlank { "friend" }}!",
                style = MaterialTheme.typography.titleMedium,
                color = EventPassColors.InkMuted
            )
            if (role == Role.Organizer) {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = "Create your first event and start selling tickets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EventPassColors.InkSubtle,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(Spacing.xl))
            SummaryCard(
                role = role,
                interestCount = interestCount,
                notificationsEnabled = notificationsEnabled
            )
        }
    }
}

@Composable
private fun GreenCheckHalo() {
    Box(contentAlignment = Alignment.Center) {
        Ring(size = 180.dp, color = EventPassColors.Success.copy(alpha = 0.08f))
        Ring(size = 140.dp, color = EventPassColors.Success.copy(alpha = 0.14f))
        Ring(size = 100.dp, color = EventPassColors.Success.copy(alpha = 0.22f))
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(EventPassColors.Success),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }
    }
}

@Composable
private fun Ring(size: androidx.compose.ui.unit.Dp, color: Color) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun SummaryCard(
    role: Role?,
    interestCount: Int,
    notificationsEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.BackgroundLight)
            .padding(vertical = Spacing.sm, horizontal = Spacing.lg)
    ) {
        SummaryRow(
            icon = Icons.Filled.Person,
            tint = EventPassColors.InkMuted,
            label = "Role",
            value = role?.label ?: "—"
        )
        SummaryRow(
            icon = Icons.Filled.Favorite,
            tint = EventPassColors.InkMuted,
            label = "Event Types",
            value = "$interestCount selected"
        )
        SummaryRow(
            icon = Icons.Filled.Notifications,
            tint = EventPassColors.Primary,
            label = "Notifications",
            value = if (notificationsEnabled) "Enabled" else "Disabled",
            last = true
        )
    }
}

@Composable
private fun SummaryRow(
    icon: ImageVector,
    tint: Color,
    label: String,
    value: String,
    last: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HaloIcon(
            icon = icon,
            size = 28.dp,
            tint = tint,
            background = tint.copy(alpha = 0.12f)
        )
        Spacer(Modifier.size(Spacing.md))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun CompletionScreenPreview() {
    EventPassTheme {
        CompletionScreen(
            role = Role.Organizer,
            fullName = "Agenorwoth",
            interestCount = 3,
            notificationsEnabled = true,
            onBack = {},
            onFinish = {}
        )
    }
}
