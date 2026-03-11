package com.eventpass.android.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Organizer profile model.
 * Migrated from iOS Domain/Models/OrganizerProfile.swift
 */
data class OrganizerProfile(
    // Public Contact Information
    val publicEmail: String = "",
    val publicPhone: String = "",

    // Brand Information (optional)
    val brandName: String? = null,
    val website: String? = null,
    val instagramHandle: String? = null,
    val twitterHandle: String? = null,
    val facebookPage: String? = null,

    // Payout Information
    val payoutMethod: PayoutMethod? = null,

    // Terms Agreement
    val agreedToTermsDate: LocalDateTime? = null,
    val termsVersion: String? = null,

    // Onboarding Progress
    val completedOnboardingSteps: Set<OrganizerOnboardingStep> = emptySet(),

    // Follower count
    val followerCount: Int = 0
) {
    /**
     * Check if onboarding is complete.
     */
    val isOnboardingComplete: Boolean
        get() = completedOnboardingSteps.containsAll(OrganizerOnboardingStep.entries)

    /**
     * Current onboarding step (first incomplete).
     */
    val currentOnboardingStep: OrganizerOnboardingStep?
        get() = OrganizerOnboardingStep.entries
            .sortedBy { it.stepNumber }
            .firstOrNull { it !in completedOnboardingSteps }

    /**
     * Onboarding progress percentage.
     */
    val onboardingProgress: Float
        get() = completedOnboardingSteps.size.toFloat() / OrganizerOnboardingStep.entries.size.toFloat()
}

/**
 * Payout method types.
 */
enum class PayoutMethodType(val value: String) {
    MTN_MOMO("mtn_momo"),
    AIRTEL_MONEY("airtel_money"),
    BANK_ACCOUNT("bank_account");

    val displayName: String
        get() = when (this) {
            MTN_MOMO -> "MTN Mobile Money"
            AIRTEL_MONEY -> "Airtel Money"
            BANK_ACCOUNT -> "Bank Account"
        }

    val iconName: String
        get() = when (this) {
            MTN_MOMO -> "phone"
            AIRTEL_MONEY -> "phone"
            BANK_ACCOUNT -> "account_balance"
        }
}

/**
 * Payout method model.
 */
data class PayoutMethod(
    val id: String = UUID.randomUUID().toString(),
    val type: PayoutMethodType,
    val phoneNumber: String? = null, // For mobile money
    val bankName: String? = null, // For bank account
    val accountNumber: String? = null, // For bank account
    val accountName: String? = null, // For bank account
    val isVerified: Boolean = false,
    val isDefault: Boolean = false
) {
    /**
     * Masked account number for display.
     */
    val maskedAccountNumber: String
        get() {
            val number = phoneNumber ?: accountNumber ?: return "****"
            return if (number.length > 4) {
                "****${number.takeLast(4)}"
            } else {
                "****"
            }
        }

    /**
     * Display name for the payout method.
     */
    val displayName: String
        get() = when (type) {
            PayoutMethodType.MTN_MOMO -> "MTN MoMo - $maskedAccountNumber"
            PayoutMethodType.AIRTEL_MONEY -> "Airtel Money - $maskedAccountNumber"
            PayoutMethodType.BANK_ACCOUNT -> "${bankName ?: "Bank"} - $maskedAccountNumber"
        }
}

/**
 * Organizer onboarding steps.
 */
enum class OrganizerOnboardingStep(val value: String, val stepNumber: Int) {
    PROFILE_COMPLETION("profile_completion", 1),
    IDENTITY_VERIFICATION("identity_verification", 2),
    CONTACT_INFORMATION("contact_information", 3),
    PAYOUT_SETUP("payout_setup", 4),
    TERMS_AGREEMENT("terms_agreement", 5);

    val displayName: String
        get() = when (this) {
            PROFILE_COMPLETION -> "Complete Profile"
            IDENTITY_VERIFICATION -> "Verify Identity"
            CONTACT_INFORMATION -> "Contact Information"
            PAYOUT_SETUP -> "Payout Setup"
            TERMS_AGREEMENT -> "Terms Agreement"
        }

    val iconName: String
        get() = when (this) {
            PROFILE_COMPLETION -> "person"
            IDENTITY_VERIFICATION -> "verified_user"
            CONTACT_INFORMATION -> "email"
            PAYOUT_SETUP -> "payments"
            TERMS_AGREEMENT -> "description"
        }

    val description: String
        get() = when (this) {
            PROFILE_COMPLETION -> "Set up your organizer profile and brand information"
            IDENTITY_VERIFICATION -> "Verify your identity with a government-issued ID"
            CONTACT_INFORMATION -> "Add public contact details for attendees"
            PAYOUT_SETUP -> "Set up your payout method to receive payments"
            TERMS_AGREEMENT -> "Review and accept the organizer terms of service"
        }
}

/**
 * Organizer verification status.
 */
enum class OrganizerVerificationStatus {
    PENDING,
    IN_REVIEW,
    APPROVED,
    REJECTED;

    val displayName: String
        get() = when (this) {
            PENDING -> "Pending"
            IN_REVIEW -> "In Review"
            APPROVED -> "Approved"
            REJECTED -> "Rejected"
        }

    val iconName: String
        get() = when (this) {
            PENDING -> "hourglass_empty"
            IN_REVIEW -> "pending"
            APPROVED -> "verified"
            REJECTED -> "cancel"
        }
}
