package com.eventpass.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.EventPassButton
import com.eventpass.core.design.components.ButtonVariant
import com.eventpass.core.design.components.StepProgress
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * Shared layout for every onboarding step.
 *
 * Top: "Settings ◀" back affordance (iOS convention) + [StepProgress].
 * Middle: [content] (fills available space, scroll handled by caller if needed).
 * Bottom: footer with Back + primary action (usually Continue).
 *
 * [primaryEnabled] mirrors the iOS disabled-grey look when answers are missing.
 * If [onBack] is null, the Back button is hidden and the Continue spans full width.
 */
@Composable
fun OnboardingScaffold(
    currentStep: Int,
    totalSteps: Int,
    primaryLabel: String,
    onPrimary: () -> Unit,
    primaryEnabled: Boolean,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    showHeaderBack: Boolean = true,
    onSettings: (() -> Unit)? = null,
    footerHelper: String? = null,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.White)
            .statusBarsPadding()
    ) {
        // Top header — "◀ Settings" tap target
        if (showHeaderBack) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .let { if (onSettings != null) it.clickable(onClick = onSettings) else it }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = null,
                        tint = EventPassColors.Ink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.size(2.dp))
                    Text(
                        text = "Settings",
                        color = EventPassColors.Ink,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        } else {
            Spacer(Modifier.height(Spacing.xxxl))
        }

        // Step indicator
        StepProgress(
            totalSteps = totalSteps,
            currentStep = currentStep,
            modifier = Modifier.padding(vertical = Spacing.sm)
        )

        // Body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            content(PaddingValues(horizontal = Spacing.xl))
        }

        // Footer helper text (e.g. "You can change this later in settings")
        if (footerHelper != null) {
            Text(
                text = footerHelper,
                color = EventPassColors.InkSubtle,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Footer — Back + Primary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            if (onBack != null) {
                EventPassButton(
                    text = "< Back",
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Secondary
                )
            }
            EventPassButton(
                text = primaryLabel,
                onClick = onPrimary,
                modifier = Modifier.weight(1f),
                variant = ButtonVariant.Primary,
                enabled = primaryEnabled
            )
        }
    }
}
