package com.eventpass.feature.attendee.profile.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Spacing

/**
 * Trailing affordance shown at the right edge of a profile row.
 */
sealed interface ProfileRowTrailing {
    /** Default chevron (>). */
    data object Chevron : ProfileRowTrailing
    /** Orange `+` circle — used for empty contact fields. */
    data object AddPlus : ProfileRowTrailing
    /** Arbitrary trailing icon. */
    data class IconOnly(val icon: ImageVector) : ProfileRowTrailing
    /** Plain right-aligned value text (no chevron). */
    data class Value(val text: String) : ProfileRowTrailing
    /** Right-aligned value text followed by a chevron (e.g. "To Attendee ›"). */
    data class ValueChevron(val text: String) : ProfileRowTrailing
    /** Green filled circle with a white check — used for verified contact rows. */
    data object SuccessCheck : ProfileRowTrailing
    /** Nothing on the right. */
    data object None : ProfileRowTrailing
}

/**
 * One settings-style row inside a grouped card. Title + optional subtitle,
 * a leading icon, and a configurable trailing affordance. Set [titleColor]
 * and [iconTint] for destructive actions (Sign Out).
 */
@Composable
fun ProfileRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    iconTint: Color = EventPassColors.Primary,
    titleColor: Color = EventPassColors.Ink,
    trailing: ProfileRowTrailing = ProfileRowTrailing.Chevron
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = EventPassColors.InkMuted
                )
            }
        }
        TrailingAffordance(trailing, titleColor)
    }
}

@Composable
private fun TrailingAffordance(trailing: ProfileRowTrailing, titleColor: Color) {
    when (trailing) {
        ProfileRowTrailing.Chevron -> Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = EventPassColors.InkSubtle,
            modifier = Modifier.size(20.dp)
        )
        ProfileRowTrailing.AddPlus -> Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(EventPassColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = EventPassColors.White,
                modifier = Modifier.size(14.dp)
            )
        }
        is ProfileRowTrailing.IconOnly -> Icon(
            imageVector = trailing.icon,
            contentDescription = null,
            tint = EventPassColors.InkMuted,
            modifier = Modifier.size(18.dp)
        )
        is ProfileRowTrailing.Value -> Text(
            text = trailing.text,
            style = MaterialTheme.typography.bodyMedium,
            color = EventPassColors.InkMuted
        )
        is ProfileRowTrailing.ValueChevron -> Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = trailing.text,
                style = MaterialTheme.typography.bodyMedium,
                color = EventPassColors.InkMuted
            )
            Spacer(Modifier.width(Spacing.xs))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = EventPassColors.InkSubtle,
                modifier = Modifier.size(20.dp)
            )
        }
        ProfileRowTrailing.SuccessCheck -> Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(EventPassColors.Success),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = EventPassColors.White,
                modifier = Modifier.size(15.dp)
            )
        }
        ProfileRowTrailing.None -> {}
    }
}

/**
 * Thin divider between rows inside a grouped card.
 */
@Composable
fun ProfileRowDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.lg + 20.dp + Spacing.md),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(EventPassColors.DividerLight)
        )
    }
}
