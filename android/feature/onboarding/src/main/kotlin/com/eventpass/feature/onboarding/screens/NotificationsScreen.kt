package com.eventpass.feature.onboarding.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.eventpass.core.design.components.EventPassButton
import com.eventpass.core.design.components.ButtonVariant
import com.eventpass.core.design.components.HaloIcon
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.core.design.tokens.softShadow
import com.eventpass.feature.onboarding.components.OnboardingScaffold

/**
 * Step 5/6 — ask for notifications permission. Matches iOS IMG_2754.
 *
 * On Android 13+ (API 33), triggers the POST_NOTIFICATIONS runtime permission dialog.
 * Below 33 notifications are granted by default — the card still lets the user acknowledge.
 */
@Composable
fun NotificationsScreen(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    var requested by remember { mutableStateOf(enabled) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onEnabledChange(granted)
        requested = true
    }

    OnboardingScaffold(
        currentStep = 4,
        totalSteps = 6,
        primaryLabel = "Continue",
        onPrimary = onContinue,
        primaryEnabled = true,
        onBack = onBack,
        footerHelper = "You can always enable this later"
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "Stay in the loop",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Get notified about events you'll love",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.xxl))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .softShadow(elevation = 6.dp, shape = Radii.CardLarge)
                    .clip(Radii.CardLarge)
                    .background(EventPassColors.White)
                    .padding(Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                HaloIcon(
                    icon = Icons.Filled.Notifications,
                    size = 56.dp,
                    tint = EventPassColors.Primary
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = EventPassColors.Ink
                )
                Text(
                    text = "Receive updates about new events, ticket sales, and reminders for events you're attending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EventPassColors.InkMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Spacing.md))
                EventPassButton(
                    text = if (enabled) "Notifications Enabled" else "Enable Notifications",
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val granted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) {
                                onEnabledChange(true)
                            } else {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } else {
                            onEnabledChange(true)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Primary,
                    enabled = !enabled
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun NotificationsScreenPreview() {
    EventPassTheme {
        NotificationsScreen(
            enabled = false,
            onEnabledChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}
