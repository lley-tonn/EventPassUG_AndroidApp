package com.eventpass.feature.organizer.createevent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

private data class WizardStep(val label: String, val sub: String)

private val wizardSteps = listOf(
    WizardStep("Step 1", "Details"),
    WizardStep("Step 2", "Tickets"),
    WizardStep("Step 3", "Review")
)

/**
 * Shared chrome for the 3-step Create Event wizard: the Cancel / title /
 * Save Draft header, the step progress bar with labels, a scrollable [content]
 * body and a caller-supplied [footer]. [currentStep] is 0-based.
 */
@Composable
fun CreateEventScaffold(
    currentStep: Int,
    onCancel: () -> Unit,
    onSaveDraft: () -> Unit,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.White)
            .statusBarsPadding()
    ) {
        Header(onCancel = onCancel, onSaveDraft = onSaveDraft)
        StepBar(currentStep = currentStep)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(top = Spacing.lg, bottom = Spacing.lg)
        ) {
            content()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl, vertical = Spacing.md)
                .navigationBarsPadding()
        ) {
            footer()
        }
    }
}

@Composable
private fun Header(onCancel: () -> Unit, onSaveDraft: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderPill("Cancel", EventPassColors.Ink, onCancel, Modifier.align(Alignment.CenterVertically))
        Text(
            text = "Create Event",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        HeaderPill("Save Draft", EventPassColors.Primary, onSaveDraft, Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
private fun HeaderPill(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(Radii.Pill)
            .background(EventPassColors.BackgroundLight)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

@Composable
private fun StepBar(currentStep: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs)) {
        // Progress rail
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(EventPassColors.DividerLight)
        ) {
            val fraction = (currentStep + 1) / wizardSteps.size.toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(EventPassColors.Primary)
            )
        }
        Spacer(Modifier.height(Spacing.sm))
        Row(modifier = Modifier.fillMaxWidth()) {
            wizardSteps.forEachIndexed { index, step ->
                val active = index <= currentStep
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = when (index) {
                        0 -> Alignment.Start
                        wizardSteps.lastIndex -> Alignment.End
                        else -> Alignment.CenterHorizontally
                    }
                ) {
                    Text(
                        step.label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (active) EventPassColors.Primary else EventPassColors.InkMuted
                    )
                    Text(
                        step.sub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = EventPassColors.InkMuted
                    )
                }
            }
        }
        Spacer(Modifier.height(Spacing.sm))
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(EventPassColors.DividerLight))
    }
}
