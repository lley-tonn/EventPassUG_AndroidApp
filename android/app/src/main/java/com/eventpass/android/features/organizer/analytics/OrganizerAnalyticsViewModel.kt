package com.eventpass.android.features.organizer.analytics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.core.state.UiState
import com.eventpass.android.domain.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for Organizer Analytics Dashboard.
 * Migrated from iOS Features/Organizer/OrganizerAnalyticsDashboardViewModel.swift
 *
 * SwiftUI → Compose state mapping:
 * - @Published var analytics → StateFlow<UiState<OrganizerAnalytics>>
 * - @Published var selectedTimeRange → Included in UI state
 */
@HiltViewModel
class OrganizerAnalyticsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String? = savedStateHandle.get<String>("eventId")

    // MARK: - State

    private val _analyticsState = MutableStateFlow<UiState<OrganizerAnalytics>>(UiState.Idle)
    val analyticsState: StateFlow<UiState<OrganizerAnalytics>> = _analyticsState.asStateFlow()

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    // MARK: - Actions

    /**
     * Load analytics data.
     */
    fun loadAnalytics() {
        viewModelScope.launch {
            _analyticsState.value = UiState.Loading

            // Simulate network delay
            delay(1000)

            // TODO: Replace with real API call
            // val analytics = analyticsRepository.getAnalytics(eventId, timeRange)

            // Use mock data for now
            val mockAnalytics = createMockAnalytics()
            _analyticsState.value = UiState.Success(mockAnalytics)
        }
    }

    /**
     * Refresh analytics data.
     */
    fun refreshAnalytics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            delay(800)
            val mockAnalytics = createMockAnalytics()
            _analyticsState.value = UiState.Success(mockAnalytics)

            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    /**
     * Set time range for analytics.
     */
    fun setTimeRange(range: TimeRange) {
        _uiState.update { it.copy(selectedTimeRange = range) }
        loadAnalytics()
    }

    /**
     * Get revenue growth percentage.
     */
    fun getRevenueGrowth(): Double {
        val analytics = (_analyticsState.value as? UiState.Success)?.data ?: return 0.0
        // Calculate growth based on historical data
        val salesData = analytics.salesOverTime
        if (salesData.size < 2) return 0.0

        val recentRevenue = salesData.takeLast(7).sumOf { it.revenue }
        val previousRevenue = salesData.dropLast(7).takeLast(7).sumOf { it.revenue }

        return if (previousRevenue > 0) {
            ((recentRevenue - previousRevenue) / previousRevenue) * 100
        } else {
            0.0
        }
    }

    /**
     * Check if event is performing well.
     */
    fun isPerformingWell(): Boolean {
        val analytics = (_analyticsState.value as? UiState.Success)?.data ?: return false
        return analytics.healthScore >= 70
    }

    // MARK: - Mock Data

    private fun createMockAnalytics(): OrganizerAnalytics {
        return OrganizerAnalytics(
            eventId = eventId ?: "mock-event",
            eventTitle = "Sample Event",
            lastUpdated = LocalDateTime.now(),
            // Overview
            revenue = 12_450_000.0,
            ticketsSold = 342,
            totalCapacity = 500,
            attendanceRate = 0.85,
            capacityUsed = 0.684,
            salesTarget = 18_000_000.0,
            salesProgress = 0.69,
            // Sales Performance
            salesOverTime = generateMockSalesData(),
            salesByTier = listOf(
                TierSalesData(tierName = "Early Bird", sold = 100, capacity = 100, revenue = 2_000_000.0, price = 20_000.0),
                TierSalesData(tierName = "Regular", sold = 180, capacity = 300, revenue = 7_200_000.0, price = 40_000.0),
                TierSalesData(tierName = "VIP", sold = 52, capacity = 80, revenue = 2_600_000.0, price = 50_000.0),
                TierSalesData(tierName = "VVIP", sold = 10, capacity = 20, revenue = 650_000.0, price = 65_000.0)
            ),
            ticketVelocity = 12.5,
            sellOutForecast = SellOutForecast(
                estimatedDate = LocalDateTime.now().plusDays(14),
                confidence = 0.75,
                daysRemaining = 14,
                willSellOut = true
            ),
            dailySalesAverage = 11.4,
            peakSalesDay = "Friday",
            // Audience Insights
            totalAttendees = 342,
            repeatAttendees = 96,
            repeatRate = 0.28,
            vipShare = 0.18,
            demographics = DemographicsData(
                ageGroups = listOf(
                    AgeGroupData(ageRange = "18-24", percentage = 0.35, count = 120),
                    AgeGroupData(ageRange = "25-34", percentage = 0.40, count = 137),
                    AgeGroupData(ageRange = "35-44", percentage = 0.18, count = 62),
                    AgeGroupData(ageRange = "45+", percentage = 0.07, count = 23)
                ),
                topCities = listOf(
                    CityData(city = "Kampala", count = 280, percentage = 0.82),
                    CityData(city = "Entebbe", count = 35, percentage = 0.10),
                    CityData(city = "Jinja", count = 27, percentage = 0.08)
                )
            ),
            newVsReturning = NewVsReturningData(newAttendees = 246, returningAttendees = 96),
            // Marketing & Conversion
            eventViews = 5032,
            uniqueViews = 3421,
            conversionRate = 0.10,
            trafficSources = listOf(
                TrafficSourceData(source = "Instagram", visits = 1450, conversions = 145, percentage = 0.42),
                TrafficSourceData(source = "WhatsApp", visits = 980, conversions = 98, percentage = 0.29),
                TrafficSourceData(source = "Twitter", visits = 560, conversions = 56, percentage = 0.16),
                TrafficSourceData(source = "Direct", visits = 431, conversions = 43, percentage = 0.13)
            ),
            promoPerformance = listOf(
                PromoPerformanceData(promoCode = "EARLY20", usageCount = 45, revenue = 720_000.0, discountGiven = 180_000.0),
                PromoPerformanceData(promoCode = "VIP10", usageCount = 12, revenue = 540_000.0, discountGiven = 60_000.0)
            ),
            shareCount = 234,
            saveCount = 567,
            // Operations
            checkinRate = 0.0,
            peakArrivalTime = "7:00 PM",
            averageArrivalTime = "6:45 PM",
            queueEstimate = 5,
            checkinsByHour = emptyList(),
            // Financial
            paymentMethodsSplit = listOf(
                PaymentMethodData(method = "MTN MoMo", amount = 7_470_000.0, count = 205, percentage = 0.60, color = "FFCC00", icon = "phone_android"),
                PaymentMethodData(method = "Airtel Money", amount = 3_735_000.0, count = 103, percentage = 0.30, color = "FF0000", icon = "phone_android"),
                PaymentMethodData(method = "Card", amount = 1_245_000.0, count = 34, percentage = 0.10, color = "0066CC", icon = "credit_card")
            ),
            grossRevenue = 12_450_000.0,
            netRevenue = 11_205_000.0,
            platformFees = 622_500.0,
            processingFees = 622_500.0,
            refundsTotal = 240_000.0,
            refundsCount = 6,
            // Insights & Alerts
            alerts = listOf(
                AnalyticsAlert(
                    type = AnalyticsAlert.AlertType.HIGH_DEMAND,
                    title = "High Demand",
                    message = "VIP tickets selling faster than expected",
                    severity = AnalyticsAlert.AlertSeverity.SUCCESS
                )
            ),
            revenueForecast = 16_500_000.0,
            healthScore = 85
        )
    }

    private fun generateMockSalesData(): List<SalesDataPoint> {
        val today = LocalDate.now()
        return (30 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val baseTickets = (5..20).random()
            val baseRevenue = baseTickets * 36000.0 * (0.8 + Math.random() * 0.4)
            SalesDataPoint(
                date = date.atStartOfDay(),
                sales = baseTickets,
                revenue = baseRevenue
            )
        }
    }
}

/**
 * UI State for Analytics Dashboard.
 */
data class AnalyticsUiState(
    val selectedTimeRange: TimeRange = TimeRange.LAST_7_DAYS,
    val isRefreshing: Boolean = false
)

/**
 * Time range options for analytics.
 */
enum class TimeRange(val displayName: String, val shortName: String) {
    LAST_24_HOURS("Last 24 Hours", "24h"),
    LAST_7_DAYS("Last 7 Days", "7d"),
    LAST_30_DAYS("Last 30 Days", "30d"),
    ALL_TIME("All Time", "All")
}
