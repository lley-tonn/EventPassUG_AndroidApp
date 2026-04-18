package com.eventpass.core.design.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Corner radius tokens. iOS uses a narrow set of soft curves — 12 / 16 / 20 / 28. */
object Radii {
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 20.dp
    val xl: Dp = 28.dp
    val pill: Dp = 999.dp

    val Card = RoundedCornerShape(md)
    val CardLarge = RoundedCornerShape(lg)
    val Button = RoundedCornerShape(md)
    val Field = RoundedCornerShape(md)
    val Pill = RoundedCornerShape(pill)
    val Sheet = RoundedCornerShape(topStart = xl, topEnd = xl, bottomStart = 0.dp, bottomEnd = 0.dp)
}
