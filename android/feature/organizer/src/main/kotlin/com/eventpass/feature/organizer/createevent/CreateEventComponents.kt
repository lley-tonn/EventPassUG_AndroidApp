package com.eventpass.feature.organizer.createevent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/** Bold black field label sitting above a filled field. */
@Composable
fun WizardLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = EventPassColors.Ink,
        modifier = modifier.padding(bottom = Spacing.md)
    )
}

/** Filled grey rounded text field (Create Event form style). */
@Composable
fun FilledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Radii.Field)
            .background(EventPassColors.BackgroundLight)
            .border(1.dp, EventPassColors.DividerLight, Radii.Field)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalAlignment = if (minLines > 1) Alignment.Top else Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = EventPassColors.InkSubtle, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Spacing.md))
        }
        Box(modifier = Modifier.weight(1f).heightIn(min = (minLines * 22).dp), contentAlignment = Alignment.TopStart) {
            if (value.isEmpty()) {
                Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = EventPassColors.InkSubtle)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = EventPassColors.Ink),
                singleLine = minLines == 1,
                cursorBrush = SolidColor(EventPassColors.Primary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Grey rounded pill used for the date/time selectors. */
@Composable
fun DateTimePill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(Radii.Pill)
            .background(EventPassColors.BackgroundLight)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md)
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge, color = EventPassColors.Ink)
    }
}

/**
 * Wizard footer. When [onBack] is null the primary button spans full width
 * (Step 1); otherwise Back + primary sit side by side (Steps 2/3).
 */
@Composable
fun WizardFooter(
    primaryLabel: String,
    onPrimary: () -> Unit,
    primaryEnabled: Boolean,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    showPrimaryTrailingArrow: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        if (onBack != null) {
            OutlinedPillButton("Back", onBack, Modifier.weight(1f), leadingIcon = Icons.AutoMirrored.Filled.ArrowBack)
        }
        Box(
            modifier = Modifier
                .weight(if (onBack != null) 1.2f else 1f)
                .height(56.dp)
                .clip(Radii.Button)
                .background(if (primaryEnabled) EventPassColors.Primary else EventPassColors.Primary.copy(alpha = 0.45f))
                .clickable(enabled = primaryEnabled, onClick = onPrimary),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(primaryLabel, style = MaterialTheme.typography.titleMedium, color = EventPassColors.White)
                if (showPrimaryTrailingArrow) {
                    Spacer(Modifier.width(Spacing.sm))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = EventPassColors.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun OutlinedPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(Radii.Button)
            .border(1.5.dp, EventPassColors.Primary, Radii.Button)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = EventPassColors.Primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(Spacing.sm))
        }
        Text(text, style = MaterialTheme.typography.titleMedium, color = EventPassColors.Primary)
    }
}

/** Small palette helper so category colours stay consistent. */
internal fun categoryColor(): Color = EventPassColors.Primary
