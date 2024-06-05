package com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.AdvertView
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.MobileAdsManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.UpgradeManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.bannerAdSize
import kotlinx.coroutines.launch

@Composable
private fun getGradient(): List<Color> {
    return if (isSystemInDarkTheme()) listOf(
        Color(0xFF0E4875), Color(0xFF09A796)
    ) else listOf(
        Color(0xFF09A796), Color(0xFF00D1FF)
    )
}

@Composable
fun BackgroundGradient(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = getGradient()
                )
            )
    ) {
        content()
    }
}
@Composable
fun Modifier.horizontalGradient(shape: Shape) =
    background(
        brush = Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colors.primary,
                MaterialTheme.colors.primary.copy(alpha = 0.4f),
                MaterialTheme.colors.primary.copy(alpha = 0f)
            )
        ), shape = shape
    )

@Composable
fun Modifier.verticalBackground(shape: Shape) =
    background(
        Brush.verticalGradient(
            listOf(
                MaterialTheme.colors.primary,
                MaterialTheme.colors.primary.copy(alpha = 0.4f),
                MaterialTheme.colors.primary.copy(alpha = 0f)
            )
        ), shape
    )

@Composable
fun AppScreen(
    topAppRow: @Composable () -> Unit,
    bottomAppRow: @Composable (() -> Unit)?,
    snackbarMessage: SnackbarMessage? = null,
    showBannerAd: Boolean = false,
    content: @Composable (modifier: Modifier) -> Unit
) {
    val state = rememberScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var loadingAdvert by remember { mutableStateOf(true) }

    val isUserUpgraded = UpgradeManager.userUpgradedFlow.collectAsState()

    Scaffold(topBar = {
        Column {

            topAppRow()

            if (showBannerAd && !isUserUpgraded.value) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(bannerAdSize.height.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (loadingAdvert) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.loading_advert))
                            LinearProgressIndicator()
                        }
                    }

                    AdvertView {
                        loadingAdvert = false
                    }
                }
            } else {
                loadingAdvert = false

                MobileAdsManager.setBannerAd(null)
            }
        }
    },
        bottomBar = { if (bottomAppRow != null) bottomAppRow() },
        scaffoldState = state,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValue ->
        content(
            Modifier
                .padding(paddingValue)
                .padding(MaterialTheme.spacing.extraSmall)
        )
    }

    LaunchedEffect(snackbarHostState) {
        snackbarMessage?.let { snackbarDetails ->
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    snackbarDetails.message, snackbarDetails.actionLabel, snackbarDetails.duration
                )

                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        snackbarMessage.onAction?.invoke()
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }

                    SnackbarResult.Dismissed -> {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarDetails.onDismiss()
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppRow(
    startIcon: ImageVector,
    onStartPressed: () -> Unit,
    title: String,
    endIcon: ImageVector? = null,
    onEndIconPressed: (() -> Unit)? = null
) {
    TopAppRow(startIcon = {
        Icon(
            startIcon,
            contentDescription = startIcon.name,
            Modifier.clickable { onStartPressed() },
            tint = MaterialTheme.colors.onPrimary
        )
    }, title = title, endIcon = {
        if (endIcon != null) {
            val modifier = if (onEndIconPressed != null) Modifier.clickable { onEndIconPressed() }
            else Modifier

            Icon(
                endIcon,
                contentDescription = endIcon.name,
                modifier,
                tint = MaterialTheme.colors.onPrimary
            )
        }
    })
}

@Composable
fun TopAppRow(
    startIcon: @Composable () -> Unit, title: String, endIcon: @Composable (() -> Unit)?
) {
    Surface(contentColor = MaterialTheme.colors.onBackground) {
        Row(
            Modifier
                .height(MaterialTheme.spacing.extraLarge)
                .background(MaterialTheme.colors.primary)
                .padding(MaterialTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {

            startIcon()
            Text(
                title,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis
            )
            if (endIcon != null) endIcon()
        }
    }
}


@Composable
fun BottomAppRow(
    height: Dp? = null,
    content: @Composable RowScope.() -> Unit
) {
    Surface(contentColor = MaterialTheme.colors.onPrimary) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(height ?: MaterialTheme.spacing.extraExtraLarge)
                .padding(MaterialTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Preview(name = "Tablet", device = Devices.PIXEL_C)
@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun Preview() {
    AppTheme {
        AppScreen(topAppRow = {
            TopAppRow(
                startIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onStartPressed = { },
                title = "Example Screen Top Bar"
            )
        }, bottomAppRow = {
            BottomAppRow {
                Text("Unmodified FAB", Modifier.weight(1f))

                FloatingActionButton(onClick = { }) {
                    Icon(Icons.Filled.Check, contentDescription = null)
                }
            }
        }, showBannerAd = true
        ) { modifier ->
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Text("This is an example screen")
            }
        }
    }
}
