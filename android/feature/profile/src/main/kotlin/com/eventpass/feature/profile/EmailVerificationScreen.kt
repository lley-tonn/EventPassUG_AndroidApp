package com.eventpass.feature.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.eventpass.core.design.components.PrimaryButton
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.feature.profile.components.CenteredFlowScaffold

/**
 * "Email Verification" sheet (iOS reference IMG_2781). Sends a verification
 * link to the user's current email; dismissed via the Done action.
 */
@Composable
fun EmailVerificationScreen(
    email: String,
    onDone: () -> Unit,
    onSendVerification: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenteredFlowScaffold(
        icon = Icons.Filled.MarkEmailUnread,
        iconTint = EventPassColors.Info,
        title = "Verify Your Email",
        topBarTitle = "Email Verification",
        actionLabel = "Done",
        onAction = onDone,
        subtitle = {
            Text(
                text = "We'll send a verification link to:\n$email",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier
    ) {
        PrimaryButton(
            text = "Send Verification Email",
            onClick = onSendVerification
        )
    }
}
