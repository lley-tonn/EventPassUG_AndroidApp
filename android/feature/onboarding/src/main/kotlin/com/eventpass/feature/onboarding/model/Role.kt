package com.eventpass.feature.onboarding.model

/**
 * Primary role the user selects during onboarding. Can be changed later in settings.
 */
enum class Role(val label: String, val description: String) {
    Attendee(
        label = "Attendee",
        description = "Discover events, buy tickets, and enjoy experiences"
    ),
    Organizer(
        label = "Organizer",
        description = "Create events, sell tickets, and grow your audience"
    )
}
