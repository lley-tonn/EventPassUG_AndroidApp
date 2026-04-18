package com.eventpass.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eventpass.core.design.theme.EventPassTheme
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.core.design.tokens.softShadow

/**
 * Reusable event card matching the iOS reference (IMG_2757 onward).
 *
 * Takes primitives only so `:core:design` stays free of domain-model deps —
 * callers map from their `Event` / `Ticket` / whatever into these fields.
 *
 * Layout:
 *   - 16:10 hero image (skeleton grey when [imageUrl] null) with optional
 *     "Happening now" green badge (top-left) and share button (top-right)
 *   - Title + favorite heart on one row
 *   - Calendar-icon + date row
 *   - Pin-icon + venue row
 *   - Orange price (left) + star rating pill (right)
 */
@Composable
fun EventCard(
    title: String,
    dateText: String,
    venueText: String,
    priceText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    isHappeningNow: Boolean = false,
    isFavorited: Boolean = false,
    onFavoriteToggle: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    rating: Double? = null,
    ratingCount: Int? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .softShadow(elevation = 6.dp, shape = Radii.CardLarge)
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
            .clickable(onClick = onClick)
    ) {
        EventHero(
            imageUrl = imageUrl,
            isHappeningNow = isHappeningNow,
            onShare = onShare
        )

        Column(
            modifier = Modifier.padding(
                horizontal = Spacing.lg,
                vertical = Spacing.lg
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = EventPassColors.Ink,
                    modifier = Modifier.weight(1f)
                )
                if (onFavoriteToggle != null) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorited) "Unfavorite" else "Favorite",
                        tint = if (isFavorited) EventPassColors.Primary else EventPassColors.InkSubtle,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onFavoriteToggle)
                            .padding(2.dp)
                    )
                }
            }

            MetaRow(icon = Icons.Filled.CalendarToday, text = dateText)
            MetaRow(icon = Icons.Filled.LocationOn, text = venueText)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = priceText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = EventPassColors.Primary
                )
                if (rating != null) {
                    RatingPill(rating = rating, count = ratingCount)
                }
            }
        }
    }
}

@Composable
private fun EventHero(
    imageUrl: String?,
    isHappeningNow: Boolean,
    onShare: (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
            .background(EventPassColors.BackgroundLight)
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Subtle top gradient so badges/icons stay readable over bright images
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EventPassColors.Black.copy(alpha = 0.18f),
                            EventPassColors.Black.copy(alpha = 0f)
                        ),
                        startY = 0f,
                        endY = 120f
                    )
                )
        )

        if (isHappeningNow) {
            HappeningNowBadge(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(Spacing.md)
            )
        }

        if (onShare != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Spacing.md)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(EventPassColors.White.copy(alpha = 0.92f))
                    .clickable(onClick = onShare),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.IosShare,
                    contentDescription = "Share",
                    tint = EventPassColors.Ink,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun HappeningNowBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(Radii.Pill)
            .background(EventPassColors.White)
            .padding(horizontal = Spacing.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(EventPassColors.HappeningNow)
        )
        Text(
            text = "Happening now",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
    }
}

@Composable
private fun MetaRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EventPassColors.InkMuted,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted
        )
    }
}

@Composable
private fun RatingPill(rating: Double, count: Int?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = EventPassColors.Warning,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "%.1f".format(rating),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = EventPassColors.Ink
        )
        if (count != null) {
            Text(
                text = "($count)",
                style = MaterialTheme.typography.labelMedium,
                color = EventPassColors.InkMuted
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun EventCardPreview() {
    EventPassTheme {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            EventCard(
                title = "Summer Music Festival",
                dateText = "18 Apr 2026 at 15:40",
                venueText = "Kampala Serena Hotel",
                priceText = "UGX 50,000 - 150,000",
                onClick = {},
                isHappeningNow = true,
                isFavorited = false,
                onFavoriteToggle = {},
                onShare = {},
                rating = 4.5,
                ratingCount = 120
            )
            EventCard(
                title = "Tech Innovators Summit 2024",
                dateText = "12 May 2026 at 09:00",
                venueText = "UICC Plaza",
                priceText = "UGX 25,000",
                onClick = {},
                onFavoriteToggle = {},
                rating = 4.2,
                ratingCount = 48
            )
        }
    }
}
