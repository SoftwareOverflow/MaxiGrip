package com.softwareoverflow.maxigriphangboardtrainer.ui.settings

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.softwareoverflow.maxigriphangboardtrainer.BuildConfig
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.ui.SnackbarManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.consent.ConsentManagerGoogle
import com.softwareoverflow.maxigriphangboardtrainer.ui.navigation.NavigationResultActionBasic
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AppScreen
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.CircleCheckbox
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.TopAppRow
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.findActivity

@Destination
@Composable
fun SettingsScreen(
    resultBackNavigator: ResultBackNavigator<NavigationResultActionBasic>,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val prepSetEnabled = viewModel.prepSetEnabled.collectAsState()
    val prepSetTime = viewModel.prepSetDuration.collectAsState()
    val finalSeconds = viewModel.finalSeconds.collectAsState()
    val analytics = viewModel.analytics.collectAsState()

    val context = LocalContext.current
    val consentManager = ConsentManagerGoogle.getInstance(context)

    BackHandler {
        viewModel.saveSettings(context) {
            resultBackNavigator.navigateBack(NavigationResultActionBasic.ACTION_POSITIVE)
        }
    }

    AppScreen(topAppRow = {
        TopAppRow(
            startIcon = Icons.AutoMirrored.Filled.ArrowBack, onStartPressed = {
                viewModel.saveSettings(context) {
                    resultBackNavigator.navigateBack(NavigationResultActionBasic.CANCELLED)
                }
            }, title = stringResource(id = R.string.settings)
        )

    }, bottomAppRow = null) { modifier ->

        val consentInformation: ConsentInformation =
            UserMessagingPlatform.getConsentInformation(LocalContext.current)

        val isPrivacyOptionsRequired =
            consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

        SettingsScreenContent(modifier = modifier,
            prepSetEnabled.value,
            prepSetTime.value,
            finalSeconds.value.toMutableSet(),
            analytics.value,
            requirePrivacyOptions = isPrivacyOptionsRequired,
            onPrepSetChange = viewModel::onPrepSetEnabledChange,
            onPrepDurationChange = viewModel::onPrepDurationChange,
            onFinalSecondsChange = viewModel::onFinalSecondsChange,
            onAnalyticsChange = viewModel::onAnalyticsChange,
            openPrivacyOptions = {
                val activity = context.findActivity()

                var privacyOptionError = activity == null
                activity?.let {
                    consentManager.showPrivacyOptionsForm(activity) {
                        it?.let {
                            privacyOptionError = true
                        }
                    }
                }

                if (privacyOptionError) {
                    SnackbarManager.showMessage(context.getString(R.string.unexpected_problem_try_again))
                }
            })
    }
}


@Composable
private fun SettingsScreenContent(
    modifier: Modifier,
    prepSetEnabled: Boolean,
    prepDuration: Int,
    finalSeconds: MutableSet<String>,
    analytics: Boolean,
    requirePrivacyOptions: Boolean,
    onPrepSetChange: (Boolean) -> Unit,
    onPrepDurationChange: (Int) -> Unit,
    onFinalSecondsChange: (Set<String>) -> Unit,
    onAnalyticsChange: (Boolean) -> Unit,
    openPrivacyOptions: () -> Unit,
) {
    Column(modifier.fillMaxSize()) {
        SettingsSwitch(title = R.string.preparation_set_enabled,
            subtitle = R.string.preparation_set_summary,
            isChecked = prepSetEnabled,
            onCheckChange = { onPrepSetChange(it) })


        AnimatedVisibility(visible = prepSetEnabled) {
            Column {
                Text(
                    stringResource(id = R.string.preparation_set_time),
                    style = MaterialTheme.typography.body1
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    SettingsCheckbox(title = "3s",
                        isChecked = prepDuration == 3,
                        onCheckChange = { onPrepDurationChange(3) })

                    SettingsCheckbox(title = "5s",
                        isChecked = prepDuration == 5,
                        onCheckChange = { onPrepDurationChange(5) })

                    SettingsCheckbox(title = "10s",
                        isChecked = prepDuration == 10,
                        onCheckChange = { onPrepDurationChange(10) })
                }
            }
        }

        Text(
            stringResource(id = R.string.final_seconds_beep), style = MaterialTheme.typography.body1
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            SettingsCheckbox(title = "5s", isChecked = finalSeconds.contains("5"), onCheckChange = {
                if (it) finalSeconds.add("5")
                else finalSeconds.remove("5")

                onFinalSecondsChange(finalSeconds)
            })

            SettingsCheckbox(title = "10s",
                isChecked = finalSeconds.contains("10"),
                onCheckChange = {
                    if (it) finalSeconds.add("10")
                    else finalSeconds.remove("10")

                    onFinalSecondsChange(finalSeconds)
                })

            SettingsCheckbox(title = "15s",
                isChecked = finalSeconds.contains("15"),
                onCheckChange = {
                    if (it) finalSeconds.add("15")
                    else finalSeconds.remove("15")

                    onFinalSecondsChange(finalSeconds)
                })
        }

        SettingsSwitch(title = R.string.analytics_enabled,
            subtitle = R.string.analytics_enabled_summary,
            isChecked = analytics,
            onCheckChange = {
                onAnalyticsChange(it)
            })

        if (requirePrivacyOptions) {
            val activity = LocalContext.current.findActivity()
            activity?.let {
                Text(text = stringResource(id = R.string.manage_privacy_gdpr),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.clickable {
                        openPrivacyOptions()
                    })
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "${stringResource(R.string.version)} ${BuildConfig.VERSION_NAME}"
        )
    }
}

@Composable
private fun SettingsSwitch(
    @StringRes title: Int,
    @StringRes subtitle: Int?,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(stringResource(id = title), style = MaterialTheme.typography.body1)

            subtitle?.let {
                Text(stringResource(id = it), style = MaterialTheme.typography.caption)
            }
        }

        Switch(checked = isChecked, onCheckedChange = onCheckChange)
    }
}

@Composable
private fun SettingsCheckbox(
    title: String,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.clickable { onCheckChange(true) }, verticalAlignment = Alignment.CenterVertically
    ) {
        CircleCheckbox(checked = isChecked, onCheckedChange = onCheckChange)
        Text(title, style = MaterialTheme.typography.subtitle1)
    }
}


@Composable
@Preview
private fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreenContent(modifier = Modifier,
            true,
            10,
            mutableSetOf("10", "15"),
            true,
            true,
            {},
            {},
            {},
            {},
            {})
    }
}

