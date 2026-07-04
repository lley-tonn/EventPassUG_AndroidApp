package com.eventpass.android.features.organizer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventpass.android.domain.models.User
import com.eventpass.feature.becomeorganizer.ProfileCompletionData
import com.eventpass.feature.becomeorganizer.ProfileCompletionScreen

/**
 * :app-side wrapper for Become-an-Organizer Step 1. Owns the VM, maps the
 * domain `User` to the feature's primitive [ProfileCompletionData], and
 * forwards row/navigation actions.
 */
@Composable
fun BecomeOrganizerScreen(
    onCancel: () -> Unit,
    onVerifyEmail: () -> Unit = {},
    onVerifyPhone: () -> Unit = {},
    onAddPhoto: () -> Unit = {},
    onContinue: () -> Unit = {},
    viewModel: BecomeOrganizerViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    ProfileCompletionScreen(
        data = user.toProfileCompletionData(),
        onCancel = onCancel,
        onVerifyEmail = onVerifyEmail,
        onVerifyPhone = onVerifyPhone,
        onAddPhoto = onAddPhoto,
        onContinue = onContinue
    )
}

private fun User?.toProfileCompletionData(): ProfileCompletionData {
    val u = this
    return ProfileCompletionData(
        fullName = u?.fullName?.takeIf { it.isNotBlank() },
        verifiedEmail = u?.email?.takeIf { u.isEmailVerified },
        verifiedPhone = u?.phoneNumber?.takeIf { u.isPhoneVerified },
        profilePhotoUrl = u?.profileImageUrl
    )
}
