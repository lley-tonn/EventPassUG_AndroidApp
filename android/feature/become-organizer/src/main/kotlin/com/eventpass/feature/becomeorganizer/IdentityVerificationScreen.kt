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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.becomeorganizer.components.OrganizerFooter
import com.eventpass.feature.becomeorganizer.components.OrganizerTopBar
import com.eventpass.feature.becomeorganizer.components.StepConnector

/**
 * "Become an Organizer" — Step 2 of 5: Identity Verification.
 *
 * Invites the user to upload a government document (routing into the National ID
 * upload flow) and explains why verification is required. Continue stays
 * disabled until [identityVerified]; tapping it early surfaces an inline error
 * (matches design reference IMG_2784/IMG_2785).
 */
@Composable
fun IdentityVerificationScreen(
    identityVerified: Boolean,
    onCancel: () -> Unit,
    onChooseDocument: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.White)
            .statusBarsPadding()
    ) {
        OrganizerTopBar(onCancel = onCancel)

        StepConnector(
            totalSteps = 5,
            currentStep = 1,
            modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.sm)
        )
        Text(
            text = "Step 2 of 5: Identity Verification",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Spacing.xxl))
            Icon(
                imageVector = Icons.Filled.GppGood,
                contentDescription = null,
                tint = EventPassColors.Primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = "Verify Your Identity",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = EventPassColors.Ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "To protect our community, we require identity verification for all organizers.",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.xxl))

            Text(
                text = "Choose a verification document:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.lg))

            DocumentRow(onClick = onChooseDocument)

            Spacer(Modifier.height(Spacing.xxxl))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Why do we verify?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = EventPassColors.Ink
                )
                Spacer(Modifier.height(Spacing.md))
                ReasonRow(Icons.Filled.Shield, "Protect attendees from fraud")
                ReasonRow(Icons.Filled.HowToReg, "Build trust in the community")
                ReasonRow(Icons.Filled.Lock, "Secure payment processing")
                ReasonRow(Icons.Filled.WorkspacePremium, "Verified organizer badge")
            }

            Spacer(Modifier.height(Spacing.xl))
        }

        OrganizerFooter(
            primaryLabel = "Continue",
            onPrimary = { if (identityVerified) onContinue() else showError = true },
            primaryEnabled = identityVerified,
            showError = showError && !identityVerified,
            errorText = "Complete identity verification to continue",
            onBack = onBack
        )
    }
}

@Composable
private fun DocumentRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.Card)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Radii.sm))
                .background(EventPassColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Badge,
                contentDescription = null,
                tint = EventPassColors.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.width(Spacing.lg))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "National ID or Passport",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Upload front and back images",
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = EventPassColors.InkSubtle,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ReasonRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EventPassColors.Primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(Spacing.md))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted
        )
    }
}
