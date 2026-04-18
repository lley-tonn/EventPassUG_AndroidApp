package com.eventpass.feature.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Person
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
 * Create-account screen. Stateless — caller owns all field state + submission.
 *
 * Validation is minimal here (non-blank + password-length + match); deeper rules
 * live in the caller's `AuthFormState`.
 */
@Composable
fun SignUpScreen(
    fullName: String,
    email: String,
    password: String,
    confirmPassword: String,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean,
    errorText: String?,
    modifier: Modifier = Modifier
) {
    val passwordTooShort = password.isNotEmpty() && password.length < 6
    val passwordsMismatch = confirmPassword.isNotEmpty() && password != confirmPassword
    val canSubmit = fullName.isNotBlank() &&
        email.isNotBlank() &&
        password.length >= 6 &&
        password == confirmPassword &&
        !isLoading

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
                text = "Create account",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = EventPassColors.Ink
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Sign up to start discovering events",
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )

            Spacer(Modifier.height(Spacing.xxxl))

            OutlinedField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = "Full name",
                placeholder = "Enter your full name",
                leadingIcon = Icons.Filled.Person
            )

            Spacer(Modifier.height(Spacing.lg))

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
                placeholder = "At least 6 characters",
                errorText = if (passwordTooShort) "Password must be at least 6 characters" else null
            )

            Spacer(Modifier.height(Spacing.lg))

            OutlinedPasswordField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm password",
                placeholder = "Re-enter your password",
                errorText = if (passwordsMismatch) "Passwords don't match" else null
            )

            if (errorText != null) {
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodySmall,
                    color = EventPassColors.Error
                )
            }

            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = "By creating an account, you agree to our Terms of Service and Privacy Policy.",
                style = MaterialTheme.typography.bodySmall,
                color = EventPassColors.InkSubtle
            )

            Spacer(Modifier.height(Spacing.lg))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
                .navigationBarsPadding()
        ) {
            EventPassButton(
                text = if (isLoading) "Creating account…" else "Create Account",
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
private fun SignUpPreview() {
    EventPassTheme {
        SignUpScreen(
            fullName = "Alex Kamau",
            email = "alex@example.com",
            password = "hunter22",
            confirmPassword = "hunter22",
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onSubmit = {},
            onBack = {},
            isLoading = false,
            errorText = null
        )
    }
}
