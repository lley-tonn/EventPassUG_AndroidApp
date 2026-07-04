package com.eventpass.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.PrimaryButton
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.profile.components.CenteredFlowScaffold
import com.eventpass.feature.profile.components.FormCard
import com.eventpass.feature.profile.components.InsetTextField

/**
 * "Add Phone Number" sheet (iOS reference IMG_2780). Collects a phone number
 * for SMS verification; the action enables once [canAdd] is true.
 */
@Composable
fun AddPhoneScreen(
    phoneNumber: String,
    canAdd: Boolean,
    onPhoneChange: (String) -> Unit,
    onCancel: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenteredFlowScaffold(
        onCancel = onCancel,
        icon = Icons.Filled.Phone,
        iconTint = EventPassColors.Primary,
        title = "Add Phone Number",
        subtitle = {
            Text(
                text = "Add a phone number to your account for SMS verification and login",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier
    ) {
        FormCard {
            InsetTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                placeholder = "+256 700 123 456",
                keyboardType = KeyboardType.Phone
            )
        }
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = "A verification code will be sent to this number",
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.lg))
        PrimaryButton(
            text = "Add Phone Number",
            onClick = onAdd,
            enabled = canAdd
        )
    }
}
