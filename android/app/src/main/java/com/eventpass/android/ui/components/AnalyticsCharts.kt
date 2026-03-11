package com.eventpass.android.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eventpass.android.ui.theme.EventPassColors
import com.eventpass.android.ui.theme.EventPassDimensions
import java.util.UUID

/**
 * Analytics chart components.
 * Migrated from iOS UI/Components/AnalyticsCharts.swift
 *
 * SwiftUI → Compose mapping:
 * - Shape → Canvas with drawArc/drawPath
 * - GeometryReader → BoxWithConstraints or Modifier.fillMaxWidth
 * - animatableData → animateFloatAsState
 */

// MARK: - Data Classes

data class DonutSegment(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val value: Double,
    val percentage: Double,
    val color: Color
)

data class LineChartDataPoint(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val value: Double
)

data class BarChartData(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val value: Double,
    val color: Color? = null
) {
    val formattedValue: String
        get() = when {
            value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000)
            value >= 1_000 -> String.format("%.0fK", value / 1_000)
            else -> String.format("%.0f", value)
        }
}

// MARK: - Donut Chart

@Composable
fun DonutChartView(
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    lineWidth: Dp = 20.dp,
    showLabels: Boolean = true,
    centerText: String? = null,
    centerSubtext: String? = null
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animationProgress = 1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "donut_progress"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.md)
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val strokeWidth = lineWidth.toPx()
                val radius = (this.size.minDimension - strokeWidth) / 2
                val center = Offset(this.size.width / 2, this.size.height / 2)

                // Background circle
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.1f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Segments
                var startAngle = -90f
                segments.forEach { segment ->
                    val sweepAngle = (segment.percentage * 360f * animatedProgress).toFloat()
                    drawArc(
                        color = segment.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(
                            center.x - radius,
                            center.y - radius
                        ),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }

            // Center content
            if (centerText != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = centerText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (centerSubtext != null) {
                        Text(
                            text = centerSubtext,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Legend
        if (showLabels) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(segments) { segment ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(segment.color)
                        )
                        Text(
                            text = segment.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${(segment.percentage * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// MARK: - Line Chart

@Composable
fun LineChartView(
    dataPoints: List<LineChartDataPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = EventPassColors.Primary,
    fillGradient: Boolean = true,
    showPoints: Boolean = true,
    showGrid: Boolean = true,
    height: Dp = 150.dp,
    showXLabels: Boolean = true,
    showYLabels: Boolean = true
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animationProgress = 1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1200),
        label = "line_progress"
    )

    val maxValue = dataPoints.maxOfOrNull { it.value } ?: 1.0
    val minValue = 0.0

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            // Y-axis labels
            if (showYLabels) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatValue(maxValue),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatValue(maxValue * 0.5),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = if (showYLabels) 40.dp else 0.dp)
            ) {
                if (dataPoints.size < 2) return@Canvas

                val width = this.size.width
                val chartHeight = this.size.height
                val stepX = width / (dataPoints.size - 1)

                // Grid lines
                if (showGrid) {
                    for (i in 0..3) {
                        val y = chartHeight * i / 4
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.15f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }
                }

                // Create path for line
                val linePath = Path()
                val fillPath = Path()

                dataPoints.forEachIndexed { index, point ->
                    val x = index * stepX
                    val normalizedY = ((point.value - minValue) / (maxValue - minValue)).toFloat()
                    val y = chartHeight - (normalizedY * chartHeight * animatedProgress)

                    if (index == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, chartHeight)
                        fillPath.lineTo(x, y)
                    } else {
                        linePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }
                }

                // Gradient fill
                if (fillGradient) {
                    fillPath.lineTo(width, chartHeight)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.3f),
                                lineColor.copy(alpha = 0.05f)
                            )
                        )
                    )
                }

                // Line
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(
                        width = 2.5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Data points
                if (showPoints) {
                    dataPoints.forEachIndexed { index, point ->
                        val x = index * stepX
                        val normalizedY = ((point.value - minValue) / (maxValue - minValue)).toFloat()
                        val y = chartHeight - (normalizedY * chartHeight * animatedProgress)

                        // Outer circle (white border)
                        drawCircle(
                            color = Color.White,
                            radius = 6f,
                            center = Offset(x, y)
                        )
                        // Inner circle (colored)
                        drawCircle(
                            color = lineColor,
                            radius = 4f,
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        // X-axis labels
        if (showXLabels && dataPoints.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = if (showYLabels) 40.dp else 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val step = maxOf(1, dataPoints.size / 5)
                for (i in dataPoints.indices step step) {
                    Text(
                        text = dataPoints[i].label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// MARK: - Bar Chart

@Composable
fun BarChartView(
    bars: List<BarChartData>,
    modifier: Modifier = Modifier,
    barColor: Color = EventPassColors.Primary,
    height: Dp = 150.dp,
    showValues: Boolean = true,
    horizontal: Boolean = false,
    barSpacing: Dp = 8.dp
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animationProgress = 1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 100f
        ),
        label = "bar_progress"
    )

    val maxValue = bars.maxOfOrNull { it.value } ?: 1.0

    if (horizontal) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(barSpacing)
        ) {
            bars.forEach { bar ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bar.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(60.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        val widthFraction = ((bar.value / maxValue) * animatedProgress).toFloat()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(widthFraction.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(bar.color ?: barColor)
                        )
                    }

                    if (showValues) {
                        Text(
                            text = bar.formattedValue,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.width(50.dp)
                        )
                    }
                }
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(height),
            horizontalArrangement = Arrangement.spacedBy(barSpacing),
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEach { bar ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs)
                ) {
                    if (showValues) {
                        Text(
                            text = bar.formattedValue,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    val barHeight = ((bar.value / maxValue) * (height.value - 40) * animatedProgress).dp
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(maxOf(barHeight, 4.dp))
                            .clip(RoundedCornerShape(4.dp))
                            .background(bar.color ?: barColor)
                    )

                    Text(
                        text = bar.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// MARK: - Progress Ring

@Composable
fun ProgressRingView(
    progress: Double,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    lineWidth: Dp = 8.dp,
    color: Color = EventPassColors.Primary,
    backgroundColor: Color = Color.Gray.copy(alpha = 0.2f),
    showPercentage: Boolean = true
) {
    val safeProgress = if (progress.isFinite()) progress.coerceIn(0.0, 1.0) else 0.0

    var animatedProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(safeProgress) {
        animatedProgress = safeProgress.toFloat()
    }

    val animatedValue by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "ring_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = lineWidth.toPx()
            val radius = (this.size.minDimension - strokeWidth) / 2
            val center = Offset(this.size.width / 2, this.size.height / 2)

            // Background ring
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Progress ring
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = animatedValue * 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Percentage text
        if (showPercentage) {
            Text(
                text = "${(animatedValue * 100).toInt()}%",
                style = if (size > 50.dp) {
                    MaterialTheme.typography.labelMedium
                } else {
                    MaterialTheme.typography.labelSmall
                },
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// MARK: - Sparkline

@Composable
fun SparklineView(
    values: List<Double>,
    modifier: Modifier = Modifier,
    color: Color = EventPassColors.Primary,
    height: Dp = 30.dp
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animationProgress = 1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 800),
        label = "sparkline_progress"
    )

    val maxValue = values.maxOrNull() ?: 1.0
    val minValue = values.minOrNull() ?: 0.0
    val range = if (maxValue - minValue > 0) maxValue - minValue else 1.0

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (values.size < 2) return@Canvas

        val width = this.size.width
        val chartHeight = this.size.height
        val stepX = width / (values.size - 1)

        val path = Path()

        values.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = ((value - minValue) / range).toFloat()
            val y = chartHeight - (normalizedY * chartHeight * animatedProgress)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

// MARK: - Helper Functions

private fun formatValue(value: Double): String {
    return when {
        value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000)
        value >= 1_000 -> String.format("%.0fK", value / 1_000)
        else -> String.format("%.0f", value)
    }
}
