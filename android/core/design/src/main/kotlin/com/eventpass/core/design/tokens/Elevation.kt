package com.eventpass.core.design.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * iOS-style soft shadow. Material's default elevation looks hard on Android —
 * we use a low-opacity ambient/spot colour and a larger radius.
 */
@Composable
fun Modifier.softShadow(
    elevation: Dp = 6.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = Color.Black.copy(alpha = 0.06f)
): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = color,
    spotColor = color,
    clip = false
)

object Elevation {
    val None: Dp = 0.dp
    val Flat: Dp = 1.dp
    val Card: Dp = 6.dp
    val Lifted: Dp = 10.dp
    val Sheet: Dp = 16.dp
}
