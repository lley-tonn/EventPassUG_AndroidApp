package com.eventpass.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eventpass.android.ui.theme.EventPassColors
import com.eventpass.android.ui.theme.EventPassDimensions

/**
 * Primary button component.
 * Migrated from iOS button styles.
 *
 * SwiftUI Button modifiers → Compose Button parameters:
 * - .buttonStyle(.borderedProminent) → Button with filled style
 * - .frame(height:) → Modifier.height()
 * - .cornerRadius() → shape = RoundedCornerShape()
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = true,
    height: Dp = EventPassDimensions.Button.heightLarge,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(EventPassDimensions.CornerRadius.button),
        colors = ButtonDefaults.buttonColors(
            containerColor = EventPassColors.Primary,
            contentColor = Color.White,
            disabledContainerColor = EventPassColors.Primary.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(
            horizontal = EventPassDimensions.Button.paddingHorizontal,
            vertical = EventPassDimensions.Button.paddingVertical
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(8.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Secondary button (outlined).
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = true,
    height: Dp = EventPassDimensions.Button.heightLarge,
    leadingIcon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(EventPassDimensions.CornerRadius.button),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) EventPassColors.Primary else EventPassColors.Primary.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = EventPassColors.Primary,
            disabledContentColor = EventPassColors.Primary.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(
            horizontal = EventPassDimensions.Button.paddingHorizontal,
            vertical = EventPassDimensions.Button.paddingVertical
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = EventPassColors.Primary,
                strokeWidth = 2.dp
            )
        } else {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(8.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Text button.
 */
@Composable
fun TertiaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = EventPassColors.Primary,
            disabledContentColor = EventPassColors.Primary.copy(alpha = 0.5f)
        )
    ) {
        leadingIcon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.size(6.dp)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Destructive button (for delete actions).
 */
@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = true,
    height: Dp = EventPassDimensions.Button.heightLarge
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(EventPassDimensions.CornerRadius.button),
        colors = ButtonDefaults.buttonColors(
            containerColor = EventPassColors.Error,
            contentColor = Color.White,
            disabledContainerColor = EventPassColors.Error.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(
            horizontal = EventPassDimensions.Button.paddingHorizontal,
            vertical = EventPassDimensions.Button.paddingVertical
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Small compact button.
 */
@Composable
fun CompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = modifier.height(EventPassDimensions.Button.heightSmall),
            enabled = enabled,
            shape = RoundedCornerShape(EventPassDimensions.CornerRadius.sm),
            colors = ButtonDefaults.buttonColors(
                containerColor = EventPassColors.Primary,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(4.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(EventPassDimensions.Button.heightSmall),
            enabled = enabled,
            shape = RoundedCornerShape(EventPassDimensions.CornerRadius.sm),
            border = BorderStroke(1.dp, EventPassColors.Primary),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = EventPassColors.Primary
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(4.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
