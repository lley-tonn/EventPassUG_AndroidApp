package com.eventpass.core.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

/**
 * iOS-style labeled outlined field.
 *
 * The label sits *above* the field as light gray text (not an inset floating label),
 * matching the style used on Sign Up / Edit Profile / Become Organizer flows.
 *
 * - Rounded 16dp corners
 * - Border: `OutlineLight` (focused => `Primary`)
 * - Error state: red border + helper text below
 * - Supports leading/trailing icons and a password-toggle variant
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    val isError = errorText != null
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = EventPassColors.InkMuted,
            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            placeholder = placeholder?.let {
                { Text(it, color = EventPassColors.InkSubtle) }
            },
            leadingIcon = leadingIcon?.let {
                { Icon(it, contentDescription = null, tint = EventPassColors.InkMuted) }
            },
            trailingIcon = trailingIcon?.let {
                {
                    if (onTrailingClick != null) {
                        IconButton(onClick = onTrailingClick) {
                            Icon(it, contentDescription = null, tint = EventPassColors.InkMuted)
                        }
                    } else {
                        Icon(it, contentDescription = null, tint = EventPassColors.InkMuted)
                    }
                }
            },
            singleLine = singleLine,
            enabled = enabled,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = Radii.Field,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EventPassColors.Primary,
                unfocusedBorderColor = EventPassColors.OutlineLight,
                disabledBorderColor = EventPassColors.DividerLight,
                errorBorderColor = EventPassColors.Error,
                focusedContainerColor = EventPassColors.White,
                unfocusedContainerColor = EventPassColors.White,
                disabledContainerColor = EventPassColors.BackgroundLight,
                errorContainerColor = EventPassColors.White,
                focusedTextColor = EventPassColors.Ink,
                unfocusedTextColor = EventPassColors.Ink
            )
        )
        val subtext = errorText ?: helperText
        if (subtext != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = if (isError) EventPassColors.Error else EventPassColors.InkMuted,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

/**
 * Convenience wrapper for a password field with show/hide eye toggle.
 */
@Composable
fun OutlinedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorText: String? = null
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        errorText = errorText,
        leadingIcon = Icons.Filled.Lock,
        trailingIcon = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
        onTrailingClick = { visible = !visible },
        keyboardType = KeyboardType.Password,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun OutlinedFieldPreview() {
    EventPassTheme {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            OutlinedField(
                value = "Alex Kamau",
                onValueChange = {},
                label = "Full name",
                placeholder = "Enter your name",
                leadingIcon = Icons.Filled.Person
            )
            OutlinedField(
                value = "alex@example.com",
                onValueChange = {},
                label = "Email",
                placeholder = "you@example.com",
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email,
                helperText = "We'll send you a verification code"
            )
            OutlinedField(
                value = "",
                onValueChange = {},
                label = "Email",
                placeholder = "you@example.com",
                leadingIcon = Icons.Filled.Email,
                errorText = "Please enter a valid email"
            )
            OutlinedPasswordField(
                value = "hunter2",
                onValueChange = {},
                label = "Password"
            )
        }
    }
}
