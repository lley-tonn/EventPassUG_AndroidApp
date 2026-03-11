package com.eventpass.android.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Comprehensive analytics model for organizer dashboard.
 * Migrated from iOS Domain/Models/OrganizerAnalytics.swift
 */
data class OrganizerAnalytics(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String,
    val eventTitle: String,
    val lastUpdated: LocalDateTime = LocalDateTime.now(),

    // Overview
    val revenue: Double,
    val ticketsSold: Int,
    val totalCapacity: Int,
    val attendanceRate: Double, // 0.0 - 1.0
    val capacityUsed: Double, // 0.0 - 1.0
    val salesTarget: Double,
    val salesProgress: Double, // 0.0 - 1.0

    // Sales Performance
    val salesOverTime: List<SalesDataPoint> = emptyList(),
    val salesByTier: List<TierSalesData> = emptyList(),
    val ticketVelocity: Double, // tickets per hour
    val sellOutForecast: SellOutForecast? = null,
    val dailySalesAverage: Double = 0.0,
    val peakSalesDay: String? = null,

    // Audience Insights
    val totalAttendees: Int,
    val repeatAttendees: Int,
    val repeatRate: Double, // 0.0 - 1.0
    val vipShare: Double, // 0.0 - 1.0
    val demographics: DemographicsData? = null,
    val newVsReturning: NewVsReturningData = NewVsReturningData(0, 0),

    // Marketing & Conversion
    val eventViews: Int,
    val uniqueViews: Int,
    val conversionRate: Double, // 0.0 - 1.0
    val trafficSources: List<TrafficSourceData> = emptyList(),
    val promoPerformance: List<PromoPerformanceData> = emptyList(),
    val shareCount: Int = 0,
    val saveCount: Int = 0,

    // Operations
    val checkinRate: Double, // 0.0 - 1.0
    val peakArrivalTime: String? = null,
    val averageArrivalTime: String? = null,
    val queueEstimate: Int = 0, // minutes
    val checkinsByHour: List<CheckinDataPoint> = emptyList(),

    // Financial
    val paymentMethodsSplit: List<PaymentMethodData> = emptyList(),
    val grossRevenue: Double,
    val netRevenue: Double,
    val platformFees: Double,
    val processingFees: Double,
    val refundsTotal: Double = 0.0,
    val refundsCount: Int = 0,

    // Insights & Alerts
    val alerts: List<AnalyticsAlert> = emptyList(),
    val revenueForecast: Double? = null,
    val healthScore: Int = 0 // 0-100
) {
    val formattedRevenue: String
        get() = formatCurrency(revenue)

    val formattedNetRevenue: String
        get() = formatCurrency(netRevenue)

    val capacityPercentage: Int
        get() = (capacityUsed * 100).toInt()

    val attendancePercentage: Int
        get() = (attendanceRate * 100).toInt()

    val conversionPercentage: Double
        get() = conversionRate * 100

    val remainingCapacity: Int
        get() = totalCapacity - ticketsSold

    val averageTicketPrice: Double
        get() = if (ticketsSold > 0) revenue / ticketsSold else 0.0

    val totalFees: Double
        get() = platformFees + processingFees

    private fun formatCurrency(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format("UGX %.1fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("UGX %.0fK", amount / 1_000)
            else -> String.format("UGX %.0f", amount)
        }
    }
}

data class SalesDataPoint(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDateTime,
    val sales: Int,
    val revenue: Double
)

data class TierSalesData(
    val id: String = UUID.randomUUID().toString(),
    val tierName: String,
    val sold: Int,
    val capacity: Int,
    val revenue: Double,
    val price: Double,
    val color: String = "FF7A00" // Hex color
) {
    val percentage: Double
        get() = if (capacity > 0) sold.toDouble() / capacity.toDouble() else 0.0

    val remainingTickets: Int
        get() = capacity - sold

    val isSoldOut: Boolean
        get() = sold >= capacity
}

data class SellOutForecast(
    val estimatedDate: LocalDateTime?,
    val confidence: Double, // 0.0 - 1.0
    val daysRemaining: Int?,
    val willSellOut: Boolean
) {
    val confidenceLevel: String
        get() = when {
            confidence >= 0.8 -> "High"
            confidence >= 0.5 -> "Medium"
            else -> "Low"
        }
}

data class DemographicsData(
    val ageGroups: List<AgeGroupData> = emptyList(),
    val topCities: List<CityData> = emptyList()
)

data class AgeGroupData(
    val id: String = UUID.randomUUID().toString(),
    val ageRange: String,
    val percentage: Double,
    val count: Int
)

data class CityData(
    val id: String = UUID.randomUUID().toString(),
    val city: String,
    val count: Int,
    val percentage: Double
)

data class NewVsReturningData(
    val newAttendees: Int,
    val returningAttendees: Int
) {
    val total: Int
        get() = newAttendees + returningAttendees

    val newPercentage: Double
        get() = if (total > 0) newAttendees.toDouble() / total.toDouble() else 0.0

    val returningPercentage: Double
        get() = if (total > 0) returningAttendees.toDouble() / total.toDouble() else 0.0
}

data class TrafficSourceData(
    val id: String = UUID.randomUUID().toString(),
    val source: String,
    val visits: Int,
    val conversions: Int,
    val percentage: Double,
    val icon: String = "language"
) {
    val conversionRate: Double
        get() = if (visits > 0) conversions.toDouble() / visits.toDouble() else 0.0
}

data class PromoPerformanceData(
    val id: String = UUID.randomUUID().toString(),
    val promoCode: String,
    val usageCount: Int,
    val revenue: Double,
    val discountGiven: Double,
    val isActive: Boolean = true
)

data class CheckinDataPoint(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int, // 0-23
    val checkins: Int
) {
    val formattedHour: String
        get() {
            val period = if (hour < 12) "AM" else "PM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return "$displayHour$period"
        }
}

data class PaymentMethodData(
    val id: String = UUID.randomUUID().toString(),
    val method: String,
    val amount: Double,
    val count: Int,
    val percentage: Double,
    val color: String, // Hex color
    val icon: String
)

data class AnalyticsAlert(
    val id: String = UUID.randomUUID().toString(),
    val type: AlertType,
    val title: String,
    val message: String,
    val severity: AlertSeverity,
    val actionTitle: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    enum class AlertType {
        LOW_SALES,
        HIGH_DEMAND,
        NEAR_SELL_OUT,
        REVENUE_FORECAST,
        SLOW_SALES,
        PRICING_OPPORTUNITY,
        REFUND_SPIKE,
        CAPACITY_WARNING
    }

    enum class AlertSeverity {
        INFO,
        WARNING,
        SUCCESS,
        CRITICAL;

        val iconName: String
            get() = when (this) {
                INFO -> "info"
                WARNING -> "warning"
                SUCCESS -> "check_circle"
                CRITICAL -> "error"
            }

        val colorHex: Long
            get() = when (this) {
                INFO -> 0xFF2196F3
                WARNING -> 0xFFFF9800
                SUCCESS -> 0xFF4CAF50
                CRITICAL -> 0xFFF44336
            }
    }
}
