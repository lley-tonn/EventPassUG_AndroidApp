package com.eventpass.feature.attendee.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpass.core.design.tokens.EventPassColors
import com.eventpass.core.design.tokens.Radii
import com.eventpass.core.design.tokens.Spacing
import com.eventpass.feature.attendee.profile.components.ProfileRow
import com.eventpass.feature.attendee.profile.components.ProfileRowDivider
import com.eventpass.feature.attendee.profile.components.ProfileRowTrailing

/**
 * Profile data shown at the top of the screen — kept primitive so
 * `:feature:attendee` doesn't depend on the domain `User` type.
 */
data class ProfileHeaderData(
    val fullName: String,
    val roleLabel: String,
    val avatarUrl: String?,
    val email: String?,
    val phoneNumber: String?,
    val versionText: String,
    val isVerified: Boolean = false,
    val followerCount: Int = 0,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val isVerifiedOrganizer: Boolean = false,
    val verifiedOnText: String? = null,
    val switchRoleTargetLabel: String? = null,
    val isSignedIn: Boolean = true
)

/**
 * Profile screen — matches design reference (IMG_2774, IMG_2775).
 *
 * Stateless: caller owns identity + version state and supplies callbacks for
 * each row. Empty contact fields (`email == null` / `phoneNumber == null`)
 * render as "Add …" rows with the orange `+` affordance.
 */
@Composable
fun ProfileScreen(
    data: ProfileHeaderData,
    onVerifyNationalId: () -> Unit,
    onBecomeOrganizer: () -> Unit,
    onSwitchRole: () -> Unit,
    onAddEmail: () -> Unit,
    onAddPhone: () -> Unit,
    onLinkAccounts: () -> Unit,
    onEditProfile: () -> Unit,
    onInterests: () -> Unit,
    onNotifications: () -> Unit,
    onPaymentMethods: () -> Unit,
    onInviteFriends: () -> Unit,
    onRateUs: () -> Unit,
    onSocialClick: (Social) -> Unit,
    onHelpCenter: () -> Unit,
    onContactSupport: () -> Unit,
    onTermsPrivacy: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EventPassColors.BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.xxxl)
    ) {
        ProfileHeader(data = data)

        Spacer(Modifier.height(Spacing.lg))

        SectionLabel("Account")
        GroupedCard {
            if (data.isVerifiedOrganizer) {
                ProfileRow(
                    icon = Icons.Filled.VerifiedUser,
                    title = "Verified Organizer",
                    subtitle = data.verifiedOnText?.let { "Verified on $it" },
                    onClick = { /* no-op */ },
                    iconTint = EventPassColors.Success,
                    trailing = ProfileRowTrailing.None
                )
                ProfileRowDivider()
                ProfileRow(
                    icon = Icons.Filled.SwapHoriz,
                    title = "Switch Role",
                    onClick = onSwitchRole,
                    trailing = data.switchRoleTargetLabel
                        ?.let { ProfileRowTrailing.ValueChevron(it) }
                        ?: ProfileRowTrailing.Chevron
                )
            } else {
                ProfileRow(
                    icon = Icons.Filled.VerifiedUser,
                    title = "Verify National ID (Optional)",
                    onClick = onVerifyNationalId
                )
                ProfileRowDivider()
                ProfileRow(
                    icon = Icons.Filled.BusinessCenter,
                    title = "Become an Organizer",
                    subtitle = "Host events and sell tickets",
                    onClick = onBecomeOrganizer
                )
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        SectionLabel("Contact Information")
        GroupedCard {
            val emailVerified = data.email != null && data.isEmailVerified
            val phoneVerified = data.phoneNumber != null && data.isPhoneVerified

            ProfileRow(
                icon = Icons.Filled.Email,
                title = if (emailVerified) "Email Verified" else (data.email ?: "Add Email Address"),
                subtitle = if (emailVerified) data.email else if (data.email != null) "Email" else null,
                onClick = onAddEmail,
                iconTint = if (emailVerified) EventPassColors.Success else EventPassColors.Primary,
                trailing = when {
                    emailVerified -> ProfileRowTrailing.SuccessCheck
                    data.email == null -> ProfileRowTrailing.AddPlus
                    else -> ProfileRowTrailing.Chevron
                }
            )
            ProfileRowDivider()
            ProfileRow(
                icon = Icons.Filled.Phone,
                title = if (phoneVerified) "Phone Verified" else (data.phoneNumber ?: "Add Phone Number"),
                subtitle = if (phoneVerified) data.phoneNumber else if (data.phoneNumber != null) "Phone" else null,
                onClick = onAddPhone,
                iconTint = if (phoneVerified) EventPassColors.Success else EventPassColors.Primary,
                trailing = when {
                    phoneVerified -> ProfileRowTrailing.SuccessCheck
                    data.phoneNumber == null -> ProfileRowTrailing.AddPlus
                    else -> ProfileRowTrailing.Chevron
                }
            )
            if (!(emailVerified && phoneVerified)) {
                ProfileRowDivider()
                ProfileRow(
                    icon = Icons.Filled.VerifiedUser,
                    title = "Link Accounts",
                    subtitle = "Add more sign-in options",
                    onClick = onLinkAccounts
                )
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        SectionLabel("Settings")
        GroupedCard {
            ProfileRow(
                icon = Icons.Filled.PersonOutline,
                title = "Edit Profile",
                onClick = onEditProfile
            )
            ProfileRowDivider()
            ProfileRow(
                icon = Icons.Filled.AutoAwesome,
                title = "Interests",
                onClick = onInterests
            )
            ProfileRowDivider()
            ProfileRow(
                icon = Icons.Filled.Notifications,
                title = "Notifications",
                onClick = onNotifications
            )
            ProfileRowDivider()
            ProfileRow(
                icon = Icons.Filled.CreditCard,
                title = "Payment Methods",
                onClick = onPaymentMethods
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        SectionLabel("Community")
        GroupedCard {
            ProfileRow(
                icon = Icons.Filled.Group,
                title = "Invite Friends",
                onClick = onInviteFriends,
                trailing = ProfileRowTrailing.IconOnly(Icons.Filled.Share)
            )
            ProfileRowDivider()
            ProfileRow(
                icon = Icons.Filled.Star,
                title = "Rate Us",
                onClick = onRateUs
            )
        }

        Spacer(Modifier.height(Spacing.md))
        SocialRow(onClick = onSocialClick)

        Spacer(Modifier.height(Spacing.lg))

        SectionLabel("Support")
        GroupedCard {
            ProfileRow(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = "Help Center",
                onClick = onHelpCenter
            )
            ProfileRowDivider()
            ProfileRow(
                icon = Icons.Filled.MailOutline,
                title = "Contact Support",
                onClick = onContactSupport
            )
            ProfileRowDivider()
            ProfileRow(
                icon = Icons.Filled.Description,
                title = "Terms & Privacy",
                onClick = onTermsPrivacy
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        GroupedCard {
            ProfileRow(
                icon = Icons.Filled.Description,
                title = "Version",
                onClick = { /* no-op */ },
                iconTint = EventPassColors.InkMuted,
                trailing = ProfileRowTrailing.Value(data.versionText)
            )
        }

        Spacer(Modifier.height(Spacing.sm))

        GroupedCard {
            if (data.isSignedIn) {
                ProfileRow(
                    icon = Icons.AutoMirrored.Filled.Login,
                    title = "Sign Out",
                    onClick = onSignOut,
                    iconTint = EventPassColors.Error,
                    titleColor = EventPassColors.Error,
                    trailing = ProfileRowTrailing.IconOnly(Icons.AutoMirrored.Filled.ExitToApp)
                )
            } else {
                ProfileRow(
                    icon = Icons.AutoMirrored.Filled.Login,
                    title = "Sign In",
                    onClick = onSignOut,
                    iconTint = EventPassColors.Primary,
                    titleColor = EventPassColors.Primary,
                    trailing = ProfileRowTrailing.Chevron
                )
            }
        }
    }
}

// MARK: - Header

@Composable
private fun ProfileHeader(data: ProfileHeaderData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(EventPassColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            // TODO: render Coil AsyncImage when avatarUrl != null
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = EventPassColors.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = data.fullName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = EventPassColors.Ink,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (data.isVerified) {
                    Spacer(Modifier.width(Spacing.xs))
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = "Verified",
                        tint = EventPassColors.Success,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Group,
                    contentDescription = null,
                    tint = EventPassColors.InkMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text = "${data.followerCount} followers",
                    style = MaterialTheme.typography.labelMedium,
                    color = EventPassColors.InkMuted
                )
                Text(
                    text = "  •  ",
                    style = MaterialTheme.typography.labelMedium,
                    color = EventPassColors.InkSubtle
                )
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = EventPassColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = data.roleLabel,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = EventPassColors.Primary
                )
            }
        }
    }
}

// MARK: - Grouped card + section label

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = EventPassColors.InkMuted,
        modifier = Modifier.padding(start = Spacing.xl, bottom = Spacing.xs)
    )
}

@Composable
private fun GroupedCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl)
            .clip(Radii.CardLarge)
            .background(EventPassColors.White)
    ) {
        content()
    }
}

// MARK: - Social row

enum class Social { TikTok, Instagram, X, Facebook, Website }

@Composable
private fun SocialRow(onClick: (Social) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialIcon(Icons.Filled.MusicNote, "TikTok") { onClick(Social.TikTok) }
        SocialIcon(Icons.Filled.PhotoCamera, "Instagram") { onClick(Social.Instagram) }
        SocialIcon(Icons.Filled.Close, "X") { onClick(Social.X) }
        SocialIcon(Icons.Filled.Facebook, "Facebook") { onClick(Social.Facebook) }
        SocialIcon(Icons.Filled.Language, "Website") { onClick(Social.Website) }
    }
}

@Composable
private fun SocialIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(EventPassColors.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = EventPassColors.Ink,
            modifier = Modifier.size(20.dp)
        )
    }
}
