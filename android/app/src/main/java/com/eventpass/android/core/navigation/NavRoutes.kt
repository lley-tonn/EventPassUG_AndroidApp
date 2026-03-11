package com.eventpass.android.core.navigation

/**
 * Navigation route definitions for the app.
 * Mirrors the iOS navigation structure.
 */
sealed class NavRoutes(val route: String) {

    // Onboarding & Auth
    object Onboarding : NavRoutes("onboarding")
    object AuthChoice : NavRoutes("auth_choice")
    object PhoneVerification : NavRoutes("phone_verification/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "phone_verification/$phoneNumber"
    }
    object OnboardingFlow : NavRoutes("onboarding_flow")
    object Permissions : NavRoutes("permissions")

    // Main Tab Navigation
    object MainTabs : NavRoutes("main_tabs")

    // Attendee Routes
    object AttendeeHome : NavRoutes("attendee_home")
    object EventDetails : NavRoutes("event_details/{eventId}") {
        fun createRoute(eventId: String) = "event_details/$eventId"
    }
    object Search : NavRoutes("search")
    object FavoriteEvents : NavRoutes("favorite_events")
    object Tickets : NavRoutes("tickets")
    object TicketDetail : NavRoutes("ticket_detail/{ticketId}") {
        fun createRoute(ticketId: String) = "ticket_detail/$ticketId"
    }
    object TicketPurchase : NavRoutes("ticket_purchase/{eventId}") {
        fun createRoute(eventId: String) = "ticket_purchase/$eventId"
    }
    object PaymentConfirmation : NavRoutes("payment_confirmation/{eventId}/{ticketTypeId}/{quantity}") {
        fun createRoute(eventId: String, ticketTypeId: String, quantity: Int) =
            "payment_confirmation/$eventId/$ticketTypeId/$quantity"
    }
    object TicketSuccess : NavRoutes("ticket_success/{ticketId}") {
        fun createRoute(ticketId: String) = "ticket_success/$ticketId"
    }

    // Organizer Routes
    object OrganizerHome : NavRoutes("organizer_home")
    object OrganizerDashboard : NavRoutes("organizer_dashboard")
    object CreateEvent : NavRoutes("create_event")
    object EditEvent : NavRoutes("edit_event/{eventId}") {
        fun createRoute(eventId: String) = "edit_event/$eventId"
    }
    object ManageEventTickets : NavRoutes("manage_event_tickets/{eventId}") {
        fun createRoute(eventId: String) = "manage_event_tickets/$eventId"
    }
    object EventAnalytics : NavRoutes("event_analytics/{eventId}") {
        fun createRoute(eventId: String) = "event_analytics/$eventId"
    }
    object OrganizerAnalyticsDashboard : NavRoutes("organizer_analytics_dashboard")
    object QRScanner : NavRoutes("qr_scanner/{eventId}") {
        fun createRoute(eventId: String) = "qr_scanner/$eventId"
    }
    object BecomeOrganizer : NavRoutes("become_organizer")

    // Scanner Routes
    object PairScanner : NavRoutes("pair_scanner/{eventId}") {
        fun createRoute(eventId: String) = "pair_scanner/$eventId"
    }
    object ManageScannerDevices : NavRoutes("manage_scanner_devices/{eventId}") {
        fun createRoute(eventId: String) = "manage_scanner_devices/$eventId"
    }

    // Common Routes (Profile, Settings)
    object Profile : NavRoutes("profile")
    object EditProfile : NavRoutes("edit_profile")
    object NotificationSettings : NavRoutes("notification_settings")
    object Notifications : NavRoutes("notifications")
    object PaymentMethods : NavRoutes("payment_methods")
    object NationalIDVerification : NavRoutes("national_id_verification")
    object SupportCenter : NavRoutes("support_center")
    object HelpCenter : NavRoutes("help_center")
    object PrivacyPolicy : NavRoutes("privacy_policy")
    object TermsOfUse : NavRoutes("terms_of_use")

    // Refund Routes
    object RefundRequest : NavRoutes("refund_request/{ticketId}") {
        fun createRoute(ticketId: String) = "refund_request/$ticketId"
    }

    // Cancellation Routes
    object EventCancellation : NavRoutes("event_cancellation/{eventId}") {
        fun createRoute(eventId: String) = "event_cancellation/$eventId"
    }
}

/**
 * Bottom navigation tabs (role-aware)
 */
enum class BottomNavTab(
    val route: String,
    val label: String
) {
    // Attendee tabs
    HOME("attendee_home", "Home"),
    SEARCH("search", "Search"),
    TICKETS("tickets", "Tickets"),
    PROFILE("profile", "Profile"),

    // Organizer tabs (when in organizer mode)
    ORGANIZER_HOME("organizer_home", "Home"),
    EVENTS("organizer_dashboard", "Events"),
    ANALYTICS("organizer_analytics_dashboard", "Analytics"),
    ORGANIZER_PROFILE("profile", "Profile")
}
