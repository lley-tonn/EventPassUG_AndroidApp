package com.eventpass.android.core.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.eventpass.core.design.tokens.EventPassColors

/**
 * Prompt shown when a guest tries to open a sign-in-protected route. Offers to
 * take them to the sign-in flow or dismiss.
 */
@Composable
fun SignInRequiredDialog(
    message: String,
    onSignIn: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sign in required") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onSignIn) {
                Text("Sign In", color = EventPassColors.Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not now", color = EventPassColors.InkMuted)
            }
        }
    )
}
