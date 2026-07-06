package com.eventpass.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.profile.components.FormCard
import com.eventpass.feature.profile.components.FormDivider
import com.eventpass.feature.profile.components.FormSectionLabel
import com.eventpass.feature.profile.components.InsetTextField
import com.eventpass.feature.profile.components.ModalTopBar

/**
 * "Change Email" sheet (design reference IMG_2778).
 *
 * Shows the current email, a new-email field and a password-confirm field. The
 * Update pill enables once both fields are filled ([canUpdate]).
 */
@Composable
fun ChangeEmailScreen(
    currentEmail: String,
    newEmail: String,
    confirmPassword: String,
    canUpdate: Boolean,
    onNewEmailChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onCancel: () -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
            .statusBarsPadding()
    ) {
        ModalTopBar(
            title = "Change Email",
            onCancel = onCancel,
            actionLabel = "Update",
            actionEnabled = canUpdate,
            onAction = onUpdate
        )

        Column(modifier = Modifier.padding(horizontal = Spacing.xl)) {
            Spacer(Modifier.height(Spacing.sm))
            FormSectionLabel("Update Email")
            FormCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Email",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = EventPassColors.Ink
                    )
                    Spacer(Modifier.width(Spacing.lg))
                    Text(
                        text = currentEmail,
                        style = MaterialTheme.typography.bodyLarge,
                        color = EventPassColors.InkMuted
                    )
                }
                FormDivider()
                InsetTextField(
                    value = newEmail,
                    onValueChange = onNewEmailChange,
                    placeholder = "New Email Address",
                    keyboardType = KeyboardType.Email
                )
                FormDivider()
                InsetTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    placeholder = "Confirm Password",
                    keyboardType = KeyboardType.Password,
                    secure = true
                )
            }
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "A verification link will be sent to your new email. Your current email remains active until verification is complete.",
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted,
                modifier = Modifier.padding(horizontal = Spacing.xs)
            )
        }
    }
}
