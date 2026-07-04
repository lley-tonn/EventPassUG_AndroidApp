package com.eventpass.feature.becomeorganizer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * Shared page frame for a Become-an-Organizer step: Cancel header, the step
 * connector + "Step N of 5: {title}" caption, a scrollable [content] body, and
 * the Continue/Back footer with inline validation error.
 *
 * [stepNumber] is 1-based; [totalSteps] defaults to the 5-step flow.
 */
@Composable
fun OrganizerStepScaffold(
    stepNumber: Int,
    stepTitle: String,
    onCancel: () -> Unit,
    primaryLabel: String,
    primaryEnabled: Boolean,
    onPrimary: () -> Unit,
    showError: Boolean,
    errorText: String,
    modifier: Modifier = Modifier,
    totalSteps: Int = 5,
    onBack: (() -> Unit)? = null,
    primaryTrailingIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.White)
            .statusBarsPadding()
    ) {
        OrganizerTopBar(onCancel = onCancel)

        StepConnector(
            totalSteps = totalSteps,
            currentStep = stepNumber - 1,
            modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.sm)
        )
        Text(
            text = "Step $stepNumber of $totalSteps: $stepTitle",
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.md)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }

        OrganizerFooter(
            primaryLabel = primaryLabel,
            onPrimary = onPrimary,
            primaryEnabled = primaryEnabled,
            showError = showError,
            errorText = errorText,
            onBack = onBack,
            primaryTrailingIcon = primaryTrailingIcon
        )
    }
}
