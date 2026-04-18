package com.eventpass.feature.auth.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.EventPassButton
import com.eventpass.core.design.components.OutlinedField
import com.eventpass.core.design.components.OutlinedPasswordField
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * Email + password sign-in. Stateless — all fields and loading/error state are
 * hoisted to the caller so the :feature:auth module stays free of :app deps.
 */
@Composable
fun LoginScreen(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onForgotPassword: () -> Unit,
    isLoading: Boolean,
    errorText: String?,
    modifier: Modifier = Modifier
) {
    val canSubmit = email.isNotBlank() && password.isNotBlank() && !isLoading

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.White)
            .statusBarsPadding()
            .imePadding()
    ) {
        HeaderBar(onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
        ) {
            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = EventPassColors.Ink
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Sign in to continue to EventPass",
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )

            Spacer(Modifier.height(Spacing.xxxl))

            OutlinedField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "you@example.com",
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(Spacing.lg))

            OutlinedPasswordField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "Enter your password"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot password?",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    color = EventPassColors.Primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onForgotPassword)
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                )
            }

            if (errorText != null) {
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodySmall,
                    color = EventPassColors.Error
                )
            }
        }

        // Footer CTA
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
                .navigationBarsPadding()
        ) {
            EventPassButton(
                text = if (isLoading) "Signing in…" else "Sign In",
                onClick = onSubmit,
                enabled = canSubmit
            )
        }
    }
}

@Composable
private fun HeaderBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Back",
                tint = EventPassColors.Ink,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun LoginPreview() {
    EventPassTheme {
        LoginScreen(
            email = "alex@example.com",
            password = "hunter2",
            onEmailChange = {},
            onPasswordChange = {},
            onSubmit = {},
            onBack = {},
            onForgotPassword = {},
            isLoading = false,
            errorText = null
        )
    }
}
