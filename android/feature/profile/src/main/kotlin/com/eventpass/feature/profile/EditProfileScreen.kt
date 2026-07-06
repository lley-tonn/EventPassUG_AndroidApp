package com.eventpass.feature.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.profile.components.FormCard
import com.eventpass.feature.profile.components.FormDivider
import com.eventpass.feature.profile.components.FormSectionLabel
import com.eventpass.feature.profile.components.InsetTextField
import com.eventpass.feature.profile.components.ModalTopBar

/**
 * "Edit Profile" sheet (design reference IMG_2779).
 *
 * Stateless — the caller owns first/last name edit state and the contact
 * values. [canSave] drives the Save pill; contact rows push into the dedicated
 * change-email / add-phone flows via [onChangeEmail] / [onAddPhone].
 */
@Composable
fun EditProfileScreen(
    firstName: String,
    lastName: String,
    email: String?,
    isEmailVerified: Boolean,
    phoneNumber: String?,
    canSave: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onChangePhoto: () -> Unit,
    onChangeEmail: () -> Unit,
    onAddPhone: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
            .statusBarsPadding()
    ) {
        ModalTopBar(
            title = "Edit Profile",
            actionLabel = "Save",
            actionEnabled = canSave,
            onAction = onSave
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxxl)
        ) {
            // Profile Photo
            FormSectionLabel("Profile Photo")
            FormCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(EventPassColors.Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = EventPassColors.White,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                    Spacer(Modifier.height(Spacing.md))
                    Text(
                        text = "Change Photo",
                        style = MaterialTheme.typography.titleMedium,
                        color = EventPassColors.Primary,
                        modifier = Modifier.clickable(onClick = onChangePhoto)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            // Personal Information
            FormSectionLabel("Personal Information")
            FormCard {
                InsetTextField(
                    value = firstName,
                    onValueChange = onFirstNameChange,
                    placeholder = "First Name"
                )
                FormDivider()
                InsetTextField(
                    value = lastName,
                    onValueChange = onLastNameChange,
                    placeholder = "Last Name"
                )
            }

            Spacer(Modifier.height(Spacing.xl))

            // Email Address
            FormSectionLabel("Email Address")
            FormCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = email ?: "No email set",
                            style = MaterialTheme.typography.bodyLarge,
                            color = EventPassColors.Ink
                        )
                        if (email != null && !isEmailVerified) {
                            Spacer(Modifier.height(Spacing.xs))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Error,
                                    contentDescription = null,
                                    tint = EventPassColors.Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(Spacing.xs))
                                Text(
                                    text = "Not Verified",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EventPassColors.Primary
                                )
                            }
                        }
                    }
                    Text(
                        text = "Change",
                        style = MaterialTheme.typography.titleMedium,
                        color = EventPassColors.Primary,
                        modifier = Modifier.clickable(onClick = onChangeEmail)
                    )
                }
            }
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Changing your email requires verification. Your current email remains active until the new one is verified.",
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted,
                modifier = Modifier.padding(horizontal = Spacing.xs)
            )

            Spacer(Modifier.height(Spacing.xl))

            // Phone Number
            FormSectionLabel("Phone Number")
            FormCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAddPhone)
                        .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (phoneNumber == null) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(EventPassColors.Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = EventPassColors.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.width(Spacing.md))
                        Text(
                            text = "Add Phone Number",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = EventPassColors.Ink
                        )
                    } else {
                        Text(
                            text = phoneNumber,
                            style = MaterialTheme.typography.bodyLarge,
                            color = EventPassColors.Ink
                        )
                    }
                }
            }
        }
    }
}
