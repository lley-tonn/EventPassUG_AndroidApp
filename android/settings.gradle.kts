pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EventPassUG"

include(":app")

// Core modules
include(":core:common")
include(":core:design")
include(":core:ui")
include(":core:domain")
include(":core:data")

// Feature modules (empty shells for now; features migrate in here as they're rebuilt)
include(":feature:onboarding")
include(":feature:auth")
include(":feature:attendee")
include(":feature:organizer")
include(":feature:profile")
include(":feature:become-organizer")
include(":feature:notifications")
