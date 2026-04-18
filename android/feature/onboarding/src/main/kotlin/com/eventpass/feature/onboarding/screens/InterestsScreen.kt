package com.eventpass.feature.onboarding.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.PillChip
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.onboarding.components.OnboardingScaffold
import com.eventpass.feature.onboarding.model.InterestCategory

/**
 * Step 4/6 — pick favorite event categories. Matches iOS IMG_2753.
 * Count of selected is rendered in orange above the chip grid.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsScreen(
    selected: Set<InterestCategory>,
    onToggle: (InterestCategory) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    OnboardingScaffold(
        currentStep = 3,
        totalSteps = 6,
        primaryLabel = "Continue",
        onPrimary = onContinue,
        primaryEnabled = selected.isNotEmpty(),
        onBack = onBack
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl)
        ) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "What interests you?",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Select your favorite event types",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "${selected.size} selected",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = EventPassColors.Primary
            )
            Spacer(Modifier.height(Spacing.md))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                InterestCategory.entries.forEach { interest ->
                    PillChip(
                        text = interest.label,
                        selected = interest in selected,
                        onClick = { onToggle(interest) },
                        leadingIcon = interest.icon
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun InterestsScreenPreview() {
    EventPassTheme {
        InterestsScreen(
            selected = setOf(InterestCategory.Music, InterestCategory.Festivals),
            onToggle = {},
            onBack = {},
            onContinue = {}
        )
    }
}
