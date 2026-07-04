package com.eventpass.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.PrimaryButton
import com.eventpass.core.design.components.SegmentedControl
import com.eventpass.core.design.components.SegmentedOption
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.profile.components.FormCard
import com.eventpass.feature.profile.components.InsetTextField
import com.eventpass.feature.profile.components.ModalTopBar

enum class IdDocumentType { NATIONAL_ID, PASSPORT }

/** Identity-document details captured on the verification sheet. */
data class NationalIdVerificationState(
    val documentType: IdDocumentType,
    val fullName: String,
    val documentNumber: String,
    val frontCaptured: Boolean,
    val backCaptured: Boolean
)

/**
 * National ID / Passport verification sheet (iOS reference IMG_2786/IMG_2787).
 *
 * Reached from the organizer identity step and from Profile → Verify National
 * ID. Collects the document type, holder name, document number and photos.
 * Submit enables once the required fields and captures are present.
 */
@Composable
fun NationalIdVerificationScreen(
    state: NationalIdVerificationState,
    onDocumentTypeChange: (IdDocumentType) -> Unit,
    onFullNameChange: (String) -> Unit,
    onDocumentNumberChange: (String) -> Unit,
    onCaptureFront: () -> Unit,
    onCaptureBack: () -> Unit,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNationalId = state.documentType == IdDocumentType.NATIONAL_ID
    val docLabel = if (isNationalId) "National ID" else "Passport"

    val canSubmit = state.fullName.isNotBlank() &&
        state.documentNumber.isNotBlank() &&
        state.frontCaptured &&
        (!isNationalId || state.backCaptured)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
            .statusBarsPadding()
    ) {
        ModalTopBar(title = "", onCancel = onCancel)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxxl)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(Spacing.md))
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
                    color = EventPassColors.Ink
                )
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = "To access organizer features, we need to verify your identity. This helps ensure the safety and security of our event community.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = EventPassColors.InkMuted,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(Spacing.xl))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(EventPassColors.DividerLight)
            )
            Spacer(Modifier.height(Spacing.xl))

            FieldLabel("Document Type")
            SegmentedControl(
                options = listOf(SegmentedOption("National ID"), SegmentedOption("Passport")),
                selectedIndex = if (isNationalId) 0 else 1,
                onSelected = { index ->
                    onDocumentTypeChange(if (index == 0) IdDocumentType.NATIONAL_ID else IdDocumentType.PASSPORT)
                }
            )

            Spacer(Modifier.height(Spacing.lg))
            FieldLabel("Full Name")
            FormCard {
                InsetTextField(
                    value = state.fullName,
                    onValueChange = onFullNameChange,
                    placeholder = "Full name as on document"
                )
            }

            Spacer(Modifier.height(Spacing.lg))
            FieldLabel(if (isNationalId) "National ID Number" else "Passport Number")
            FormCard {
                InsetTextField(
                    value = state.documentNumber,
                    onValueChange = onDocumentNumberChange,
                    placeholder = if (isNationalId) "e.g., CM12345678901234" else "e.g., A01234567"
                )
            }

            Spacer(Modifier.height(Spacing.lg))
            FieldLabel(if (isNationalId) "$docLabel (Front)" else "$docLabel (Photo Page)")
            CaptureRow(
                text = if (isNationalId) "Capture Front of ID" else "Capture Photo Page",
                captured = state.frontCaptured,
                onClick = onCaptureFront
            )

            if (isNationalId) {
                Spacer(Modifier.height(Spacing.lg))
                FieldLabel("$docLabel (Back)")
                CaptureRow(
                    text = "Capture Back of ID",
                    captured = state.backCaptured,
                    onClick = onCaptureBack
                )
            }

            Spacer(Modifier.height(Spacing.xl))
            EncryptionNote()

            Spacer(Modifier.height(Spacing.xl))
            PrimaryButton(
                text = "Submit for Verification",
                onClick = onSubmit,
                enabled = canSubmit
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = EventPassColors.InkMuted,
        modifier = Modifier.padding(bottom = Spacing.sm)
    )
}

@Composable
private fun CaptureRow(text: String, captured: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.PhotoCamera,
            contentDescription = null,
            tint = EventPassColors.Primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(Spacing.md))
        Text(
            text = if (captured) "$text ✓" else text,
            style = MaterialTheme.typography.titleMedium,
            color = EventPassColors.Ink,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = EventPassColors.InkSubtle,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun EncryptionNote() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.Card)
            .background(EventPassColors.White)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Filled.Shield,
            contentDescription = null,
            tint = EventPassColors.Primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(Spacing.sm))
        Text(
            text = "Your information is encrypted and securely stored. We only use it to verify your identity for organizer activities.",
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted
        )
    }
}
