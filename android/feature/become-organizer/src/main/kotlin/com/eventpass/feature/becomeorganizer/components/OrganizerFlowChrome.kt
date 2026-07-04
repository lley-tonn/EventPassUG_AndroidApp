package com.eventpass.feature.becomeorganizer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/**
 * Shared header for every Become-an-Organizer step: a "Cancel" pill followed by
 * the flow title.
 */
@Composable
fun OrganizerTopBar(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(Radii.Pill)
                .background(EventPassColors.BackgroundLight)
                .clickable(onClick = onCancel)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.titleMedium,
                color = EventPassColors.Ink
            )
        }
        Spacer(Modifier.width(Spacing.md))
        Text(
            text = "Become an Organizer",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.Ink
        )
    }
}

/**
 * Shared footer: a primary Continue button (greyed until [primaryEnabled]), an
 * optional secondary Back button, and an inline validation error surfaced when
 * the user taps Continue while the step is incomplete.
 */
@Composable
fun OrganizerFooter(
    primaryLabel: String,
    onPrimary: () -> Unit,
    primaryEnabled: Boolean,
    showError: Boolean,
    errorText: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    primaryTrailingIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clip(Radii.Button)
                .background(
                    if (primaryEnabled) EventPassColors.Primary
                    else EventPassColors.OutlineLight.copy(alpha = 0.7f)
                )
                .clickable(onClick = onPrimary),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = primaryLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = EventPassColors.White
                )
                Spacer(Modifier.width(Spacing.sm))
                Icon(
                    imageVector = primaryTrailingIcon,
                    contentDescription = null,
                    tint = EventPassColors.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (onBack != null) {
            Spacer(Modifier.height(Spacing.md))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .clip(Radii.Button)
                    .background(EventPassColors.BackgroundLight)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = EventPassColors.Ink,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Text(
                        text = "Back",
                        style = MaterialTheme.typography.titleMedium,
                        color = EventPassColors.Ink
                    )
                }
            }
        }

        if (showError) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.Error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
