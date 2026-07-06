package com.eventpass.feature.organizer.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.components.ProgressDial
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing

private val Purple = EventPassColors.OrganizerAccent

/**
 * Organizer Dashboard (design reference IMG_2796–IMG_2799).
 *
 * A long, read-only overview of the organizer's performance: health score,
 * revenue/overview tiles, marketing, operations, predictions, sales, earnings
 * (with withdraw), and quick actions. Stateless — [data] and the action
 * callbacks are supplied by the caller.
 */
@Composable
fun DashboardScreen(
    data: DashboardData,
    onMore: () -> Unit,
    onViewInsights: () -> Unit,
    onWithdraw: () -> Unit,
    onCreateEvent: () -> Unit,
    onScanTickets: () -> Unit,
    onManageScanners: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
            .statusBarsPadding()
    ) {
        TopBar(onMore = onMore)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.huge)
        ) {
            HealthCard(data)
            SectionSpacer()

            DashboardSectionHeader(Icons.Filled.ShowChart, EventPassColors.Ink, "Overview", trailing = {})
            OverviewGrid(data, onViewInsights)
            SectionSpacer()

            DashboardSectionHeader(Icons.Filled.Campaign, EventPassColors.Primary, "Marketing & Engagement")
            MarketingGrid(data)
            SectionSpacer()

            DashboardSectionHeader(Icons.Filled.Settings, EventPassColors.InkMuted, "Event Operations")
            OperationsGrid(data)
            SectionSpacer()

            DashboardSectionHeader(
                Icons.AutoMirrored.Filled.TrendingUp, Purple, "Insights & Predictions",
                trailing = { NextRangePill() }
            )
            InsightsSection(data)
            SectionSpacer()

            DashboardSectionHeader(Icons.Filled.CalendarMonth, EventPassColors.Info, "Your Events")
            if (data.hasEvents) {
                // TODO: render event list once events exist
            } else {
                EmptyEventsCard()
            }
            SectionSpacer()

            DashboardSectionHeader(Icons.Filled.ShowChart, EventPassColors.Success, "Sales Performance")
            SalesPerformanceCard(data)
            SectionSpacer()

            DashboardSectionHeader(Icons.Filled.MonetizationOn, EventPassColors.Primary, "Earnings")
            EarningsCard(data, onWithdraw)
            SectionSpacer()

            DashboardSectionHeader(Icons.Filled.Bolt, Purple, "Quick Actions")
            QuickActions(onCreateEvent, onScanTickets, onManageScanners)
        }
    }
}

@Composable
private fun SectionSpacer() = Spacer(Modifier.height(Spacing.xxl))

// MARK: - Top bar

@Composable
private fun TopBar(onMore: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = EventPassColors.Ink,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(EventPassColors.White)
                .clickable(onClick = onMore),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = "More",
                tint = EventPassColors.Primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// MARK: - Health + revenue summary

@Composable
private fun HealthCard(data: DashboardData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProgressDial(
            progress = data.healthScorePercent / 100f,
            size = 84.dp,
            strokeWidth = 9.dp,
            valueText = "${data.healthScorePercent}%"
        )
        Spacer(Modifier.width(Spacing.md))
        Column {
            Text(
                text = "Health Score",
                style = MaterialTheme.typography.bodyLarge,
                color = EventPassColors.InkMuted
            )
            Text(
                text = data.healthLabel,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = EventPassColors.Error
            )
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = data.totalRevenueText,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = EventPassColors.Ink
            )
            Text(
                text = "Total Revenue",
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )
        }
    }
}

// MARK: - Overview

@Composable
private fun OverviewGrid(data: DashboardData, onViewInsights: () -> Unit) {
    DashboardColumn {
        StatRow {
            StatCard(
                icon = Icons.Filled.Payments,
                iconTint = EventPassColors.Success,
                label = "Revenue",
                value = data.revenueText,
                modifier = Modifier.weight(1f),
                trailing = { TrendBadge("↗ ${data.revenueTrendText}") }
            )
            StatCard(
                icon = Icons.Filled.ConfirmationNumber,
                iconTint = EventPassColors.Primary,
                label = "Tickets Sold",
                value = "${data.ticketsSold}",
                modifier = Modifier.weight(1f)
            )
        }
        StatRow {
            StatCard(
                icon = Icons.Filled.CalendarMonth,
                iconTint = EventPassColors.Info,
                label = "Active Events",
                value = "${data.activeEvents}",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Filled.Groups,
                iconTint = Purple,
                label = "Attendees",
                value = "${data.attendees}",
                caption = "View insights",
                modifier = Modifier.weight(1f),
                onClick = onViewInsights
            )
        }
    }
}

// MARK: - Marketing

@Composable
private fun MarketingGrid(data: DashboardData) {
    DashboardColumn {
        StatRow {
            StatCard(Icons.Filled.Visibility, EventPassColors.Info, "Impressions", "${data.impressions}", Modifier.weight(1f), minHeight = 84.dp)
            StatCard(Icons.Filled.Favorite, EventPassColors.Error, "Likes", "${data.likes}", Modifier.weight(1f), minHeight = 84.dp)
        }
        StatRow {
            StatCard(Icons.Filled.Share, EventPassColors.Success, "Shares", "${data.shares}", Modifier.weight(1f), minHeight = 84.dp)
            StatCard(Icons.AutoMirrored.Filled.TrendingUp, Purple, "Conversion", data.conversionText, Modifier.weight(1f), minHeight = 84.dp)
        }
        StatRow {
            StatCard(
                Icons.Filled.ShowChart, EventPassColors.Info, "Engagement Rate", data.engagementRateText,
                Modifier.weight(1f), background = EventPassColors.InfoSoft, minHeight = 84.dp
            )
            StatCard(
                Icons.Filled.PersonAdd, EventPassColors.Success, "Avg. per Event", "${data.avgPerEvent}",
                Modifier.weight(1f), background = EventPassColors.SuccessSoft, minHeight = 84.dp
            )
        }
    }
}

// MARK: - Operations

@Composable
private fun OperationsGrid(data: DashboardData) {
    DashboardColumn {
        StatRow {
            StatCard(Icons.Filled.CheckCircle, EventPassColors.Success, "Check-ins", "${data.checkIns}", Modifier.weight(1f), caption = data.checkInRateText, minHeight = 92.dp)
            StatCard(Icons.Filled.Schedule, EventPassColors.Primary, "Pending", "${data.pending}", Modifier.weight(1f), minHeight = 92.dp)
        }
        StatRow {
            StatCard(Icons.AutoMirrored.Filled.Chat, EventPassColors.Info, "Response Time", data.responseTimeText, Modifier.weight(1f), caption = "avg", minHeight = 92.dp)
            StatCard(Icons.Filled.Star, EventPassColors.Premium, "Satisfaction", data.satisfactionText, Modifier.weight(1f), caption = "out of 5", minHeight = 92.dp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            StatusPill("Scanner")
            StatusPill("Support")
            StatusPill("Payments")
        }
    }
}

// MARK: - Insights & Predictions

@Composable
private fun NextRangePill() {
    Box(
        modifier = Modifier
            .clip(Radii.Pill)
            .background(Purple.copy(alpha = 0.12f))
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        Text(
            text = "Next 30 days",
            style = MaterialTheme.typography.labelMedium,
            color = EventPassColors.InkMuted
        )
    }
}

@Composable
private fun InsightsSection(data: DashboardData) {
    DashboardColumn {
        StatRow {
            StatCard(
                Icons.AutoMirrored.Filled.TrendingUp, EventPassColors.Success, "Projected Revenue",
                data.projectedRevenueText, Modifier.weight(1f),
                valueColor = EventPassColors.Success, background = EventPassColors.SuccessSoft
            )
            StatCard(
                Icons.Filled.ConfirmationNumber, EventPassColors.Info, "Projected Sales",
                "${data.projectedSales}", Modifier.weight(1f),
                valueColor = EventPassColors.Info, background = EventPassColors.InfoSoft
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Radii.Card)
                .background(EventPassColors.White)
                .padding(Spacing.lg)
        ) {
            InsightRow(Icons.Filled.BarChart, "Growth Rate", "vs last month", data.growthRateText)
            Spacer(Modifier.height(Spacing.md))
            InsightRow(Icons.Filled.Groups, "Audience Growth", "new attendees this month", data.audienceGrowthText)
        }
    }
}

@Composable
private fun InsightRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = EventPassColors.Success, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = EventPassColors.Ink)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
        }
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = EventPassColors.Success)
    }
}

// MARK: - Empty events

@Composable
private fun EmptyEventsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .padding(vertical = Spacing.xxxl, horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.EventNote,
            contentDescription = null,
            tint = EventPassColors.InkSubtle,
            modifier = Modifier.size(44.dp)
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = "No Events Yet",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = EventPassColors.Ink
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = "Create your first event to start selling tickets",
            style = MaterialTheme.typography.bodyLarge,
            color = EventPassColors.InkMuted,
            textAlign = TextAlign.Center
        )
    }
}

// MARK: - Sales performance

@Composable
private fun SalesPerformanceCard(data: DashboardData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .padding(vertical = Spacing.xl),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SalesStat("${data.totalSold}", "Total Sold", Modifier.weight(1f))
        VerticalDivider()
        SalesStat(data.avgPriceText, "Avg Price", Modifier.weight(1f))
        VerticalDivider()
        SalesStat(data.capacityText, "Capacity", Modifier.weight(1f))
    }
}

@Composable
private fun SalesStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = EventPassColors.Ink)
        Spacer(Modifier.height(Spacing.xs))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .height(36.dp)
            .width(0.5.dp)
            .background(EventPassColors.DividerLight)
    )
}

// MARK: - Earnings

@Composable
private fun EarningsCard(data: DashboardData, onWithdraw: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .padding(Spacing.lg)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Available Balance", style = MaterialTheme.typography.bodyLarge, color = EventPassColors.InkMuted)
                Text(
                    data.availableBalanceText,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = EventPassColors.Primary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row {
                    Text("Net: ", style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
                    Text(data.netText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = EventPassColors.Success)
                }
                Row {
                    Text("Fees: ", style = MaterialTheme.typography.bodyMedium, color = EventPassColors.InkMuted)
                    Text(data.feesText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = EventPassColors.Primary)
                }
            }
        }
        Spacer(Modifier.height(Spacing.lg))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(Radii.Button)
                .background(EventPassColors.Primary)
                .clickable(onClick = onWithdraw),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ArrowDownward, contentDescription = null, tint = EventPassColors.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(Spacing.sm))
                Text("Withdraw Funds", style = MaterialTheme.typography.titleMedium, color = EventPassColors.White)
            }
        }
    }
}

// MARK: - Quick actions

@Composable
private fun QuickActions(onCreateEvent: () -> Unit, onScanTickets: () -> Unit, onManageScanners: () -> Unit) {
    DashboardColumn {
        QuickActionRow(
            icon = Icons.Filled.Add,
            iconTint = EventPassColors.Success,
            iconBackground = EventPassColors.SuccessSoft,
            title = "Create Event",
            subtitle = "Launch a new event",
            onClick = onCreateEvent
        )
        QuickActionRow(
            icon = Icons.Filled.QrCodeScanner,
            iconTint = EventPassColors.Info,
            iconBackground = EventPassColors.InfoSoft,
            title = "Scan Tickets",
            subtitle = "Validate attendee tickets",
            onClick = onScanTickets
        )
        QuickActionRow(
            icon = Icons.Filled.DevicesOther,
            iconTint = Purple,
            iconBackground = Purple.copy(alpha = 0.12f),
            title = "Manage Scanners",
            subtitle = "Authorize scanning devices",
            onClick = onManageScanners
        )
    }
}
