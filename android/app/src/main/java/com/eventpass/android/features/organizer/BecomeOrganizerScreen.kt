package com.eventpass.android.features.organizer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventpass.android.domain.models.User
import com.eventpass.feature.becomeorganizer.ContactInformationScreen
import com.eventpass.feature.becomeorganizer.ContactInformationState
import com.eventpass.feature.becomeorganizer.IdentityVerificationScreen
import com.eventpass.feature.becomeorganizer.PayoutMethod
import com.eventpass.feature.becomeorganizer.PayoutSetupScreen
import com.eventpass.feature.becomeorganizer.PayoutSetupState
import com.eventpass.feature.becomeorganizer.ProfileCompletionData
import com.eventpass.feature.becomeorganizer.ProfileCompletionScreen
import com.eventpass.feature.becomeorganizer.TermsAgreementScreen

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

/**
 * :app-side wrapper for Become-an-Organizer Step 2: Identity Verification.
 * Identity upload isn't wired to a backend yet, so verification is driven off
 * the domain `User.isVerified` flag.
 */
@Composable
fun BecomeOrganizerIdentityScreen(
    onCancel: () -> Unit,
    onChooseDocument: () -> Unit = {},
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    viewModel: BecomeOrganizerViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    IdentityVerificationScreen(
        identityVerified = user?.isVerified == true,
        onCancel = onCancel,
        onChooseDocument = onChooseDocument,
        onBack = onBack,
        onContinue = onContinue
    )
}

/**
 * :app-side wrapper for Become-an-Organizer Step 3: Contact Information.
 * Toggle + brand/social state is kept local; public email/phone are prefilled
 * from the account.
 */
@Composable
fun BecomeOrganizerContactScreen(
    onCancel: () -> Unit,
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    viewModel: BecomeOrganizerViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    var usePersonalEmail by rememberSaveable { mutableStateOf(true) }
    var usePersonalPhone by rememberSaveable { mutableStateOf(true) }
    var brandName by rememberSaveable { mutableStateOf("") }
    var website by rememberSaveable { mutableStateOf("") }
    var instagram by rememberSaveable { mutableStateOf("") }
    var twitter by rememberSaveable { mutableStateOf("") }
    var facebook by rememberSaveable { mutableStateOf("") }

    ContactInformationScreen(
        state = ContactInformationState(
            usePersonalEmail = usePersonalEmail,
            usePersonalPhone = usePersonalPhone,
            accountEmail = user?.email,
            accountPhone = user?.phoneNumber,
            brandName = brandName,
            website = website,
            instagram = instagram,
            twitter = twitter,
            facebook = facebook
        ),
        onToggleEmail = { usePersonalEmail = it },
        onTogglePhone = { usePersonalPhone = it },
        onBrandNameChange = { brandName = it },
        onWebsiteChange = { website = it },
        onInstagramChange = { instagram = it },
        onTwitterChange = { twitter = it },
        onFacebookChange = { facebook = it },
        onCancel = onCancel,
        onBack = onBack,
        onContinue = onContinue
    )
}

/**
 * :app-side wrapper for Become-an-Organizer Step 4: Payout Setup.
 */
@Composable
fun BecomeOrganizerPayoutScreen(
    onCancel: () -> Unit,
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    viewModel: BecomeOrganizerViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    var method by rememberSaveable { mutableStateOf(PayoutMethod.MTN) }
    var useAccountNumber by rememberSaveable { mutableStateOf(true) }
    var customNumber by rememberSaveable { mutableStateOf("") }
    var bankName by rememberSaveable { mutableStateOf("") }
    var bankAccountNumber by rememberSaveable { mutableStateOf("") }
    var bankAccountName by rememberSaveable { mutableStateOf("") }

    PayoutSetupScreen(
        state = PayoutSetupState(
            method = method,
            useAccountNumber = useAccountNumber,
            accountPhone = user?.phoneNumber,
            customNumber = customNumber,
            bankName = bankName,
            bankAccountNumber = bankAccountNumber,
            bankAccountName = bankAccountName
        ),
        onSelectMethod = { method = it },
        onToggleUseAccountNumber = { useAccountNumber = it },
        onCustomNumberChange = { customNumber = it },
        onBankNameChange = { bankName = it },
        onBankAccountNumberChange = { bankAccountNumber = it },
        onBankAccountNameChange = { bankAccountName = it },
        onCancel = onCancel,
        onBack = onBack,
        onContinue = onContinue
    )
}

/**
 * :app-side wrapper for Become-an-Organizer Step 5: Terms Agreement.
 */
@Composable
fun BecomeOrganizerTermsScreen(
    onCancel: () -> Unit,
    onBack: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    var agreed by rememberSaveable { mutableStateOf(false) }

    TermsAgreementScreen(
        agreed = agreed,
        onAgreedChange = { agreed = it },
        onCancel = onCancel,
        onBack = onBack,
        onComplete = onComplete
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
