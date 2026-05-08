package com.eventpass.android.features.common.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventpass.android.BuildConfig
import com.eventpass.android.domain.models.User
import com.eventpass.android.domain.models.UserRole
import com.eventpass.feature.attendee.profile.ProfileHeaderData
import com.eventpass.feature.attendee.profile.ProfileScreen as ProfileContent

/**
 * :app-side wrapper for the Profile screen — owns the VM, maps domain
 * `User` to `ProfileHeaderData`, and forwards row clicks to navigation.
 */
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit = {},
    onNotificationSettings: () -> Unit = {},
    onPaymentMethods: () -> Unit = {},
    onPrivacySecurity: () -> Unit = {},
    onHelpSupport: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onBecomeOrganizer: () -> Unit = {},
    onVerifyNationalId: () -> Unit = {},
    onInterests: () -> Unit = {},
    onTermsPrivacy: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    ProfileContent(
        data = user.toHeaderData(),
        onVerifyNationalId = onVerifyNationalId,
        onBecomeOrganizer = onBecomeOrganizer,
        onAddEmail = onEditProfile,
        onAddPhone = onEditProfile,
        onLinkAccounts = onEditProfile,
        onEditProfile = onEditProfile,
        onInterests = onInterests,
        onNotifications = onNotificationSettings,
        onPaymentMethods = onPaymentMethods,
        onInviteFriends = { /* TODO: share intent */ },
        onRateUs = { /* TODO: launch Play Store rating */ },
        onSocialClick = { /* TODO: open brand URL */ },
        onHelpCenter = onHelpSupport,
        onContactSupport = onContactSupport,
        onTermsPrivacy = onTermsPrivacy,
        onSignOut = {
            viewModel.signOut()
            onSignOut()
        }
    )
}

private fun User?.toHeaderData(): ProfileHeaderData {
    val u = this
    return ProfileHeaderData(
        fullName = u?.fullName ?: "Guest",
        roleLabel = u?.currentActiveRole.toLabel(),
        avatarUrl = u?.profileImageUrl,
        email = u?.email,
        phoneNumber = u?.phoneNumber,
        versionText = BuildConfig.VERSION_NAME
    )
}

private fun UserRole?.toLabel(): String = when (this) {
    UserRole.ATTENDEE -> "Attendee"
    UserRole.ORGANIZER -> "Organizer"
    null -> "Guest"
}
