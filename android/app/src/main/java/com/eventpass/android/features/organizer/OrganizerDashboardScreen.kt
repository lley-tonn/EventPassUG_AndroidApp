package com.eventpass.android.features.organizer

import androidx.compose.runtime.Composable
import com.eventpass.feature.organizer.dashboard.DashboardData
import com.eventpass.feature.organizer.dashboard.DashboardScreen

/**
 * :app-side wrapper for the Organizer Dashboard. Uses placeholder metrics
 * ([DashboardData.sample]) until the analytics repository is wired in, and
 * forwards the quick actions to navigation.
 */
@Composable
fun OrganizerDashboardScreen(
    onCreateEvent: () -> Unit = {},
    onScanTickets: () -> Unit = {},
    onManageScanners: () -> Unit = {},
    onWithdraw: () -> Unit = {},
    onViewInsights: () -> Unit = {},
    onMore: () -> Unit = {}
) {
    DashboardScreen(
        data = DashboardData.sample,
        onMore = onMore,
        onViewInsights = onViewInsights,
        onWithdraw = onWithdraw,
        onCreateEvent = onCreateEvent,
        onScanTickets = onScanTickets,
        onManageScanners = onManageScanners
    )
}
