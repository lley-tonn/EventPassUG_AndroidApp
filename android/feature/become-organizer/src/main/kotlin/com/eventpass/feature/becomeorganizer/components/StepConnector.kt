package com.eventpass.feature.becomeorganizer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors

/**
 * Node-and-connector step indicator used at the top of the Become-an-Organizer
 * flow — a filled orange dot for the current/past steps, grey dots for future
 * steps, joined by thin grey rails (matches iOS reference IMG_2776/IMG_2777).
 */
@Composable
fun StepConnector(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val reached = index <= currentStep
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (reached) EventPassColors.Primary else EventPassColors.OutlineLight)
            )
            if (index < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(EventPassColors.OutlineLight)
                )
            }
        }
    }
}
