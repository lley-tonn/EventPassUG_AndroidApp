package com.eventpass.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eventpass.core.design.components.PrimaryButton
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.profile.components.CenteredFlowScaffold

private const val CODE_LENGTH = 6

/**
 * "Verify Phone Number" sheet (design reference IMG_2782 / IMG_2783).
 *
 * A single screen with two phases driven by [codeSent]: first offer to send a
 * 6-digit SMS code, then collect and verify it. [code] is hoisted so the caller
 * owns the entry state.
 */
@Composable
fun PhoneVerificationScreen(
    phoneNumber: String,
    codeSent: Boolean,
    code: String,
    onCodeChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSendCode: () -> Unit,
    onVerifyCode: () -> Unit,
    onResendCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenteredFlowScaffold(
        icon = Icons.AutoMirrored.Filled.Chat,
        iconTint = EventPassColors.Primary,
        title = "Verify Phone Number",
        onCancel = onCancel,
        subtitle = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "We'll send a verification code to",
                    style = MaterialTheme.typography.bodyLarge,
                    color = EventPassColors.InkMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = phoneNumber,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = EventPassColors.Primary
                )
            }
        },
        modifier = modifier
    ) {
        if (!codeSent) {
            Text(
                text = "You'll receive a 6-digit code via SMS",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.lg))
            PrimaryButton(
                text = "Send Verification Code",
                onClick = onSendCode
            )
        } else {
            Text(
                text = "Enter the 6-digit code",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.lg))
            CodeField(code = code, onCodeChange = onCodeChange)
            Spacer(Modifier.height(Spacing.lg))
            PrimaryButton(
                text = "Verify Code",
                onClick = onVerifyCode,
                enabled = code.length == CODE_LENGTH
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = "Resend Code",
                style = MaterialTheme.typography.titleMedium,
                color = EventPassColors.Primary,
                modifier = Modifier.clickable(onClick = onResendCode)
            )
        }
    }
}

@Composable
private fun CodeField(code: String, onCodeChange: (String) -> Unit) {
    val textStyle = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        letterSpacing = 8.sp,
        color = if (code.isEmpty()) EventPassColors.InkSubtle else EventPassColors.Ink
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .padding(vertical = Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = code,
            onValueChange = { new -> onCodeChange(new.filter { it.isDigit() }.take(CODE_LENGTH)) },
            textStyle = textStyle,
            singleLine = true,
            cursorBrush = SolidColor(EventPassColors.Primary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.Center) {
                    if (code.isEmpty()) {
                        Text(
                            text = "000000",
                            style = textStyle,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    inner()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
