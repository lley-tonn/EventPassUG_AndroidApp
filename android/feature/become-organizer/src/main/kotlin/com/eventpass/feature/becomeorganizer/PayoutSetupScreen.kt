package com.eventpass.feature.becomeorganizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.OutlinedField
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.becomeorganizer.components.OrganizerStepScaffold

enum class PayoutMethod { MTN, AIRTEL, BANK }

/** Payout method + account details captured in Step 4. */
data class PayoutSetupState(
    val method: PayoutMethod,
    val useAccountNumber: Boolean,
    val accountPhone: String?,
    val customNumber: String,
    val bankName: String,
    val bankAccountNumber: String,
    val bankAccountName: String
)

private val PayoutMethod.providerName: String
    get() = when (this) {
        PayoutMethod.MTN -> "MTN Mobile Money"
        PayoutMethod.AIRTEL -> "Airtel Money"
        PayoutMethod.BANK -> "Bank Account"
    }

/**
 * "Become an Organizer" — Step 4 of 5: Payout Setup.
 *
 * Pick a payout method (MTN / Airtel / Bank) and provide the matching account
 * details. Continue enables once the selected method has usable details
 * (matches iOS reference IMG_2790/IMG_2791).
 */
@Composable
fun PayoutSetupScreen(
    state: PayoutSetupState,
    onSelectMethod: (PayoutMethod) -> Unit,
    onToggleUseAccountNumber: (Boolean) -> Unit,
    onCustomNumberChange: (String) -> Unit,
    onBankNameChange: (String) -> Unit,
    onBankAccountNumberChange: (String) -> Unit,
    onBankAccountNameChange: (String) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canContinue = when (state.method) {
        PayoutMethod.BANK -> state.bankName.isNotBlank() &&
            state.bankAccountNumber.isNotBlank() &&
            state.bankAccountName.isNotBlank()
        else -> (state.useAccountNumber && state.accountPhone != null) ||
            state.customNumber.isNotBlank()
    }

    OrganizerStepScaffold(
        stepNumber = 4,
        stepTitle = "Payout Setup",
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
                .clip(RoundedCornerShape(Radii.sm))
                .background(EventPassColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Payments,
                contentDescription = null,
                tint = EventPassColors.White,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = "Payout Setup",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = EventPassColors.Ink
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Choose how you want to receive payments from ticket sales.",
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(Spacing.xxl))

        SectionHeaderLeft("Select Payout Method")
        MethodCard(
            selected = state.method == PayoutMethod.MTN,
            icon = Icons.Filled.Phone,
            iconTint = EventPassColors.Primary,
            title = "MTN Mobile Money",
            subtitle = "Receive payments via MTN Mobile Money",
            onClick = { onSelectMethod(PayoutMethod.MTN) }
        )
        Spacer(Modifier.height(Spacing.md))
        MethodCard(
            selected = state.method == PayoutMethod.AIRTEL,
            icon = Icons.Filled.Phone,
            iconTint = EventPassColors.Error,
            title = "Airtel Money",
            subtitle = "Receive payments via Airtel Money",
            onClick = { onSelectMethod(PayoutMethod.AIRTEL) }
        )
        Spacer(Modifier.height(Spacing.md))
        MethodCard(
            selected = state.method == PayoutMethod.BANK,
            icon = Icons.Filled.AccountBalance,
            iconTint = EventPassColors.Info,
            title = "Bank Account",
            subtitle = "Direct bank transfer (3-5 days)",
            onClick = { onSelectMethod(PayoutMethod.BANK) }
        )

        Spacer(Modifier.height(Spacing.xxl))

        SectionHeaderLeft("Account Details")
        if (state.method == PayoutMethod.BANK) {
            OutlinedField(
                value = state.bankName,
                onValueChange = onBankNameChange,
                label = "Bank Name",
                placeholder = "e.g., Stanbic Bank"
            )
            Spacer(Modifier.height(Spacing.lg))
            OutlinedField(
                value = state.bankAccountNumber,
                onValueChange = onBankAccountNumberChange,
                label = "Account Number",
                placeholder = "Account number",
                keyboardType = KeyboardType.Number
            )
            Spacer(Modifier.height(Spacing.lg))
            OutlinedField(
                value = state.bankAccountName,
                onValueChange = onBankAccountNameChange,
                label = "Account Holder Name",
                placeholder = "Full name on the account"
            )
        } else {
            MobileMoneyDetails(
                providerName = state.method.providerName,
                useAccountNumber = state.useAccountNumber,
                accountPhone = state.accountPhone,
                customNumber = state.customNumber,
                onToggle = onToggleUseAccountNumber,
                onCustomNumberChange = onCustomNumberChange
            )
        }

        Spacer(Modifier.height(Spacing.xl))
        ImportantCard()
        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun SectionHeaderLeft(text: String) {
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
private fun MethodCard(
    selected: Boolean,
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.Card)
            .then(
                if (selected) Modifier
                    .background(EventPassColors.PrimarySoft)
                    .border(1.5.dp, EventPassColors.Primary, Radii.Card)
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(EventPassColors.Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = EventPassColors.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .border(2.dp, EventPassColors.OutlineLight, CircleShape)
            )
        }
        Spacer(Modifier.width(Spacing.md))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )
        }
    }
}

@Composable
private fun MobileMoneyDetails(
    providerName: String,
    useAccountNumber: Boolean,
    accountPhone: String?,
    customNumber: String,
    onToggle: (Boolean) -> Unit,
    onCustomNumberChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Mobile Money Number",
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
                text = if (useAccountNumber) "Use my account number:\n${accountPhone ?: "No number"}"
                else "Enter a different number",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = EventPassColors.Ink,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = useAccountNumber,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = EventPassColors.White,
                    checkedTrackColor = EventPassColors.Primary,
                    checkedBorderColor = EventPassColors.Primary
                )
            )
        }
        if (useAccountNumber) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Make sure this number is registered with $providerName",
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.Primary
            )
        } else {
            Spacer(Modifier.height(Spacing.md))
            OutlinedField(
                value = customNumber,
                onValueChange = onCustomNumberChange,
                label = "Payout Number",
                placeholder = "+256 700 123 456",
                keyboardType = KeyboardType.Phone
            )
        }
    }
}

@Composable
private fun ImportantCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.Card)
            .background(EventPassColors.InfoSoft)
            .padding(Spacing.lg)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = EventPassColors.Info,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = "Important",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Info
            )
        }
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Payouts are processed within 3-5 business days after each event ends. " +
                "You can change your payout method anytime from your organizer settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted
        )
    }
}
