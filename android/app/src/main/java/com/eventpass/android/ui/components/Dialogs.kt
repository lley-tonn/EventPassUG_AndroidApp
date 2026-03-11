package com.eventpass.android.ui.components

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.eventpass.android.ui.theme.EventPassColors
import com.eventpass.android.ui.theme.EventPassDimensions

/**
 * Authentication prompt bottom sheet.
 * Migrated from iOS UI/Components/AuthPromptSheet.swift
 *
 * SwiftUI → Compose mapping:
 * - .sheet() → ModalBottomSheet
 * - NavigationStack → Direct composable with callbacks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthPromptSheet(
    reason: String,
    icon: ImageVector,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier
        ) {
            AuthPromptContent(
                reason = reason,
                icon = icon,
                onDismiss = onDismiss,
                onSignIn = onSignIn,
                onCreateAccount = onCreateAccount
            )
        }
    }
}

/**
 * Auth prompt content.
 */
@Composable
private fun AuthPromptContent(
    reason: String,
    icon: ImageVector,
    onDismiss: () -> Unit,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val benefits = getBenefits(reason)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(EventPassDimensions.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(EventPassColors.Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = EventPassColors.Primary
            )
        }

        Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.lg))

        // Title
        Text(
            text = "Sign in $reason",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.sm))

        Text(
            text = "Create an account or sign in to unlock this feature",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.lg))

        // Benefits
        Column(
            verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.md)
        ) {
            benefits.forEach { benefit ->
                BenefitRow(text = benefit)
            }
        }

        Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.xl))

        // Buttons
        PrimaryButton(
            text = "Sign In",
            onClick = {
                onDismiss()
                onSignIn()
            }
        )

        Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.md))

        SecondaryButton(
            text = "Create Account",
            onClick = {
                onDismiss()
                onCreateAccount()
            }
        )

        Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.md))

        TextButton(onClick = onDismiss) {
            Text(
                text = "Not Now",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.md))
    }
}

/**
 * Benefit row with checkmark.
 */
@Composable
private fun BenefitRow(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = EventPassColors.Success
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Get benefits based on reason.
 */
private fun getBenefits(reason: String): List<String> {
    return when {
        reason.contains("like") || reason.contains("favorite") -> listOf(
            "Save and sync your favorites across devices",
            "Get personalized event recommendations",
            "Never miss events you're interested in"
        )
        reason.contains("follow") -> listOf(
            "Get notified about new events from organizers",
            "Discover similar organizers you might like",
            "Build your personalized event feed"
        )
        reason.contains("purchase") || reason.contains("buy") -> listOf(
            "Secure checkout with multiple payment options",
            "QR codes and Google Wallet integration",
            "Track all your tickets in one place"
        )
        reason.contains("rate") -> listOf(
            "Share your experience with other attendees",
            "Help others discover great events",
            "Build your event history and preferences"
        )
        else -> listOf(
            "Access exclusive features and benefits",
            "Personalized experience tailored to you",
            "Join thousands of event enthusiasts"
        )
    }
}

/**
 * Confirmation dialog.
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    isDestructive: Boolean = false,
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(EventPassDimensions.CornerRadius.lg),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(EventPassDimensions.Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.md))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.lg))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.md)
                    ) {
                        SecondaryButton(
                            text = cancelText,
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        )

                        if (isDestructive) {
                            DestructiveButton(
                                text = confirmText,
                                onClick = {
                                    onConfirm()
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            PrimaryButton(
                                text = confirmText,
                                onClick = {
                                    onConfirm()
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Loading overlay dialog.
 */
@Composable
fun LoadingOverlay(
    message: String? = null,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Dialog(onDismissRequest = { }) {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(EventPassDimensions.CornerRadius.md),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(EventPassDimensions.Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.md)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    message?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Success dialog.
 */
@Composable
fun SuccessDialog(
    title: String,
    message: String,
    buttonText: String = "Done",
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(EventPassDimensions.CornerRadius.lg),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(EventPassDimensions.Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(EventPassColors.Success.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = EventPassColors.Success
                        )
                    }

                    Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.md))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.sm))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(EventPassDimensions.Spacing.lg))

                    PrimaryButton(
                        text = buttonText,
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}
