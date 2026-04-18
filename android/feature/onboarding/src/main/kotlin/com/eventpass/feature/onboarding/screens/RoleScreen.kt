package com.eventpass.feature.onboarding.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.eventpass.feature.onboarding.components.OnboardingScaffold
import com.eventpass.feature.onboarding.model.Role

/**
 * Step 2/6 — pick Attendee or Organizer. Matches iOS IMG_2751.
 * Two large tappable cards, each with a halo icon and radio indicator at the bottom.
 */
@Composable
fun RoleScreen(
    selected: Role?,
    onSelect: (Role) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    OnboardingScaffold(
        currentStep = 1,
        totalSteps = 6,
        primaryLabel = "Continue",
        onPrimary = onContinue,
        primaryEnabled = selected != null,
        onBack = onBack,
        footerHelper = "You can change this later in settings"
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "How will you\nuse EventPass?",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink
            )
            Text(
                text = "Choose your primary role",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.xs))
            RoleCard(
                role = Role.Attendee,
                icon = Icons.Filled.ConfirmationNumber,
                selected = selected == Role.Attendee,
                onClick = { onSelect(Role.Attendee) }
            )
            RoleCard(
                role = Role.Organizer,
                icon = Icons.Filled.CalendarToday,
                selected = selected == Role.Organizer,
                onClick = { onSelect(Role.Organizer) }
            )
        }
    }
}

@Composable
private fun RoleCard(
    role: Role,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        if (selected) EventPassColors.Primary else EventPassColors.DividerLight,
        label = "roleBorder"
    )
    val dotColor by animateColorAsState(
        if (selected) EventPassColors.Primary else androidx.compose.ui.graphics.Color.Transparent,
        label = "roleDot"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(elevation = 6.dp, shape = Radii.CardLarge)
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = Radii.CardLarge
            )
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xl, horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        HaloIcon(
            icon = icon,
            size = 56.dp,
            tint = EventPassColors.Ink,
            background = EventPassColors.DividerLight
        )
        Text(
            text = role.label,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.Ink
        )
        Text(
            text = role.description,
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Spacer(Modifier.height(Spacing.xs))
        // Radio indicator
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (selected) EventPassColors.Primary else EventPassColors.OutlineLight,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun RoleScreenPreview() {
    EventPassTheme {
        RoleScreen(selected = Role.Attendee, onSelect = {}, onBack = {}, onContinue = {})
    }
}
