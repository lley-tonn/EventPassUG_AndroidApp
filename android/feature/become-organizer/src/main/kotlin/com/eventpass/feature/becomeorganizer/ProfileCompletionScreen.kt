package com.eventpass.feature.becomeorganizer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.becomeorganizer.components.ChecklistRow
import com.eventpass.feature.becomeorganizer.components.ChecklistStatus
import com.eventpass.feature.becomeorganizer.components.StepConnector

/**
 * Data backing the Step 1 profile checklist — kept primitive so the feature
 * module doesn't depend on the domain `User` type. A `null` field means the
 * requirement is not yet satisfied.
 */
data class ProfileCompletionData(
    val fullName: String?,
    val verifiedEmail: String?,
    val verifiedPhone: String?,
    val profilePhotoUrl: String?
)

/**
 * "Become an Organizer" — Step 1 of 5: Profile Completion.
 *
 * Shows a checklist of the identity requirements needed before a user can host
 * events. Full legal name, a verified email, and a verified phone number are
 * required; a profile photo is optional. Continue stays disabled until every
 * required item is satisfied; tapping it while incomplete surfaces an inline
 * error (matches iOS reference IMG_2776/IMG_2777).
 */
@Composable
fun ProfileCompletionScreen(
    data: ProfileCompletionData,
    onCancel: () -> Unit,
    onVerifyEmail: () -> Unit,
    onVerifyPhone: () -> Unit,
    onAddPhoto: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val requiredComplete = !data.fullName.isNullOrBlank() &&
        !data.verifiedEmail.isNullOrBlank() &&
        !data.verifiedPhone.isNullOrBlank()

    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.White)
            .statusBarsPadding()
    ) {
        TopBar(onCancel = onCancel)

        StepConnector(
            totalSteps = 5,
            currentStep = 0,
            modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.sm)
        )
        Text(
            text = "Step 1 of 5: Profile Completion",
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.md)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Spacing.xxl))
            ProfileBadge()
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = "Complete Your Profile",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = EventPassColors.Ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Before becoming an organizer, we need to verify your profile information.",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Spacing.xl)
            )

            Spacer(Modifier.height(Spacing.xxl))

            ChecklistRow(
                status = if (!data.fullName.isNullOrBlank()) ChecklistStatus.Completed else ChecklistStatus.Required,
                title = "Full Legal Name",
                subtitle = data.fullName?.takeIf { it.isNotBlank() } ?: "Not set"
            )
            ChecklistRow(
                status = if (!data.verifiedEmail.isNullOrBlank()) ChecklistStatus.Completed else ChecklistStatus.Required,
                title = "Verified Email Address",
                subtitle = data.verifiedEmail?.takeIf { it.isNotBlank() } ?: "Not set",
                onClick = onVerifyEmail
            )
            ChecklistRow(
                status = if (!data.verifiedPhone.isNullOrBlank()) ChecklistStatus.Completed else ChecklistStatus.Required,
                title = "Verified Phone Number",
                subtitle = data.verifiedPhone?.takeIf { it.isNotBlank() } ?: "Not set",
                onClick = onVerifyPhone
            )
            ChecklistRow(
                status = if (!data.profilePhotoUrl.isNullOrBlank()) ChecklistStatus.Completed else ChecklistStatus.Optional,
                title = "Profile Photo",
                subtitle = if (!data.profilePhotoUrl.isNullOrBlank()) "Added" else "Optional",
                optionalSuffix = true,
                onClick = onAddPhoto
            )

            Spacer(Modifier.height(Spacing.xl))
        }

        Footer(
            enabled = requiredComplete,
            showError = showError && !requiredComplete,
            onContinue = {
                if (requiredComplete) onContinue() else showError = true
            }
        )
    }
}

// MARK: - Top bar

@Composable
private fun TopBar(onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(Radii.Pill)
                .background(EventPassColors.BackgroundLight)
                .clickable(onClick = onCancel)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.titleMedium,
                color = EventPassColors.Ink
            )
        }
        Spacer(Modifier.width(Spacing.md))
        Text(
            text = "Become an Organizer",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.Ink
        )
    }
}

// MARK: - Profile badge (person + shield-check)

@Composable
private fun ProfileBadge() {
    Box(contentAlignment = Alignment.BottomEnd) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = EventPassColors.Primary,
            modifier = Modifier.size(72.dp)
        )
        Icon(
            imageVector = Icons.Filled.Verified,
            contentDescription = null,
            tint = EventPassColors.Primary,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(EventPassColors.White)
                .padding(2.dp)
        )
    }
}

// MARK: - Footer

@Composable
private fun Footer(
    enabled: Boolean,
    showError: Boolean,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clip(Radii.Button)
                .background(
                    if (enabled) EventPassColors.Primary
                    else EventPassColors.OutlineLight.copy(alpha = 0.7f)
                )
                .clickable(onClick = onContinue),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    color = EventPassColors.White
                )
                Spacer(Modifier.width(Spacing.sm))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = EventPassColors.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        if (showError) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "Please complete all required fields to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.Error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
