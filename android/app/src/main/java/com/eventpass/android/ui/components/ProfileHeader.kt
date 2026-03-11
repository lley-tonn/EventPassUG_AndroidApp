package com.eventpass.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eventpass.android.domain.models.User
import com.eventpass.android.domain.models.UserRole
import com.eventpass.android.ui.theme.EventPassColors
import com.eventpass.android.ui.theme.EventPassDimensions

/**
 * Compact profile header with avatar, name, followers, and role.
 * Migrated from iOS UI/Components/ProfileHeaderView.swift
 *
 * SwiftUI → Compose mapping:
 * - @ViewBuilder → @Composable
 * - HStack/VStack → Row/Column
 * - Image(systemName:) → Icon
 * - AsyncImage with Coil
 */
@Composable
fun CompactProfileHeader(
    user: User?,
    followerCount: Int = 0,
    onAvatarTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val displayRole = user?.currentActiveRole ?: UserRole.ATTENDEE
    val isOrganizer = user?.isOrganizer == true
    val isVerified = user?.isVerified == true
    val roleColor = getRoleColor(displayRole)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        ProfileAvatar(
            imageUrl = user?.profileImageUrl,
            size = 56.dp,
            roleColor = roleColor,
            onClick = onAvatarTap
        )

        // Name and metadata
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Name with verification badge
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user?.fullName ?: "Guest",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isVerified) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        modifier = Modifier.size(16.dp),
                        tint = EventPassColors.Success
                    )
                }
            }

            // Follower count + Role on same line
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isOrganizer) {
                    // Follower count
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatFollowerCount(followerCount),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Bullet separator
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Role badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (displayRole == UserRole.ORGANIZER) {
                            Icons.Default.Star
                        } else {
                            Icons.Default.Person
                        },
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = roleColor
                    )
                    Text(
                        text = displayRole.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = roleColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * Centered profile header for profile/settings screens.
 */
@Composable
fun CenteredProfileHeader(
    user: User?,
    followerCount: Int = 0,
    onAvatarTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val displayRole = user?.currentActiveRole ?: UserRole.ATTENDEE
    val isOrganizer = user?.isOrganizer == true
    val isVerified = user?.isVerified == true
    val roleColor = getRoleColor(displayRole)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EventPassDimensions.Spacing.sm)
    ) {
        // Avatar
        ProfileAvatar(
            imageUrl = user?.profileImageUrl,
            size = 72.dp,
            roleColor = roleColor,
            onClick = onAvatarTap
        )

        // Name with verification badge
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user?.fullName ?: "Guest",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (isVerified) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified",
                    modifier = Modifier.size(16.dp),
                    tint = EventPassColors.Success
                )
            }
        }

        // Follower count + Role on same line
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isOrganizer) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatFollowerCount(followerCount),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (displayRole == UserRole.ORGANIZER) {
                        Icons.Default.Star
                    } else {
                        Icons.Default.Person
                    },
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = roleColor
                )
                Text(
                    text = displayRole.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = roleColor
                )
            }
        }
    }
}

/**
 * Profile avatar with placeholder.
 */
@Composable
fun ProfileAvatar(
    imageUrl: String?,
    size: androidx.compose.ui.unit.Dp,
    roleColor: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile picture",
                modifier = Modifier.size(size),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(size)
                    .background(roleColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(size / 2),
                    tint = roleColor
                )
            }
        }
    }
}

/**
 * Get role-specific color.
 */
private fun getRoleColor(role: UserRole): Color {
    return when (role) {
        UserRole.ORGANIZER -> EventPassColors.OrganizerPrimary
        UserRole.ATTENDEE -> EventPassColors.Primary
    }
}

/**
 * Format follower count for display.
 */
private fun formatFollowerCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM followers", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK followers", count / 1_000.0)
        count == 1 -> "1 follower"
        else -> "$count followers"
    }
}
