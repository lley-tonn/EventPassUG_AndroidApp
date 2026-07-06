package com.eventpass.feature.becomeorganizer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.OutlinedField
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.becomeorganizer.components.OrganizerStepScaffold

/** Public contact + brand details captured in Step 3. */
data class ContactInformationState(
    val usePersonalEmail: Boolean,
    val usePersonalPhone: Boolean,
    val accountEmail: String?,
    val accountPhone: String?,
    val brandName: String,
    val website: String,
    val instagram: String,
    val twitter: String,
    val facebook: String
)

/**
 * "Become an Organizer" — Step 3 of 5: Contact Information.
 *
 * Toggles to reuse the account email/phone publicly, plus optional brand and
 * social handles. Continue requires a brand/organization name (matches
 * reference IMG_2788/IMG_2789).
 */
@Composable
fun ContactInformationScreen(
    state: ContactInformationState,
    onToggleEmail: (Boolean) -> Unit,
    onTogglePhone: (Boolean) -> Unit,
    onBrandNameChange: (String) -> Unit,
    onWebsiteChange: (String) -> Unit,
    onInstagramChange: (String) -> Unit,
    onTwitterChange: (String) -> Unit,
    onFacebookChange: (String) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canContinue = state.brandName.isNotBlank() &&
        state.accountEmail != null && state.accountPhone != null

    OrganizerStepScaffold(
        stepNumber = 3,
        stepTitle = "Contact Information",
        onCancel = onCancel,
        primaryLabel = "Continue",
        primaryEnabled = canContinue,
        onPrimary = onContinue,
        onBack = onBack,
        showError = false,
        errorText = "",
        modifier = modifier
    ) {
        Spacer(Modifier.height(Spacing.md))
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(EventPassColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = EventPassColors.White,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = "Contact Information",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = EventPassColors.Ink
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "This information will be visible to attendees on your event pages.",
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(Spacing.xxl))

        SectionHeader("Required Information")
        ToggleRow(
            label = "Public Email",
            title = "Use my account email",
            subtitle = state.accountEmail ?: "No account email",
            checked = state.usePersonalEmail,
            onCheckedChange = onToggleEmail
        )
        Spacer(Modifier.height(Spacing.lg))
        ToggleRow(
            label = "Public Phone Number",
            title = "Use my account phone",
            subtitle = state.accountPhone ?: "No account phone",
            checked = state.usePersonalPhone,
            onCheckedChange = onTogglePhone
        )

        Spacer(Modifier.height(Spacing.xxl))

        SectionHeader("Brand & Social Platforms")
        OutlinedField(
            value = state.brandName,
            onValueChange = onBrandNameChange,
            label = "Brand/Organization Name",
            placeholder = "Your Brand Name"
        )
        Spacer(Modifier.height(Spacing.lg))
        OutlinedField(
            value = state.website,
            onValueChange = onWebsiteChange,
            label = "Website",
            placeholder = "https://yourwebsite.com",
            keyboardType = KeyboardType.Uri
        )
        Spacer(Modifier.height(Spacing.lg))
        OutlinedField(
            value = state.instagram,
            onValueChange = onInstagramChange,
            label = "Instagram Handle",
            placeholder = "@yourhandle"
        )
        Spacer(Modifier.height(Spacing.lg))
        OutlinedField(
            value = state.twitter,
            onValueChange = onTwitterChange,
            label = "Twitter/X Handle",
            placeholder = "@yourhandle"
        )
        Spacer(Modifier.height(Spacing.lg))
        OutlinedField(
            value = state.facebook,
            onValueChange = onFacebookChange,
            label = "Facebook Page",
            placeholder = "facebook.com/yourpage",
            keyboardType = KeyboardType.Uri
        )

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = EventPassColors.Ink,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.md)
    )
}

@Composable
private fun ToggleRow(
    label: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted
        )
        Spacer(Modifier.height(Spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = EventPassColors.Ink,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = EventPassColors.White,
                    checkedTrackColor = EventPassColors.Primary,
                    checkedBorderColor = EventPassColors.Primary
                )
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted
        )
    }
}
