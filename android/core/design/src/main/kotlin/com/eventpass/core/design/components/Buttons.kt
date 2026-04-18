package com.eventpass.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

private val ButtonMinHeight = 56.dp
private val ButtonCompactHeight = 44.dp

enum class ButtonVariant { Primary, Secondary, Tertiary, Destructive }
enum class ButtonSize { Regular, Compact }

@Composable
fun EventPassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Regular,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val (bg, fg, borderColor) = buttonColors(variant, enabled)
    val minHeight = if (size == ButtonSize.Regular) ButtonMinHeight else ButtonCompactHeight
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .clip(Radii.Button)
            .background(bg)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, Radii.Button) else Modifier
            )
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = ripple(color = fg),
                onClick = onClick
            )
            .padding(horizontal = Spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                androidx.compose.material3.Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(Spacing.sm))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = fg
            )
        }
    }
}

private data class BtnColors(val bg: Color, val fg: Color, val border: Color?)

@Composable
private fun buttonColors(variant: ButtonVariant, enabled: Boolean): BtnColors {
    val disabledBg = EventPassColors.OutlineLight.copy(alpha = 0.4f)
    val disabledFg = EventPassColors.White
    return when {
        !enabled -> BtnColors(disabledBg, disabledFg, null)
        variant == ButtonVariant.Primary -> BtnColors(EventPassColors.Primary, EventPassColors.White, null)
        variant == ButtonVariant.Secondary -> BtnColors(
            bg = EventPassColors.BackgroundLight,
            fg = EventPassColors.Ink,
            border = null
        )
        variant == ButtonVariant.Tertiary -> BtnColors(
            bg = Color.Transparent,
            fg = EventPassColors.Primary,
            border = EventPassColors.Primary
        )
        else -> BtnColors(EventPassColors.Error, EventPassColors.White, null)
    }
}

// Convenience wrappers — keep call sites terse.
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, leadingIcon: ImageVector? = null) =
    EventPassButton(text, onClick, modifier, ButtonVariant.Primary, enabled = enabled, leadingIcon = leadingIcon)

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) =
    EventPassButton(text, onClick, modifier, ButtonVariant.Secondary, enabled = enabled)

@Composable
fun TertiaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) =
    EventPassButton(text, onClick, modifier, ButtonVariant.Tertiary, enabled = enabled)

@Preview(name = "Buttons • Light", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Preview(name = "Buttons • Dark", showBackground = true, backgroundColor = 0xFF000000, uiMode = 0x21)
@Composable
private fun ButtonsPreview() {
    EventPassTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            PrimaryButton("Get Started", onClick = {})
            SecondaryButton("Back", onClick = {})
            TertiaryButton("Add Ticket Type", onClick = {})
            PrimaryButton("Continue", onClick = {}, enabled = false)
        }
    }
}
