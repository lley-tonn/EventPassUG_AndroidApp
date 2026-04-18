package com.eventpass.feature.onboarding.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.HaloIcon
import com.eventpass.core.design.components.OutlinedField
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.onboarding.components.OnboardingScaffold
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Step 3/6 — collect full name + date of birth. Matches iOS IMG_2752.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    fullName: String,
    dateOfBirth: LocalDate?,
    onFullNameChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var pickerVisible by remember { mutableStateOf(false) }
    val canContinue = fullName.isNotBlank() && dateOfBirth != null

    OnboardingScaffold(
        currentStep = 2,
        totalSteps = 6,
        primaryLabel = "Continue",
        onPrimary = onContinue,
        primaryEnabled = canContinue,
        onBack = onBack
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "Tell us about yourself",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink
            )
            Text(
                text = "We'll personalize your experience",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted
            )
            Spacer(Modifier.height(Spacing.sm))

            OutlinedField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = "Full Name",
                placeholder = "Enter your full name"
            )

            DateOfBirthRow(
                date = dateOfBirth,
                onClick = { pickerVisible = true }
            )
        }
    }

    if (pickerVisible) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateOfBirth
                ?.atStartOfDay(ZoneId.of("UTC"))
                ?.toInstant()
                ?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { pickerVisible = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = pickerState.selectedDateMillis
                    if (millis != null) {
                        val picked = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        onDateChange(picked)
                    }
                    pickerVisible = false
                }) {
                    Text("OK", color = EventPassColors.Primary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pickerVisible = false }) {
                    Text("Cancel", color = EventPassColors.InkMuted)
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun DateOfBirthRow(
    date: LocalDate?,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = "Date of Birth",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = EventPassColors.InkMuted,
            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Radii.Field)
                .clickable(onClick = onClick)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HaloIcon(
                icon = Icons.Filled.CalendarMonth,
                size = 44.dp,
                tint = EventPassColors.Primary
            )
            Spacer(Modifier.size(Spacing.md))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Birthday",
                    style = MaterialTheme.typography.labelLarge,
                    color = EventPassColors.InkMuted
                )
                Text(
                    text = date?.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                        ?: "Select date",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = EventPassColors.Ink
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = EventPassColors.InkSubtle,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun PersonalInfoScreenPreview() {
    EventPassTheme {
        PersonalInfoScreen(
            fullName = "",
            dateOfBirth = null,
            onFullNameChange = {},
            onDateChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}
