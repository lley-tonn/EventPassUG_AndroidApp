package com.eventpass.core.design.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * Circular "gauge" used for Health Score / Event readiness on the organizer dashboard.
 *
 * Draws a partial arc from the top, filling clockwise to represent `progress` (0f..1f).
 * Colour ramps: red (<0.4) -> amber (<0.7) -> green (>=0.7), unless an explicit [color] is passed.
 */
@Composable
fun ProgressDial(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    trackColor: Color = EventPassColors.DividerLight,
    color: Color? = null,
    label: String? = null,
    valueText: String? = null
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(targetValue = safeProgress, label = "dialProgress")
    val resolved = color ?: when {
        safeProgress < 0.4f -> EventPassColors.Error
        safeProgress < 0.7f -> EventPassColors.Warning
        else -> EventPassColors.Success
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2f
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            drawArc(
                color = resolved,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = valueText ?: "${(safeProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink
            )
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = EventPassColors.InkMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ProgressDialPreview() {
    EventPassTheme {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            ProgressDial(progress = 0.25f, label = "Score", size = 100.dp, strokeWidth = 10.dp)
            ProgressDial(progress = 0.55f, label = "Score", size = 100.dp, strokeWidth = 10.dp)
            ProgressDial(progress = 0.85f, label = "Score", size = 100.dp, strokeWidth = 10.dp)
        }
    }
}
