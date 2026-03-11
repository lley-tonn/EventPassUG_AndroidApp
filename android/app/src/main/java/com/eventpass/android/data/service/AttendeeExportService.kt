package com.eventpass.android.data.service

import android.content.Context
import android.util.Log
import com.eventpass.android.domain.models.Attendee
import com.eventpass.android.domain.models.CheckInStatus
import com.eventpass.android.domain.models.OrganizerAnalytics
import com.eventpass.android.domain.models.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AttendeeExportFilter - matches iOS AttendeeExportFilter.
 * Filters for exporting attendee lists.
 */
enum class AttendeeExportFilter(val value: String) {
    ALL("all"),
    CHECKED_IN("checkedIn"),
    VIP("vip"),
    MARKETING_CONSENTED("marketingConsented");

    val displayName: String
        get() = when (this) {
            ALL -> "All Attendees"
            CHECKED_IN -> "Checked In Only"
            VIP -> "VIP Only"
            MARKETING_CONSENTED -> "Marketing Opt-in"
        }

    /**
     * Filters attendees based on the filter type.
     */
    fun filter(attendees: List<Attendee>): List<Attendee> {
        return when (this) {
            ALL -> attendees
            CHECKED_IN -> attendees.filter { it.checkInStatus == CheckInStatus.CHECKED_IN }
            VIP -> attendees.filter { it.isVIP }
            MARKETING_CONSENTED -> attendees.filter { it.marketingConsent }
        }
    }
}

/**
 * Export errors.
 */
sealed class ExportError : Exception() {
    object EventMismatch : ExportError() {
        private fun readResolve(): Any = EventMismatch
        override val message: String = "Attendees belong to multiple events. Export must be scoped to single event."
    }

    object NoDataToExport : ExportError() {
        private fun readResolve(): Any = NoDataToExport
        override val message: String = "No data to export with the selected filter."
    }

    object FileGenerationFailed : ExportError() {
        private fun readResolve(): Any = FileGenerationFailed
        override val message: String = "Failed to generate export file."
    }
}

/**
 * Export analytics event.
 */
data class ExportAnalyticsEvent(
    val name: String,
    val eventId: String,
    val format: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val filterType: String,
    val attendeeCount: Int
)

/**
 * AttendeeExportService.
 * Migrated from iOS Data/Services/AttendeeExportService.swift
 *
 * Service for exporting attendee lists for a specific event.
 * CRITICAL: All exports are strictly scoped to a single eventId.
 */
@Singleton
class AttendeeExportService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // MARK: - Fetch Attendees

    /**
     * Fetches attendees for a SPECIFIC event only.
     * In production, this would call the ticket repository.
     */
    suspend fun fetchAttendees(eventId: String): List<Attendee> {
        // CRITICAL: Fetch ONLY tickets for this specific event
        // This ensures we never accidentally include data from other events

        // Mock implementation - in production, fetch from repository
        val attendees = Attendee.mockAttendees(eventId, count = 25)

        // Safety verification: ensure all attendees belong to the requested event
        val allBelongToEvent = attendees.all { it.eventId == eventId }
        if (!allBelongToEvent) {
            throw ExportError.EventMismatch
        }

        return attendees
    }

    // MARK: - Export Attendees

    /**
     * Exports attendees for a SPECIFIC event with the given filter.
     * Returns the File path to the generated CSV file.
     */
    suspend fun exportAttendees(
        eventId: String,
        eventTitle: String,
        filter: AttendeeExportFilter
    ): File {
        // Fetch attendees for this specific event
        val attendees = fetchAttendees(eventId)

        // Apply the selected filter
        val filteredAttendees = filter.filter(attendees)

        if (filteredAttendees.isEmpty()) {
            throw ExportError.NoDataToExport
        }

        // Generate CSV
        val file = CSVGenerator.generateAttendeeCSV(
            context = context,
            attendees = filteredAttendees,
            eventTitle = eventTitle,
            filter = filter
        ) ?: throw ExportError.FileGenerationFailed

        // Track analytics
        trackExportAnalytics(eventId, filteredAttendees.size, filter)

        return file
    }

    // MARK: - Analytics Tracking

    private fun trackExportAnalytics(
        eventId: String,
        attendeeCount: Int,
        filter: AttendeeExportFilter
    ) {
        val analyticsEvent = ExportAnalyticsEvent(
            name = "attendee_list_exported",
            eventId = eventId,
            format = "csv",
            filterType = filter.value,
            attendeeCount = attendeeCount
        )

        Log.d("ExportAnalytics", "${analyticsEvent.name} - eventId: $eventId, count: $attendeeCount, filter: ${filter.value}")
    }

    // MARK: - Mock Data

    /**
     * Provides mock attendees for preview and testing.
     */
    fun mockAttendees(eventId: String, count: Int = 25): List<Attendee> {
        return Attendee.mockAttendees(eventId, count)
    }
}

/**
 * CSVGenerator.
 * Migrated from iOS Core/Utilities/CSVGenerator.swift
 *
 * CSV file generation utility for export functionality.
 */
object CSVGenerator {

    // MARK: - Attendee Export

    /**
     * Generates a CSV file for attendees of a specific event.
     * Returns the generated File, or null if generation fails.
     */
    fun generateAttendeeCSV(
        context: Context,
        attendees: List<Attendee>,
        eventTitle: String,
        filter: AttendeeExportFilter
    ): File? {
        // Verify all attendees belong to the same event
        val firstEventId = attendees.firstOrNull()?.eventId ?: return null
        val allSameEvent = attendees.all { it.eventId == firstEventId }

        if (!allSameEvent) {
            Log.e("CSVGenerator", "Attendees belong to multiple events. Export must be scoped to single event.")
            return null
        }

        val csvContent = StringBuilder()

        // Header row - Email/Phone are NEVER exported for privacy
        val headers = listOf(
            "Full Name",
            "Ticket Type",
            "Order ID",
            "Purchase Date",
            "Check-in Status",
            "Attendance Status"
        )
        csvContent.appendLine(headers.joinToString(","))

        // Data rows
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")

        for (attendee in attendees) {
            val row = listOf(
                escapeCSV(attendee.fullName),
                escapeCSV(attendee.ticketType),
                escapeCSV(attendee.orderId),
                escapeCSV(attendee.purchaseDate.format(dateFormatter)),
                escapeCSV(attendee.checkInStatus.value),
                escapeCSV(attendee.attendanceStatus.value)
            )
            csvContent.appendLine(row.joinToString(","))
        }

        // Generate filename
        val sanitizedTitle = eventTitle
            .replace(" ", "_")
            .replace("/", "-")
        val filterSuffix = if (filter == AttendeeExportFilter.ALL) "" else "_${filter.value.replace(" ", "_")}"
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val fileName = "Attendees_${sanitizedTitle}${filterSuffix}_$timestamp.csv"

        // Save to cache directory
        return try {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)
            file.writeText(csvContent.toString())
            file
        } catch (e: Exception) {
            Log.e("CSVGenerator", "Failed to write CSV: ${e.message}")
            null
        }
    }

    // MARK: - Event Report CSV Export

    /**
     * Generates a CSV summary report for a specific event's analytics.
     * Returns the generated File, or null if generation fails.
     */
    fun generateEventReportCSV(
        context: Context,
        analytics: OrganizerAnalytics,
        event: Event
    ): File? {
        // Verify analytics belongs to the correct event
        if (analytics.eventId != event.id) {
            Log.e("CSVGenerator", "Analytics eventId does not match event.id. Export must be scoped to single event.")
            return null
        }

        val csvContent = StringBuilder()

        // Event Summary Section
        csvContent.appendLine("Event Report")
        csvContent.appendLine("Generated,${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}")
        csvContent.appendLine()

        // Event Details
        csvContent.appendLine("Event Details")
        csvContent.appendLine("Event Name,${escapeCSV(event.title)}")
        csvContent.appendLine("Event Date,${event.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a"))}")
        csvContent.appendLine("Venue,${escapeCSV(event.venue.name)}")
        csvContent.appendLine("Location,${escapeCSV("${event.venue.city}, ${event.venue.address}")}")
        csvContent.appendLine()

        // Revenue Metrics
        csvContent.appendLine("Revenue Metrics")
        csvContent.appendLine("Total Revenue,${formatCurrency(analytics.revenue)}")
        csvContent.appendLine("Gross Revenue,${formatCurrency(analytics.grossRevenue)}")
        csvContent.appendLine("Net Revenue,${formatCurrency(analytics.netRevenue)}")
        csvContent.appendLine("Platform Fees,${formatCurrency(analytics.platformFees)}")
        csvContent.appendLine("Processing Fees,${formatCurrency(analytics.processingFees)}")
        csvContent.appendLine()

        // Ticket Sales
        csvContent.appendLine("Ticket Sales")
        csvContent.appendLine("Tickets Sold,${analytics.ticketsSold}")
        csvContent.appendLine("Total Capacity,${analytics.totalCapacity}")
        csvContent.appendLine("Capacity Used,${String.format("%.1f%%", analytics.capacityUsed * 100)}")
        csvContent.appendLine()

        // Attendance
        csvContent.appendLine("Attendance")
        csvContent.appendLine("Attendance Rate,${String.format("%.1f%%", analytics.attendanceRate * 100)}")
        csvContent.appendLine("Check-in Rate,${String.format("%.1f%%", analytics.checkinRate * 100)}")
        csvContent.appendLine()

        // Refunds
        csvContent.appendLine("Refunds")
        csvContent.appendLine("Refund Count,${analytics.refundsCount}")
        csvContent.appendLine("Refund Amount,${formatCurrency(analytics.refundsTotal)}")
        csvContent.appendLine()

        // Payment Methods
        csvContent.appendLine("Payment Method Breakdown")
        csvContent.appendLine("Method,Amount,Count,Percentage")
        for (payment in analytics.paymentMethodsSplit) {
            csvContent.appendLine(
                "${escapeCSV(payment.method)},${formatCurrency(payment.amount)},${payment.count},${String.format("%.1f%%", payment.percentage * 100)}"
            )
        }
        csvContent.appendLine()

        // Ticket Tier Breakdown
        csvContent.appendLine("Ticket Tier Breakdown")
        csvContent.appendLine("Tier,Sold,Capacity,Revenue,Price")
        for (tier in analytics.salesByTier) {
            csvContent.appendLine(
                "${escapeCSV(tier.tierName)},${tier.sold},${tier.capacity},${formatCurrency(tier.revenue)},${formatCurrency(tier.price)}"
            )
        }

        // Generate filename
        val sanitizedTitle = event.title
            .replace(" ", "_")
            .replace("/", "-")
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val fileName = "EventReport_${sanitizedTitle}_$timestamp.csv"

        // Save to cache directory
        return try {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)
            file.writeText(csvContent.toString())
            file
        } catch (e: Exception) {
            Log.e("CSVGenerator", "Failed to write CSV: ${e.message}")
            null
        }
    }

    // MARK: - Helper Functions

    /**
     * Escapes a string value for CSV format.
     */
    private fun escapeCSV(value: String): String {
        // If the value contains commas, quotes, or newlines, wrap in quotes and escape existing quotes
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            val escaped = value.replace("\"", "\"\"")
            "\"$escaped\""
        } else {
            value
        }
    }

    private fun formatCurrency(amount: Double): String {
        return String.format("UGX %.0f", amount)
    }
}
