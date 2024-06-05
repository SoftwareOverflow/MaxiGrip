package com.softwareoverflow.maxigriphangboardtrainer.ui.theme

import android.content.pm.ActivityInfo
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.BackgroundGradient
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.LockScreenOrientation

private val DarkColorScheme = darkColors(
    primary = primaryDark,
    secondary = secondaryDark,
    error = Color.Red,
    onPrimary = Color.White,
    onSurface = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.Black,
    background = Color.Transparent,
    surface = Color.Transparent
)

private val LightColorScheme = lightColors(

    primary = primaryLight,
    secondary = secondaryLight,
    error = Color.Red,
    onPrimary = Color.White,
    onSurface = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.Black,
    background = Color.Transparent,
    surface = Color.Transparent

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            content = {
                /*// A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colors.background
                )*/
                BackgroundGradient{
                    content()
                }
            }
        )
    }
}