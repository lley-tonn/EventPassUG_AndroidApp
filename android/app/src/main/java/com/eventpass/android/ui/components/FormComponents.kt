package com.eventpass.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.eventpass.android.ui.theme.EventPassColors
import com.eventpass.android.ui.theme.EventPassDimensions

/**
 * Styled text field.
 * Migrated from iOS UI/Components/FormInputComponents.swift
 *
 * SwiftUI → Compose mapping:
 * - TextField → BasicTextField (for custom styling) or TextField
 * - @FocusState → remember { mutableStateOf } + onFocusChanged
 * - .textInputAutocapitalization → KeyboardOptions(capitalization)
 * - .keyboardType → KeyboardOptions(keyboardType)
 */
@Composable
fun StyledTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    isError: Boolean = false,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm)
    ) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Input field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(EventPassDimensions.Input.height)
                .clip(RoundedCornerShape(EventPassDimensions.CornerRadius.input))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = when {
                        isError -> EventPassColors.Error
                        isFocused -> EventPassColors.Primary
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    },
                    shape = RoundedCornerShape(EventPassDimensions.CornerRadius.input)
                )
                .padding(horizontal = EventPassDimensions.Input.paddingHorizontal)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(EventPassDimensions.Input.iconSize),
                        tint = if (isFocused) EventPassColors.Primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(EventPassDimensions.Spacing.sm))
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused = it.isFocused },
                        enabled = enabled,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardType,
                            capitalization = capitalization,
                            imeAction = imeAction
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onImeAction() },
                            onNext = { onImeAction() }
                        ),
                        visualTransformation = if (isPassword) PasswordVisualTransformation()
                        else VisualTransformation.None,
                        cursorBrush = SolidColor(EventPassColors.Primary)
                    )
                }
            }
        }

        // Error message
        errorMessage?.let { error ->
            if (isError) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = EventPassColors.Error
                )
            }
        }
    }
}

/**
 * Styled multi-line text editor.
 */
@Composable
fun StyledTextEditor(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Int = 120,
    maxLines: Int = 10
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm)
    ) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Text area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight.dp)
                .clip(RoundedCornerShape(EventPassDimensions.CornerRadius.input))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) EventPassColors.Primary
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(EventPassDimensions.CornerRadius.input)
                )
                .padding(EventPassDimensions.Input.paddingHorizontal)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                cursorBrush = SolidColor(EventPassColors.Primary),
                maxLines = maxLines
            )
        }
    }
}

/**
 * Number input field.
 */
@Composable
fun StyledNumberField(
    label: String,
    placeholder: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    suffix: String? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    var textValue by remember(value) { mutableStateOf(if (value == 0) "" else value.toString()) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs)
    ) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Input field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(EventPassDimensions.Input.heightCompact)
                .clip(RoundedCornerShape(EventPassDimensions.CornerRadius.input))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) EventPassColors.Primary
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(EventPassDimensions.CornerRadius.input)
                )
                .padding(horizontal = EventPassDimensions.Input.paddingHorizontal)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                prefix?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(EventPassDimensions.Spacing.xs))
                }

                BasicTextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        // Only allow digits
                        val filtered = newValue.filter { it.isDigit() }
                        textValue = filtered
                        onValueChange(filtered.toIntOrNull() ?: 0)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isFocused = it.isFocused },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    cursorBrush = SolidColor(EventPassColors.Primary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (textValue.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                suffix?.let {
                    Spacer(modifier = Modifier.width(EventPassDimensions.Spacing.xs))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Form section container.
 */
@Composable
fun FormSection(
    title: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.md)
    ) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        content()
    }
}

/**
 * Price input field with currency prefix.
 */
@Composable
fun PriceField(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    currency: String = "UGX"
) {
    var isFocused by remember { mutableStateOf(false) }
    var textValue by remember(value) {
        mutableStateOf(if (value == 0.0) "" else value.toLong().toString())
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(EventPassDimensions.Input.heightCompact)
                .clip(RoundedCornerShape(EventPassDimensions.CornerRadius.input))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) EventPassColors.Primary
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(EventPassDimensions.CornerRadius.input)
                )
                .padding(horizontal = EventPassDimensions.Input.paddingHorizontal)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currency,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = EventPassColors.Primary
                )

                Spacer(modifier = Modifier.width(EventPassDimensions.Spacing.xs))

                BasicTextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { it.isDigit() }
                        textValue = filtered
                        onValueChange(filtered.toDoubleOrNull() ?: 0.0)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isFocused = it.isFocused },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    cursorBrush = SolidColor(EventPassColors.Primary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (textValue.isEmpty()) {
                                Text(
                                    text = "0",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}
