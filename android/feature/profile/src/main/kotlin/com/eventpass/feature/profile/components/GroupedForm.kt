package com.eventpass.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/**
 * Section label above a grouped card (grey, uppercase-ish caption).
 */
@Composable
fun FormSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = EventPassColors.InkMuted,
        modifier = modifier.padding(start = Spacing.xs, bottom = Spacing.sm)
    )
}

/**
 * White rounded card that groups form rows (iOS inset-grouped style).
 */
@Composable
fun FormCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
    ) {
        content()
    }
}

/**
 * Thin inset divider between rows in a [FormCard].
 */
@Composable
fun FormDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.xl)
            .height(0.5.dp)
            .background(EventPassColors.DividerLight)
    )
}

/**
 * Borderless inset text field used inside grouped cards. Shows [placeholder]
 * when empty. Height and horizontal padding match the iOS list rows.
 */
@Composable
fun InsetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    secure: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current
) {
    val style = textStyle.merge(MaterialTheme.typography.bodyLarge).copy(color = EventPassColors.Ink)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, style = style, color = EventPassColors.InkSubtle)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = style,
            singleLine = true,
            cursorBrush = SolidColor(EventPassColors.Primary),
            visualTransformation = if (secure) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
