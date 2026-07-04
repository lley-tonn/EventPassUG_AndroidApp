package com.eventpass.feature.becomeorganizer.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * Completion state of a single profile-checklist item, driving the leading
 * status indicator on the left of a [ChecklistRow].
 */
enum class ChecklistStatus {
    /** Requirement satisfied — green filled circle with a white check. */
    Completed,

    /** Required but not yet provided — hollow grey circle. */
    Required,

    /** Optional and not provided — orange dashed circle. */
    Optional
}

/**
 * One row in the "Complete Your Profile" checklist: a status indicator, the
 * requirement title (with an optional greyed "(Optional)" suffix), a subtitle
 * showing the current value or "Not set", and a trailing chevron when the row
 * is actionable. Completed rows are non-interactive.
 */
@Composable
fun ChecklistRow(
    status: ChecklistStatus,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    optionalSuffix: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val clickable = onClick != null && status != ChecklistStatus.Completed
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (clickable) Modifier.clickable(onClick = onClick!!) else Modifier)
            .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusIndicator(status)
        Spacer(Modifier.width(Spacing.lg))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = EventPassColors.Ink
                )
                if (optionalSuffix) {
                    Spacer(Modifier.width(Spacing.sm))
                    Text(
                        text = "(Optional)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EventPassColors.InkSubtle
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )
        }
        if (clickable) {
            Spacer(Modifier.width(Spacing.sm))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = EventPassColors.InkSubtle,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun StatusIndicator(status: ChecklistStatus) {
    when (status) {
        ChecklistStatus.Completed -> Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(EventPassColors.Success),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Completed",
                tint = EventPassColors.White,
                modifier = Modifier.size(16.dp)
            )
        }

        ChecklistStatus.Required -> Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(2.dp, EventPassColors.OutlineLight, CircleShape)
        )

        ChecklistStatus.Optional -> Canvas(modifier = Modifier.size(28.dp)) {
            drawCircle(
                color = EventPassColors.Primary,
                radius = (size.minDimension - 2.dp.toPx()) / 2f,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f
                    )
                )
            )
        }
    }
}
