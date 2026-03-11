package com.eventpass.android.core.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Currency utilities.
 * Migrated from iOS utility functions for currency formatting.
 */
object CurrencyUtils {

    /**
     * Default currency for Uganda.
     */
    const val DEFAULT_CURRENCY = "UGX"

    /**
     * Format amount as currency string.
     */
    fun formatCurrency(
        amount: Double,
        currency: String = DEFAULT_CURRENCY,
        showDecimals: Boolean = false
    ): String {
        val formatter = NumberFormat.getInstance(Locale.US)
        formatter.minimumFractionDigits = if (showDecimals) 2 else 0
        formatter.maximumFractionDigits = if (showDecimals) 2 else 0

        return "$currency ${formatter.format(amount)}"
    }

    /**
     * Format amount in compact form (K, M, B).
     */
    fun formatCompact(amount: Double, currency: String = DEFAULT_CURRENCY): String {
        return when {
            amount >= 1_000_000_000 -> String.format("$currency %.1fB", amount / 1_000_000_000)
            amount >= 1_000_000 -> String.format("$currency %.1fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("$currency %.0fK", amount / 1_000)
            else -> String.format("$currency %.0f", amount)
        }
    }

    /**
     * Parse currency string to double.
     */
    fun parseCurrency(value: String): Double? {
        return try {
            val cleaned = value
                .replace(Regex("[^0-9.]"), "")
                .trim()
            cleaned.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Format price range.
     */
    fun formatPriceRange(
        min: Double,
        max: Double,
        currency: String = DEFAULT_CURRENCY
    ): String {
        return when {
            min == 0.0 && max == 0.0 -> "Free"
            min == max -> formatCurrency(min, currency)
            min == 0.0 -> "Free - ${formatCurrency(max, currency)}"
            else -> "${formatCurrency(min, currency)} - ${formatCurrency(max, currency)}"
        }
    }

    /**
     * Calculate percentage.
     */
    fun calculatePercentage(part: Double, total: Double): Double {
        return if (total > 0) (part / total) * 100 else 0.0
    }

    /**
     * Format percentage.
     */
    fun formatPercentage(value: Double, decimals: Int = 0): String {
        return String.format("%.${decimals}f%%", value)
    }
}
