package com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.softwareoverflow.maxigriphangboardtrainer.R

// Custom colors used in the app
private val colorWorkDark = Color(255, 120, 78, 255)
private val colorRestDark = Color(169, 136, 242, 255)
private val colorRepeatDark = Color(82, 211, 88, 255)
private val colorRecoverDark = Color(130, 228, 255, 255)

private val colorWorkLight = Color(255, 141, 76, 255)
private val colorRestLight = Color(170, 17, 251, 255)
private val colorRepeatLight = Color(111, 255, 118, 255)
private val colorRecoverLight = Color(6, 112, 246, 255)

@Composable
fun ColouredIcon(icon: ColouredIcon, modifier: Modifier = Modifier) {
    icon.let {
        Icon(
            painterResource(id = it.id),
            contentDescription = stringResource(id = it.contentDesc),
            tint = if (isSystemInDarkTheme()) it.colorDark else it.colorLight,
            modifier = modifier,
        )
    }
}

enum class ColouredIcon(
    @DrawableRes val id: Int,
    val colorLight: Color,
    val colorDark: Color,
    @StringRes val contentDesc: Int
) {
    WORK(
        R.drawable.icon_fire,
        colorWorkLight,
        colorWorkDark,
        R.string.content_desc_work_time
    ),
    REST(
        R.drawable.icon_rest,
        colorRestLight,
        colorRestDark,
        R.string.content_desc_rest_time
    ),
    REPEAT(
        R.drawable.icon_repeat, colorRepeatLight, colorRepeatDark, R.string.content_desc_num_reps
    ),
    RECOVER(
        R.drawable.icon_recover,
        colorRecoverLight,
        colorRecoverDark,
        R.string.content_desc_recover_time
    ),
    PREPARE(
        R.drawable.icon_foreground, Color.White, Color.White, R.string.content_desc_prepare
    )
}