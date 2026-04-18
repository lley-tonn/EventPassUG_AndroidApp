package com.eventpass.feature.onboarding.model

import java.time.LocalDate

/**
 * All answers collected across the 6 onboarding steps.
 * The flow is a straight line — each step mutates one field and advances.
 */
data class OnboardingState(
    val role: Role? = null,
    val fullName: String = "",
    val dateOfBirth: LocalDate? = null,
    val interests: Set<InterestCategory> = emptySet(),
    val notificationsEnabled: Boolean = false
) {
    val canAdvanceFromRole: Boolean get() = role != null
    val canAdvanceFromPersonal: Boolean get() = fullName.isNotBlank() && dateOfBirth != null
    val canAdvanceFromInterests: Boolean get() = interests.isNotEmpty()
}
