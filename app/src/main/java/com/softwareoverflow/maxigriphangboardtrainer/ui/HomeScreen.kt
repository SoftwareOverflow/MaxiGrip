package com.softwareoverflow.maxigriphangboardtrainer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.destinations.LoadWorkoutScreenDestination
import com.softwareoverflow.maxigriphangboardtrainer.ui.destinations.SettingsScreenDestination
import com.softwareoverflow.maxigriphangboardtrainer.ui.destinations.UpgradeScreenDestination
import com.softwareoverflow.maxigriphangboardtrainer.ui.destinations.WorkoutCreatorScreenDestination
import com.softwareoverflow.maxigriphangboardtrainer.ui.navigation.NavigationResultActionBasic
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.UpgradeManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AppScreen
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.TopAppRow

@Destination
@RootNavGraph(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<SettingsScreenDestination, NavigationResultActionBasic>
) {

    val userUpgraded by UpgradeManager.userUpgradedFlow.collectAsState()

    AppScreen(topAppRow = {
        TopAppRow(startIcon = {
            Image(
                painter = painterResource(id = R.drawable.icon_foreground),
                contentDescription = null,
                Modifier.padding(end = MaterialTheme.spacing.extraSmall)
            )
        }, title = stringResource(id = R.string.app_name), endIcon = {
            Icon(
                Icons.Filled.Settings,
                contentDescription = stringResource(
                    id = R.string.settings
                ),
                Modifier.clickable { navigator.navigate(SettingsScreenDestination) },
                tint = MaterialTheme.colors.onPrimary
            )
        })
    }, bottomAppRow = null) { modifier ->

        HomeScreenContent(modifier = modifier,
            isUserUpgraded = userUpgraded,
            onCreateNew = {
                navigator.navigate(
                    WorkoutCreatorScreenDestination(
                        WorkoutDTO()
                    )
                )
            }, onLoadWorkout = {
                navigator.navigate(LoadWorkoutScreenDestination)
            }, onUpgrade = {
                navigator.navigate(UpgradeScreenDestination)
            })
    }

    val settingsUpdateMessage = stringResource(id = R.string.settings_saved)
    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {/* Do Nothing */
            }

            is NavResult.Value -> {
                if (result.value == NavigationResultActionBasic.ACTION_POSITIVE) SnackbarManager.showMessage(
                    settingsUpdateMessage
                )
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier,
    isUserUpgraded: Boolean,
    onCreateNew: () -> Unit,
    onLoadWorkout: () -> Unit,
    onUpgrade: () -> Unit
) {
    Column(
        modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.size(MaterialTheme.spacing.extraExtraExtraLarge))

        HomeScreenRow(
            icon = Icons.Filled.AddCircle,
            stringId = R.string.create_new_workout,
            onClick = onCreateNew
        )

        HomeScreenRow(
            icon = Icons.Filled.FolderCopy,
            stringId = R.string.load_saved_workout,
            onClick = onLoadWorkout
        )

        Spacer(Modifier.padding(MaterialTheme.spacing.medium))

        if (!isUserUpgraded) {
            HomeScreenRow(
                icon = Icons.Filled.Upgrade, stringId = R.string.upgrade_to_pro, onClick = onUpgrade
            )
        }
    }
}

@Composable
private fun HomeScreenRow(icon: ImageVector, @StringRes stringId: Int, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(
            onClick = onClick,
            Modifier
                .padding(vertical = MaterialTheme.spacing.medium)
                .fillMaxWidth(0.7f)
                .border(
                    4.dp,
                    MaterialTheme.colors.onPrimary,
                    MaterialTheme.shapes.small
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null)
                Text(
                    stringResource(id = stringId),
                    Modifier.padding(horizontal = MaterialTheme.spacing.small),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}

//@Preview(name = "Tablet", device = Devices.PIXEL_C)
@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun Preview() {
    AppTheme {
        HomeScreen(EmptyDestinationsNavigator, EmptyResultRecipient())
    }
}

@Preview(name = "PhoneDark", device = Devices.PIXEL_4_XL)
@Composable
private fun PreviewDark() {
    AppTheme(darkTheme = true) {
        HomeScreen(EmptyDestinationsNavigator, EmptyResultRecipient())
    }
}