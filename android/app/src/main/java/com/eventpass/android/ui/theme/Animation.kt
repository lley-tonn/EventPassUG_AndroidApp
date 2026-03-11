package com.eventpass.android.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Animation specifications.
 * Migrated from iOS AppDesignSystem.swift Animation struct
 *
 * SwiftUI Animation → Compose AnimationSpec mapping:
 * - .easeInOut(duration:) → tween(durationMillis, easing = FastOutSlowInEasing)
 * - .spring(response:, dampingFraction:) → spring(dampingRatio, stiffness)
 */
object EventPassAnimation {

    // Quick animation (150ms)
    val quick = tween<Float>(
        durationMillis = 150,
        easing = FastOutSlowInEasing
    )

    // Standard animation (200ms)
    val standard = tween<Float>(
        durationMillis = 200,
        easing = FastOutSlowInEasing
    )

    // Slow animation (400ms)
    val slow = tween<Float>(
        durationMillis = 400,
        easing = FastOutSlowInEasing
    )

    // Spring animation (response: 0.3, dampingFraction: 0.7)
    val spring = spring<Float>(
        dampingRatio = 0.7f,
        stiffness = Spring.StiffnessMedium
    )

    // Bouncy spring animation (response: 0.4, dampingFraction: 0.6)
    val springBouncy = spring<Float>(
        dampingRatio = 0.6f,
        stiffness = Spring.StiffnessMediumLow
    )

    // Duration constants
    object Duration {
        const val QUICK = 150
        const val STANDARD = 200
        const val SLOW = 400
        const val EXTRA_SLOW = 600
    }
}

// Type alias
typealias AppAnimation = EventPassAnimation
