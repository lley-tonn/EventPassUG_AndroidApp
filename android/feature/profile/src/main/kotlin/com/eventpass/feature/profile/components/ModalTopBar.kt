package com.eventpass.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/**
 * iOS-style modal header used across the profile / contact-verification sheets.
 *
 * Left: optional "Cancel" pill. Center: bold [title]. Right: optional action
 * pill (e.g. Save / Update / Done) that greys out when [actionEnabled] is false.
 * Pills sit on a white capsule so they read on the grey grouped background.
 */
@Composable
fun ModalTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null,
    actionLabel: String? = null,
    actionEnabled: Boolean = true,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
    ) {
        if (onCancel != null) {
            PillButton(
                text = "Cancel",
                color = EventPassColors.Ink,
                onClick = onCancel,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.Ink,
            modifier = Modifier.align(Alignment.Center)
        )

        if (actionLabel != null) {
            PillButton(
                text = actionLabel,
                color = if (actionEnabled) EventPassColors.Ink else EventPassColors.InkSubtle,
                onClick = { if (actionEnabled) onAction?.invoke() },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun PillButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(Radii.Pill)
            .background(EventPassColors.White)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}
