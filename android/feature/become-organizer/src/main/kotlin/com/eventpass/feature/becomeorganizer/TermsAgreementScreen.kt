package com.eventpass.feature.becomeorganizer

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.becomeorganizer.components.OrganizerStepScaffold

private data class TermSection(val icon: ImageVector, val title: String, val body: String)

private val termSections = listOf(
    TermSection(
        Icons.AutoMirrored.Filled.Undo,
        "Refund Policy",
        "You are responsible for honouring the refund policy you set for each event. EventPass may process refunds on your behalf in line with consumer-protection requirements."
    ),
    TermSection(
        Icons.Filled.Shield,
        "Fraud Prevention",
        "You agree not to list fraudulent events or misrepresent ticket availability. Accounts involved in fraudulent activity are suspended and funds withheld pending review."
    ),
    TermSection(
        Icons.Filled.Work,
        "Organizer Responsibilities",
        "You are responsible for delivering the events you publish, providing accurate information, and supporting your attendees before, during and after each event."
    ),
    TermSection(
        Icons.Filled.Warning,
        "Liability Limitations",
        "EventPass acts as a ticketing platform only and is not liable for the conduct or cancellation of events. Liability is limited to the fees collected on a given transaction."
    ),
    TermSection(
        Icons.Filled.AttachMoney,
        "Financial Terms",
        "Platform and payment-processing fees are deducted from ticket sales before payout. Payouts are issued 3-5 business days after an event ends."
    )
)

/**
 * "Become an Organizer" — Step 5 of 5: Terms Agreement.
 *
 * Expandable summaries of the organizer terms plus a required agreement
 * checkbox. Complete Registration stays disabled until [agreed]; tapping it
 * early surfaces an inline error (matches iOS reference IMG_2792/IMG_2793).
 */
@Composable
fun TermsAgreementScreen(
    agreed: Boolean,
    onAgreedChange: (Boolean) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showError by remember { mutableStateOf(false) }

    OrganizerStepScaffold(
        stepNumber = 5,
        stepTitle = "Terms Agreement",
        onCancel = onCancel,
        primaryLabel = "Complete Registration",
        primaryEnabled = agreed,
        onPrimary = { if (agreed) onComplete() else showError = true },
        onBack = onBack,
        showError = showError && !agreed,
        errorText = "You must agree to the terms to continue",
        primaryTrailingIcon = Icons.Filled.Check,
        modifier = modifier
    ) {
        Spacer(Modifier.height(Spacing.md))
        Icon(
            imageVector = Icons.Filled.Description,
            contentDescription = null,
            tint = EventPassColors.Primary,
            modifier = Modifier.size(60.dp)
        )
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = "Organizer Agreement",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = EventPassColors.Ink
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Please review and agree to the organizer terms before completing your registration.",
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(Spacing.xl))

        termSections.forEach { section ->
            TermRow(section)
        }

        Spacer(Modifier.height(Spacing.lg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = agreed,
                onCheckedChange = onAgreedChange,
                colors = CheckboxDefaults.colors(checkedColor = EventPassColors.Primary)
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = "I have read and agree to the Organizer Terms of Service, including all responsibilities, policies, and limitations outlined above.",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.Ink,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 14.dp)
            )
        }

        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = "Version 1.0 • Last updated: November 2024",
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkSubtle
        )
        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun TermRow(section: TermSection) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(Radii.xs))
                    .background(EventPassColors.Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    tint = EventPassColors.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(Spacing.md))
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = EventPassColors.InkMuted,
                modifier = Modifier.size(22.dp)
            )
        }
        if (expanded) {
            Text(
                text = section.body,
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted,
                modifier = Modifier.padding(bottom = Spacing.lg)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(EventPassColors.DividerLight)
        )
    }
}
