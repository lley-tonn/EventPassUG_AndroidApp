package com.eventpass.android.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
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
import com.eventpass.android.features.attendee.search.SearchRoute
import com.eventpass.android.features.attendee.tickets.MyTicketsScreen
import com.eventpass.android.features.attendee.tickets.TicketDetailScreen
import com.eventpass.android.features.auth.AuthViewModel
import com.eventpass.android.features.common.profile.AddPhoneRoute
import com.eventpass.android.features.common.profile.ChangeEmailRoute
import com.eventpass.android.features.common.profile.EditProfileRoute
import com.eventpass.android.features.common.profile.EmailVerificationRoute
import com.eventpass.android.features.common.profile.NationalIdVerificationRoute
import com.eventpass.android.features.common.profile.ProfileScreen
import com.eventpass.android.features.common.profile.ProfileViewModel
import com.eventpass.android.features.common.profile.VerifyPhoneRoute
import com.eventpass.android.features.organizer.BecomeOrganizerContactScreen
import com.eventpass.android.features.organizer.BecomeOrganizerIdentityScreen
import com.eventpass.android.features.organizer.BecomeOrganizerPayoutScreen
import com.eventpass.android.features.organizer.BecomeOrganizerScreen
import com.eventpass.android.features.organizer.BecomeOrganizerTermsScreen
import com.eventpass.android.features.organizer.CreateEventFlow
import com.eventpass.android.features.organizer.OrganizerDashboardScreen
import com.eventpass.android.features.organizer.OrganizerHomeRoute
import com.eventpass.android.features.organizer.ScanTicketRoute
import com.eventpass.android.features.organizer.ScannerDevicesRoute
import com.eventpass.android.domain.models.UserRole
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
        composable(NavRoutes.TicketDetail.route) {
            TicketDetailScreen(onDone = { navController.popBackStack() })
        }

        // Profile
        composable(NavRoutes.Profile.route) {
            ProfileScreen()
        }

        // Become an Organizer — Step 1 of 5: Profile Completion
        composable(NavRoutes.BecomeOrganizer.route) {
            BecomeOrganizerScreen(
                onCancel = { navController.popBackStack() },
                onVerifyEmail = { navController.navigate(NavRoutes.EmailVerification.route) },
                onVerifyPhone = { navController.navigate(NavRoutes.VerifyPhone.route) },
                onAddPhoto = { navController.navigate(NavRoutes.EditProfile.route) },
                onContinue = { navController.navigate(NavRoutes.BecomeOrganizerIdentity.route) }
            )
        }

        // Become an Organizer — Step 2 of 5: Identity Verification
        composable(NavRoutes.BecomeOrganizerIdentity.route) {
            BecomeOrganizerIdentityScreen(
                onCancel = {
                    navController.popBackStack(NavRoutes.BecomeOrganizer.route, inclusive = true)
                },
                onChooseDocument = { navController.navigate(NavRoutes.NationalIDVerification.route) },
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(NavRoutes.BecomeOrganizerContact.route) }
            )
        }

        // Become an Organizer — Step 3 of 5: Contact Information
        composable(NavRoutes.BecomeOrganizerContact.route) {
            BecomeOrganizerContactScreen(
                onCancel = {
                    navController.popBackStack(NavRoutes.BecomeOrganizer.route, inclusive = true)
                },
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(NavRoutes.BecomeOrganizerPayout.route) }
            )
        }

        // Become an Organizer — Step 4 of 5: Payout Setup
        composable(NavRoutes.BecomeOrganizerPayout.route) {
            BecomeOrganizerPayoutScreen(
                onCancel = {
                    navController.popBackStack(NavRoutes.BecomeOrganizer.route, inclusive = true)
                },
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(NavRoutes.BecomeOrganizerTerms.route) }
            )
        }

        // Become an Organizer — Step 5 of 5: Terms Agreement
        composable(NavRoutes.BecomeOrganizerTerms.route) {
            BecomeOrganizerTermsScreen(
                onCancel = {
                    navController.popBackStack(NavRoutes.BecomeOrganizer.route, inclusive = true)
                },
                onBack = { navController.popBackStack() },
                onComplete = {
                    // TODO: submit organizer application + switch to organizer role
                    navController.popBackStack(NavRoutes.BecomeOrganizer.route, inclusive = true)
                }
            )
        }

        // National ID / Passport verification (from profile + organizer identity step)
        composable(NavRoutes.NationalIDVerification.route) {
            NationalIdVerificationRoute(
                onCancel = { navController.popBackStack() },
                onSubmitted = { navController.popBackStack() }
            )
        }

        // Edit Profile
        composable(NavRoutes.EditProfile.route) {
            EditProfileRoute(
                onBack = { navController.popBackStack() },
                onChangePhoto = { /* TODO: photo picker */ },
                onChangeEmail = { navController.navigate(NavRoutes.ChangeEmail.route) },
                onAddPhone = { navController.navigate(NavRoutes.AddPhone.route) }
            )
        }

        // Change Email
        composable(NavRoutes.ChangeEmail.route) {
            ChangeEmailRoute(
                onCancel = { navController.popBackStack() },
                onUpdated = { navController.popBackStack() }
            )
        }

        // Add Phone Number
        composable(NavRoutes.AddPhone.route) {
            AddPhoneRoute(
                onCancel = { navController.popBackStack() },
                onAdded = {
                    navController.popBackStack()
                    navController.navigate(NavRoutes.VerifyPhone.route)
                }
            )
        }

        // Email Verification
        composable(NavRoutes.EmailVerification.route) {
            EmailVerificationRoute(
                onDone = { navController.popBackStack() }
            )
        }

        // Verify Phone Number
        composable(NavRoutes.VerifyPhone.route) {
            VerifyPhoneRoute(
                onCancel = { navController.popBackStack() },
                onVerified = { navController.popBackStack() }
            )
        }

        // Create Event (3-step wizard)
        composable(NavRoutes.CreateEvent.route) {
            CreateEventFlow(
                onClose = { navController.popBackStack() },
                onPublished = { navController.popBackStack() }
            )
        }

        // Search
        composable(NavRoutes.Search.route) {
            SearchRoute(
                onClose = { navController.popBackStack() },
                onResultClick = { eventId ->
                    navController.navigate(NavRoutes.EventDetails.createRoute(eventId))
                }
            )
        }

        // Scanner Devices (event picker)
        composable(NavRoutes.ScannerDevices.route) {
            ScannerDevicesRoute(
                onBack = { navController.popBackStack() },
                onEventClick = { /* TODO: per-event scanner management */ }
            )
        }

        // Scan Ticket (QR camera)
        composable(NavRoutes.ScanTicket.route) {
            ScanTicketRoute(onCancel = { navController.popBackStack() })
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
    rootNavController: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val tabNavController = rememberNavController()

    val user by profileViewModel.currentUser.collectAsState()
    val isOrganizer = user?.currentActiveRole == UserRole.ORGANIZER

    // Middle tab is role-aware: Dashboard for organizers, Tickets for attendees.
    val middleItem = if (isOrganizer) {
        BottomNavItem(
            route = "tab_dashboard",
            label = "Dashboard",
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart
        )
    } else {
        BottomNavItem(
            route = "tab_tickets",
            label = "Tickets",
            selectedIcon = Icons.Filled.ConfirmationNumber,
            unselectedIcon = Icons.Outlined.ConfirmationNumber
        )
    }

    val bottomNavItems = listOf(
        BottomNavItem(
            route = "tab_home",
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        middleItem,
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
                if (isOrganizer) {
                    OrganizerHomeRoute(
                        greeting = "Good evening, ${user?.firstName ?: "there"}!",
                        onCreateEvent = { rootNavController.navigate(NavRoutes.CreateEvent.route) },
                        onSearch = { rootNavController.navigate(NavRoutes.Search.route) },
                        onNotifications = { /* TODO: notifications */ },
                        onEventClick = { eventId ->
                            rootNavController.navigate(NavRoutes.EventDetails.createRoute(eventId))
                        }
                    )
                } else {
                    AttendeeHomeScreen(
                        onEventClick = { eventId ->
                            rootNavController.navigate(NavRoutes.EventDetails.createRoute(eventId))
                        }
                    )
                }
            }

            composable("tab_tickets") {
                MyTicketsScreen(
                    onTicketClick = { ticketId ->
                        rootNavController.navigate(NavRoutes.TicketDetail.createRoute(ticketId))
                    }
                )
            }

            composable("tab_dashboard") {
                OrganizerDashboardScreen(
                    onCreateEvent = { rootNavController.navigate(NavRoutes.CreateEvent.route) },
                    onScanTickets = { rootNavController.navigate(NavRoutes.ScanTicket.route) },
                    onManageScanners = { rootNavController.navigate(NavRoutes.ScannerDevices.route) }
                    // TODO: onWithdraw / onViewInsights / onMore
                )
            }

            composable("tab_profile") {
                ProfileScreen(
                    onEditProfile = {
                        rootNavController.navigate(NavRoutes.EditProfile.route)
                    },
                    onBecomeOrganizer = {
                        rootNavController.navigate(NavRoutes.BecomeOrganizer.route)
                    },
                    onVerifyNationalId = {
                        rootNavController.navigate(NavRoutes.NationalIDVerification.route)
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
