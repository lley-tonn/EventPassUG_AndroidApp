package com.eventpass.android.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eventpass.android.features.attendee.home.AttendeeHomeScreen
import com.eventpass.android.features.attendee.tickets.MyTicketsScreen
import com.eventpass.android.features.auth.AuthViewModel
import com.eventpass.android.features.common.profile.ProfileScreen
import com.eventpass.feature.auth.navigation.AuthRoutes
import com.eventpass.feature.auth.screens.AuthChoiceScreen
import com.eventpass.feature.auth.screens.LoginScreen
import com.eventpass.feature.auth.screens.SignUpScreen
import com.eventpass.feature.onboarding.navigation.OnboardingRoutes
import com.eventpass.feature.onboarding.navigation.onboardingGraph

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
        !hasCompletedOnboarding -> OnboardingRoutes.GRAPH
        !authState.isAuthenticated -> AuthRoutes.CHOICE
        else -> NavRoutes.MainTabs.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding — 6-step flow in its own nested graph (see :feature:onboarding)
        onboardingGraph(navController) { _ ->
            // TODO(phase-2): persist onboarding answers to the real preferences repo
            authViewModel.completeOnboarding()
            navController.navigate(AuthRoutes.CHOICE) {
                popUpTo(OnboardingRoutes.GRAPH) { inclusive = true }
            }
        }

        // Auth Choice
        composable(AuthRoutes.CHOICE) {
            AuthChoiceScreen(
                onSignIn = { navController.navigate(AuthRoutes.LOGIN) },
                onHostEvents = {
                    // TODO(phase-2): route to organizer onboarding / verification
                    navController.navigate(AuthRoutes.SIGN_UP)
                },
                onBrowseAsGuest = {
                    navController.navigate(NavRoutes.MainTabs.route) {
                        popUpTo(AuthRoutes.CHOICE) { inclusive = true }
                    }
                }
            )
        }

        // Login
        composable(AuthRoutes.LOGIN) {
            var email by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }

            LaunchedEffect(authState.isAuthenticated) {
                if (authState.isAuthenticated) {
                    navController.navigate(NavRoutes.MainTabs.route) {
                        popUpTo(AuthRoutes.CHOICE) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                email = email,
                password = password,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onSubmit = { authViewModel.signInWithEmail(email, password) },
                onBack = { navController.popBackStack() },
                onForgotPassword = { /* TODO(phase-2) */ },
                isLoading = authState.isLoading,
                errorText = authState.error
            )
        }

        // Sign Up
        composable(AuthRoutes.SIGN_UP) {
            var fullName by rememberSaveable { mutableStateOf("") }
            var email by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var confirmPassword by rememberSaveable { mutableStateOf("") }

            LaunchedEffect(authState.isAuthenticated) {
                if (authState.isAuthenticated) {
                    navController.navigate(NavRoutes.MainTabs.route) {
                        popUpTo(AuthRoutes.CHOICE) { inclusive = true }
                    }
                }
            }

            SignUpScreen(
                fullName = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                onFullNameChange = { fullName = it },
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onConfirmPasswordChange = { confirmPassword = it },
                onSubmit = { authViewModel.signUpWithEmail(email, password, fullName) },
                onBack = { navController.popBackStack() },
                isLoading = authState.isLoading,
                errorText = authState.error
            )
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
            ProfileScreen()
        }

        // Edit Profile
        composable(NavRoutes.EditProfile.route) {
            // TODO: EditProfileScreen()
        }

        // Add more routes as features are migrated...
    }
}

/**
 * Bottom navigation tab item data.
 */
private data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * Main tabs screen with bottom navigation.
 * Role-aware: shows different tabs for Attendee vs Organizer.
 */
@Composable
fun MainTabsScreen(
    rootNavController: NavHostController
) {
    val tabNavController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(
            route = "tab_home",
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = "tab_tickets",
            label = "Tickets",
            selectedIcon = Icons.Filled.ConfirmationNumber,
            unselectedIcon = Icons.Outlined.ConfirmationNumber
        ),
        BottomNavItem(
            route = "tab_profile",
            label = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = {
                            tabNavController.navigate(item.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = "tab_home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("tab_home") {
                AttendeeHomeScreen(
                    onEventClick = { eventId ->
                        rootNavController.navigate(NavRoutes.EventDetails.createRoute(eventId))
                    }
                )
            }

            composable("tab_tickets") {
                MyTicketsScreen(
                    onTicketClick = { ticketId ->
                        rootNavController.navigate(NavRoutes.TicketDetail.createRoute(ticketId))
                    }
                )
            }

            composable("tab_profile") {
                ProfileScreen(
                    onEditProfile = {
                        rootNavController.navigate(NavRoutes.EditProfile.route)
                    },
                    onSignOut = {
                        // Navigate back to auth choice
                        rootNavController.navigate(AuthRoutes.CHOICE) {
                            popUpTo(NavRoutes.MainTabs.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
