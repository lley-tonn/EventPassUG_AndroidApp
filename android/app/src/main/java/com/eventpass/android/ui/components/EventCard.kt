package com.eventpass.android.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eventpass.android.core.util.DateUtils
import com.eventpass.android.domain.models.Event
import com.eventpass.android.ui.theme.EventPassColors
import com.eventpass.android.ui.theme.EventPassDimensions

/**
 * Event card component.
 * Migrated from iOS UI/Components/EventCard.swift
 *
 * SwiftUI → Compose mapping used:
 * - VStack → Column
 * - HStack → Row
 * - ZStack → Box
 * - Image → AsyncImage (with Coil)
 * - Button → IconButton
 * - .cornerRadius() → .clip(RoundedCornerShape())
 * - .shadow() → Card with elevation
 * - @Binding → callback function
 */
@Composable
fun EventCard(
    event: Event,
    isLiked: Boolean,
    onLikeTap: () -> Unit,
    onCardTap: () -> Unit,
    onShareTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardTap)
            .semantics {
                contentDescription = "${event.title}, ${DateUtils.formatDateTime(event.startDate)}, ${event.venue.name}"
            },
        shape = RoundedCornerShape(EventPassDimensions.CornerRadius.md),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Poster image with overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(EventPassDimensions.Card.posterHeight)
            ) {
                // Poster image
                if (event.posterUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(event.posterUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Event poster for ${event.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(EventPassDimensions.Card.posterHeight),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(EventPassDimensions.Card.posterHeight)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Happening now indicator (top-left)
                if (event.isHappeningNow) {
                    HappeningNowBadge(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(EventPassDimensions.Spacing.sm)
                    )
                }

                // Share button (top-right)
                onShareTap?.let { shareAction ->
                    IconButton(
                        onClick = shareAction,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(EventPassDimensions.Spacing.sm)
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share event",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Event details
            Column(
                modifier = Modifier.padding(EventPassDimensions.Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm)
            ) {
                // Title and like button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    AnimatedLikeButton(
                        isLiked = isLiked,
                        onTap = onLikeTap
                    )
                }

                // Date and time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = DateUtils.formatDateTime(event.startDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Venue
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = event.venue.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Price and rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.priceRange,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = EventPassColors.Primary
                    )

                    if (event.totalRatings > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFFFFD700) // Gold
                            )
                            Text(
                                text = String.format("%.1f", event.rating),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "(${event.totalRatings})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Happening now badge with pulsing dot.
 */
@Composable
fun HappeningNowBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = EventPassDimensions.Spacing.sm,
                vertical = EventPassDimensions.Spacing.xs
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.xs)
        ) {
            PulsingDot(
                color = EventPassColors.HappeningNow,
                dotSize = 10.dp
            )
            Text(
                text = "Happening now",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Animated like button with scale animation.
 * Migrated from iOS UI/Components/AnimatedLikeButton.swift
 */
@Composable
fun AnimatedLikeButton(
    isLiked: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAnimating by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        finishedListener = { isAnimating = false },
        label = "like_scale"
    )

    IconButton(
        onClick = {
            isAnimating = true
            onTap()
        },
        modifier = modifier.size(EventPassDimensions.Button.minimumTouchTarget)
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isLiked) "Unlike" else "Like",
            modifier = Modifier
                .size(22.dp)
                .scale(scale),
            tint = if (isLiked) Color.Red else Color.Gray
        )
    }
}

/**
 * Pulsing dot animation.
 * Migrated from iOS UI/Components/PulsingDot.swift
 */
@Composable
fun PulsingDot(
    color: Color = EventPassColors.HappeningNow,
    dotSize: androidx.compose.ui.unit.Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "pulsing"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(dotSize)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}
