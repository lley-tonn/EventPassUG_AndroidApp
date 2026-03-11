package com.eventpass.android.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eventpass.android.features.auth.AuthViewModel
import com.eventpass.android.features.onboarding.OnboardingScreen

/**
 * Main navigation host for the app.
 * Handles navigation between all screens based on auth state.
 */
@Composable
fun EventPassNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val hasCompletedOnboarding by authViewModel.hasCompletedOnboarding.collectAsState()

    // Determine start destination based on auth state
    val startDestination = when {
        !hasCompletedOnboarding -> NavRoutes.Onboarding.route
        !authState.isAuthenticated -> NavRoutes.AuthChoice.route
        else -> NavRoutes.MainTabs.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    authViewModel.completeOnboarding()
                    navController.navigate(NavRoutes.AuthChoice.route) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Auth Choice
        composable(NavRoutes.AuthChoice.route) {
            // TODO: AuthChoiceScreen
            // Placeholder - will be implemented when migrating Auth feature
        }

        // Main Tabs
        composable(NavRoutes.MainTabs.route) {
            MainTabsScreen(
                rootNavController = navController
            )
        }

        // Event Details
        composable(NavRoutes.EventDetails.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            // TODO: EventDetailsScreen(eventId = eventId)
        }

        // Ticket Purchase
        composable(NavRoutes.TicketPurchase.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            // TODO: TicketPurchaseScreen(eventId = eventId)
        }

        // Ticket Detail
        composable(NavRoutes.TicketDetail.route) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: return@composable
            // TODO: TicketDetailScreen(ticketId = ticketId)
        }

        // Profile
        composable(NavRoutes.Profile.route) {
            // TODO: ProfileScreen()
        }

        // Edit Profile
        composable(NavRoutes.EditProfile.route) {
            // TODO: EditProfileScreen()
        }

        // Add more routes as features are migrated...
    }
}

/**
 * Main tabs screen with bottom navigation.
 * Role-aware: shows different tabs for Attendee vs Organizer.
 */
@Composable
fun MainTabsScreen(
    rootNavController: NavHostController
) {
    // TODO: Implement with BottomNavigation
    // Will include role switching logic like iOS MainTabView
}
