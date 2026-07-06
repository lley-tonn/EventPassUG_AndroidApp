package com.eventpass.feature.profile

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.PrimaryButton
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.profile.components.CenteredFlowScaffold

/**
 * "Email Verification" sheet (design reference IMG_2781). Sends a verification
 * link to the user's current email; dismissed via the Done action. Once
 * [emailVerified] flips true it shows a confirmation state.
 */
@Composable
fun EmailVerificationScreen(
    email: String,
    onDone: () -> Unit,
    onSendVerification: () -> Unit,
    modifier: Modifier = Modifier,
    emailVerified: Boolean = false
) {
    CenteredFlowScaffold(
        icon = if (emailVerified) Icons.Filled.MarkEmailRead else Icons.Filled.MarkEmailUnread,
        iconTint = if (emailVerified) EventPassColors.Success else EventPassColors.Info,
        title = if (emailVerified) "Email Verified" else "Verify Your Email",
        topBarTitle = "Email Verification",
        actionLabel = "Done",
        onAction = onDone,
        subtitle = {
            Text(
                text = if (emailVerified) "$email is now verified."
                else "We'll send a verification link to:\n$email",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted,
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier
    ) {
        if (emailVerified) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = EventPassColors.Success,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "You're all set — tap Done to continue.",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = EventPassColors.Ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            PrimaryButton(
                text = "Send Verification Email",
                onClick = onSendVerification
            )
        }
    }
}
