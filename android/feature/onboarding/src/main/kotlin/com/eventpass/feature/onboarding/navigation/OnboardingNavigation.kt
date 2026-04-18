package com.eventpass.feature.onboarding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.eventpass.feature.onboarding.OnboardingViewModel
import com.eventpass.feature.onboarding.model.OnboardingState
import com.eventpass.feature.onboarding.screens.CompletionScreen
import com.eventpass.feature.onboarding.screens.InterestsScreen
import com.eventpass.feature.onboarding.screens.NotificationsScreen
import com.eventpass.feature.onboarding.screens.PersonalInfoScreen
import com.eventpass.feature.onboarding.screens.RoleScreen
import com.eventpass.feature.onboarding.screens.WelcomeScreen

/** Route constants — exposed so :app can deep-link / pop-up-to onboarding. */
object OnboardingRoutes {
    const val GRAPH = "onboarding_graph"
    const val WELCOME = "onboarding/welcome"
    const val ROLE = "onboarding/role"
    const val PERSONAL = "onboarding/personal"
    const val INTERESTS = "onboarding/interests"
    const val NOTIFICATIONS = "onboarding/notifications"
    const val COMPLETION = "onboarding/completion"
}

/**
 * Entry point for the onboarding flow. Register this from :app's NavHost
 * as `onboardingGraph(navController) { state -> /* persist + route to home */ }`.
 *
 * The ViewModel is scoped to the [OnboardingRoutes.GRAPH] back-stack entry so all
 * six screens share the same instance.
 */
fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    onFinished: (OnboardingState) -> Unit
) {
    navigation(
        route = OnboardingRoutes.GRAPH,
        startDestination = OnboardingRoutes.WELCOME
    ) {
        composable(OnboardingRoutes.WELCOME) {
            WelcomeScreen(onContinue = { navController.navigate(OnboardingRoutes.ROLE) })
        }
        composable(OnboardingRoutes.ROLE) { entry ->
            val vm = entry.graphViewModel(navController)
            val state by vm.state.collectAsStateWithLifecycle()
            RoleScreen(
                selected = state.role,
                onSelect = vm::setRole,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(OnboardingRoutes.PERSONAL) }
            )
        }
        composable(OnboardingRoutes.PERSONAL) { entry ->
            val vm = entry.graphViewModel(navController)
            val state by vm.state.collectAsStateWithLifecycle()
            PersonalInfoScreen(
                fullName = state.fullName,
                dateOfBirth = state.dateOfBirth,
                onFullNameChange = vm::setFullName,
                onDateChange = vm::setDateOfBirth,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(OnboardingRoutes.INTERESTS) }
            )
        }
        composable(OnboardingRoutes.INTERESTS) { entry ->
            val vm = entry.graphViewModel(navController)
            val state by vm.state.collectAsStateWithLifecycle()
            InterestsScreen(
                selected = state.interests,
                onToggle = vm::toggleInterest,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(OnboardingRoutes.NOTIFICATIONS) }
            )
        }
        composable(OnboardingRoutes.NOTIFICATIONS) { entry ->
            val vm = entry.graphViewModel(navController)
            val state by vm.state.collectAsStateWithLifecycle()
            NotificationsScreen(
                enabled = state.notificationsEnabled,
                onEnabledChange = vm::setNotifications,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(OnboardingRoutes.COMPLETION) }
            )
        }
        composable(OnboardingRoutes.COMPLETION) { entry ->
            val vm = entry.graphViewModel(navController)
            val state by vm.state.collectAsStateWithLifecycle()
            CompletionScreen(
                role = state.role,
                fullName = state.fullName,
                interestCount = state.interests.size,
                notificationsEnabled = state.notificationsEnabled,
                onBack = { navController.popBackStack() },
                onFinish = { onFinished(state) }
            )
        }
    }
}

/** Grab the graph-scoped [OnboardingViewModel] from the parent back-stack entry. */
@Composable
private fun NavBackStackEntry.graphViewModel(
    navController: NavHostController
): OnboardingViewModel {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(OnboardingRoutes.GRAPH)
    }
    return hiltViewModel(parentEntry)
}
