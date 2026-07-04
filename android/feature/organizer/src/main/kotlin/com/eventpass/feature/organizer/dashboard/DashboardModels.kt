package com.eventpass.feature.organizer.dashboard

/**
 * Everything rendered on the organizer [DashboardScreen], kept as primitive,
 * pre-formatted values so the feature module stays free of domain types.
 */
data class DashboardData(
    // Health + revenue summary
    val healthScorePercent: Int,
    val healthLabel: String,
    val totalRevenueText: String,

    // Overview
    val revenueText: String,
    val revenueTrendText: String,
    val ticketsSold: Int,
    val activeEvents: Int,
    val attendees: Int,

    // Marketing & Engagement
    val impressions: Int,
    val likes: Int,
    val shares: Int,
    val conversionText: String,
    val engagementRateText: String,
    val avgPerEvent: Int,

    // Event Operations
    val checkIns: Int,
    val checkInRateText: String,
    val pending: Int,
    val responseTimeText: String,
    val satisfactionText: String,

    // Insights & Predictions
    val projectedRevenueText: String,
    val projectedSales: Int,
    val growthRateText: String,
    val audienceGrowthText: String,

    // Sales Performance
    val totalSold: Int,
    val avgPriceText: String,
    val capacityText: String,

    // Earnings
    val availableBalanceText: String,
    val netText: String,
    val feesText: String,

    val hasEvents: Boolean
) {
    companion object {
        /** Placeholder shown until the analytics repository is wired in. */
        val sample = DashboardData(
            healthScorePercent = 50,
            healthLabel = "Fair",
            totalRevenueText = "UGX 150K",
            revenueText = "UGX 150K",
            revenueTrendText = "+12%",
            ticketsSold = 0,
            activeEvents = 0,
            attendees = 0,
            impressions = 0,
            likes = 0,
            shares = 0,
            conversionText = "0.0%",
            engagementRateText = "0.0%",
            avgPerEvent = 0,
            checkIns = 0,
            checkInRateText = "0% rate",
            pending = 0,
            responseTimeText = "2.4h",
            satisfactionText = "4.2",
            projectedRevenueText = "UGX 172K",
            projectedSales = 0,
            growthRateText = "+12.5%",
            audienceGrowthText = "+0",
            totalSold = 0,
            avgPriceText = "UGX 0",
            capacityText = "0%",
            availableBalanceText = "UGX 150K",
            netText = "UGX 142K",
            feesText = "UGX 8K",
            hasEvents = false
        )
    }
}
