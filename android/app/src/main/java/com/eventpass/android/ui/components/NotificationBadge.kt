package com.eventpass.android.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eventpass.android.ui.theme.EventPassColors
import kotlinx.coroutines.delay

/**
 * Notification badge with animated bounce effect.
 * Migrated from iOS UI/Components/NotificationBadge.swift
 *
 * SwiftUI → Compose mapping:
 * - @State private var isBouncing → remember { mutableStateOf }
 * - .scaleEffect() → Modifier.scale()
 * - .onChange(of:) → LaunchedEffect with key
 * - withAnimation(.spring()) → animateFloatAsState with spring
 */
@Composable
fun NotificationBadge(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBouncing by remember { mutableStateOf(false) }
    var previousCount by remember { mutableIntStateOf(0) }

    // Bounce animation when count increases
    val scale by animateFloatAsState(
        targetValue = if (isBouncing) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "badge_scale"
    )

    // Watch for count changes
    LaunchedEffect(count) {
        if (count > previousCount) {
            isBouncing = true
            delay(300)
            isBouncing = false
        }
        previousCount = count
    }

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .semantics {
                contentDescription = if (count > 0) {
                    "$count unread notifications"
                } else {
                    "Notifications"
                }
            },
        contentAlignment = Alignment.TopEnd
    ) {
        // Bell icon
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )

        // Badge count
        if (count > 0) {
            Box(
                modifier = Modifier
                    .offset(x = 8.dp, y = (-8).dp)
                    .scale(scale)
                    .size(if (count > 9) 20.dp else 16.dp)
                    .clip(CircleShape)
                    .background(EventPassColors.Error),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > 99) "99+" else count.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = if (count > 9) 8.sp else 10.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Simple notification dot without count.
 */
@Composable
fun NotificationDot(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = EventPassColors.Error
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}
