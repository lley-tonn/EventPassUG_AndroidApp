package com.eventpass.android.features.common.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventpass.feature.profile.AddPhoneScreen
import com.eventpass.feature.profile.ChangeEmailScreen
import com.eventpass.feature.profile.EditProfileScreen
import com.eventpass.feature.profile.EmailVerificationScreen
import com.eventpass.feature.profile.IdDocumentType
import com.eventpass.feature.profile.NationalIdVerificationScreen
import com.eventpass.feature.profile.NationalIdVerificationState
import com.eventpass.feature.profile.PhoneVerificationScreen

/**
 * :app-side wrappers that bind the stateless `:feature:profile` sheets to the
 * signed-in user and to navigation. Field edits are kept as local UI state;
 * persistence is left as TODO until the profile repository lands.
 */

@Composable
fun EditProfileRoute(
    onBack: () -> Unit,
    onChangePhoto: () -> Unit,
    onChangeEmail: () -> Unit,
    onAddPhone: () -> Unit,
    viewModel: ProfileContactViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    var firstName by rememberSaveable(user?.firstName) { mutableStateOf(user?.firstName.orEmpty()) }
    var lastName by rememberSaveable(user?.lastName) { mutableStateOf(user?.lastName.orEmpty()) }

    val changed = firstName != user?.firstName.orEmpty() || lastName != user?.lastName.orEmpty()
    val canSave = changed && firstName.isNotBlank() && lastName.isNotBlank()

    EditProfileScreen(
        firstName = firstName,
        lastName = lastName,
        email = user?.email,
        isEmailVerified = user?.isEmailVerified == true,
        phoneNumber = user?.phoneNumber,
        canSave = canSave,
        onFirstNameChange = { firstName = it },
        onLastNameChange = { lastName = it },
        onChangePhoto = onChangePhoto,
        onChangeEmail = onChangeEmail,
        onAddPhone = onAddPhone,
        onSave = {
            // TODO: persist name changes via profile repository
            onBack()
        }
    )
}

@Composable
fun ChangeEmailRoute(
    onCancel: () -> Unit,
    onUpdated: () -> Unit,
    viewModel: ProfileContactViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    var newEmail by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val canUpdate = newEmail.isNotBlank() && newEmail.contains("@") && confirmPassword.isNotBlank()

    ChangeEmailScreen(
        currentEmail = user?.email ?: "No email set",
        newEmail = newEmail,
        confirmPassword = confirmPassword,
        canUpdate = canUpdate,
        onNewEmailChange = { newEmail = it },
        onConfirmPasswordChange = { confirmPassword = it },
        onCancel = onCancel,
        onUpdate = {
            viewModel.changeEmail(newEmail)
            onUpdated()
        }
    )
}

@Composable
fun AddPhoneRoute(
    onCancel: () -> Unit,
    onAdded: () -> Unit,
    viewModel: ProfileContactViewModel = hiltViewModel()
) {
    var phone by rememberSaveable { mutableStateOf("") }
    val canAdd = phone.filter { it.isDigit() }.length >= 9

    AddPhoneScreen(
        phoneNumber = phone,
        canAdd = canAdd,
        onPhoneChange = { phone = it },
        onCancel = onCancel,
        onAdd = {
            // Register the phone (unverified), then route into SMS verification.
            viewModel.addPhone(phone)
            onAdded()
        }
    )
}

@Composable
fun EmailVerificationRoute(
    onDone: () -> Unit,
    viewModel: ProfileContactViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val isVerified = user?.isEmailVerified == true

    EmailVerificationScreen(
        email = user?.email ?: "your email",
        onDone = onDone,
        onSendVerification = {
            // Mock: sending the link auto-confirms it.
            viewModel.verifyEmail()
        },
        emailVerified = isVerified
    )
}

@Composable
fun NationalIdVerificationRoute(
    onCancel: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: ProfileContactViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    var documentType by rememberSaveable { mutableStateOf(IdDocumentType.NATIONAL_ID) }
    var fullName by rememberSaveable(user?.fullName) { mutableStateOf(user?.fullName.orEmpty()) }
    var documentNumber by rememberSaveable { mutableStateOf("") }
    var frontCaptured by rememberSaveable { mutableStateOf(false) }
    var backCaptured by rememberSaveable { mutableStateOf(false) }

    NationalIdVerificationScreen(
        state = NationalIdVerificationState(
            documentType = documentType,
            fullName = fullName,
            documentNumber = documentNumber,
            frontCaptured = frontCaptured,
            backCaptured = backCaptured
        ),
        onDocumentTypeChange = { documentType = it },
        onFullNameChange = { fullName = it },
        onDocumentNumberChange = { documentNumber = it },
        onCaptureFront = {
            // TODO: launch camera capture for document front
            frontCaptured = true
        },
        onCaptureBack = {
            // TODO: launch camera capture for document back
            backCaptured = true
        },
        onCancel = onCancel,
        onSubmit = {
            // Mock: submitting the document marks the user identity-verified.
            viewModel.submitIdentity(documentNumber)
            onSubmitted()
        }
    )
}

@Composable
fun VerifyPhoneRoute(
    onCancel: () -> Unit,
    onVerified: () -> Unit,
    viewModel: ProfileContactViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    var codeSent by rememberSaveable { mutableStateOf(false) }
    var code by rememberSaveable { mutableStateOf("") }

    PhoneVerificationScreen(
        phoneNumber = user?.phoneNumber ?: "your number",
        codeSent = codeSent,
        code = code,
        onCodeChange = { code = it },
        onCancel = onCancel,
        onSendCode = {
            // Mock: pretend the SMS was sent.
            codeSent = true
        },
        onVerifyCode = {
            // Mock: accept any 6-digit code and mark the phone verified.
            viewModel.verifyPhone()
            onVerified()
        },
        onResendCode = {
            // Mock: re-send is a no-op.
        }
    )
}
