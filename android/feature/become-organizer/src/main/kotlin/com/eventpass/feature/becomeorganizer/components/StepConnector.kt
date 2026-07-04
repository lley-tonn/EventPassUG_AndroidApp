package com.eventpass.feature.becomeorganizer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors

/**
 * Node-and-connector step indicator used at the top of the Become-an-Organizer
 * flow (matches iOS reference IMG_2776 / IMG_2784).
 *
 * Past steps render as a filled orange dot with a white check; the current step
 * is a larger soft-orange dot; upcoming steps are small grey dots. The rail
 * leading into a reached step is orange, otherwise grey.
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
            if (index > 0) {
                // Rail is orange up to and including the segment feeding the current step.
                val railActive = index <= currentStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (railActive) EventPassColors.Primary else EventPassColors.OutlineLight)
                )
            }
            when {
                index < currentStep -> Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(EventPassColors.Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = EventPassColors.White,
                        modifier = Modifier.size(11.dp)
                    )
                }

                index == currentStep -> Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(EventPassColors.PrimaryLight)
                )

                else -> Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(EventPassColors.OutlineLight)
                )
            }
        }
    }
}
