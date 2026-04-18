package com.eventpass.core.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * Stepper pill used on multi-step flows (onboarding, become organizer).
 * The current step is elongated; past steps are coloured dots; future steps are grey dots.
 */
@Composable
fun StepProgress(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = EventPassColors.Primary,
    inactiveColor: Color = EventPassColors.OutlineLight,
    dotSize: androidx.compose.ui.unit.Dp = 8.dp,
    currentWidth: androidx.compose.ui.unit.Dp = 24.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val isPast = index < currentStep
            val isCurrent = index == currentStep
            val color by animateColorAsState(
                if (isPast || isCurrent) activeColor else inactiveColor,
                label = "stepColor"
            )
            val width by animateDpAsState(
                if (isCurrent) currentWidth else dotSize,
                label = "stepWidth"
            )
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(dotSize)
                    .clip(RoundedCornerShape(50))
                    .background(color)
                    .width(width)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun StepProgressPreview() {
    EventPassTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            StepProgress(totalSteps = 6, currentStep = 0)
            StepProgress(totalSteps = 6, currentStep = 2)
            StepProgress(totalSteps = 5, currentStep = 4)
        }
    }
}
