package com.softwareoverflow.maxigriphangboardtrainer.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val default: Dp = 0.dp,
    val extraExtraSmall: Dp = 2.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 32.dp,
    val extraLarge: Dp = 48.dp,
    val extraExtraLarge: Dp = 64.dp,
    val extraExtraExtraLarge: Dp = 128.dp,
    val extraExtraExtraExtraLarge: Dp = 256.dp,
)

/*val TabletSpacing = Spacing(
    extraExtraSmall = 8.dp,
    extraSmall = 16.dp,
    small = 32.dp,
    medium = 64.dp,
    large = 96.dp,
    extraLarge = 128.dp,
    extraExtraLarge = 160.dp,
    extraExtraExtraLarge = 256.dp
)*/

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable @ReadOnlyComposable get() = LocalSpacing.current