package com.softwareoverflow.maxigriphangboardtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.preference.PreferenceManager
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LoadWorkoutScreenDestination
import com.softwareoverflow.maxigriphangboardtrainer.repository.billing.BillingRepository
import com.softwareoverflow.maxigriphangboardtrainer.ui.compose.MountainBackground
import com.softwareoverflow.maxigriphangboardtrainer.ui.consent.ConsentManagerGoogle
import com.softwareoverflow.maxigriphangboardtrainer.ui.consent.UserConsentManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.BillingViewModel
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.MobileAdsManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.InAppReviewManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adsManager: MobileAdsManager

    @Inject
    lateinit var billingClient: BillingRepository

    @Inject
    lateinit var bvm: BillingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView // POTENTIAL BUGFIX for https://issuetracker.google.com/issues/37095334 causing intermittent crashing on start.
        super.onCreate(savedInstanceState)


        // The Google SDK seems to load slowly, so start ASAP
        val consentManager = ConsentManagerGoogle.getInstance(this)
        consentManager.handleConsent(this) {
            // We have consent - initialize the required logging and ads
            UserConsentManager.userGaveConsent(this)
        }

        adsManager.initialize()

        setContent {
            AppTheme {

                val appState = rememberAppState()
                val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
                val currentScreen = navBackStackEntry?.destination?.route

                Scaffold(
                    scaffoldState = appState.scaffoldState,
                    snackbarHost = {
                        // reuse default SnackbarHost to have default animation and timing handling
                        SnackbarHost(it) { data ->
                            // custom snackbar with the custom colors
                            Snackbar(
                                actionColor = MaterialTheme.colors.secondary,
                                contentColor = MaterialTheme.colors.onPrimary,
                                backgroundColor = MaterialTheme.colors.primary,
                                snackbarData = data
                            )
                        }
                    }
                )
                { paddingValues ->

                    val mountainsVisible = when (currentScreen) {
                        HomeScreenDestination.route -> true
                        LoadWorkoutScreenDestination.route -> true
                        else -> false
                    }

                    AnimatedVisibility(
                        visible = mountainsVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        MountainBackground(
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                                .aspectRatio(5f, true)
                        )
                    }

                    Box(
                        Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                            .padding(paddingValues)
                    ) {
                        HomeScreenContent(appState)
                    }
                }

            }
        }

        // Create the InAppReviewManager
        InAppReviewManager.createReviewManager(this)

        //bvm.debugConsumePremium()
    }

    override fun onPause() {
        MobileAdsManager.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        if (this::billingClient.isInitialized) billingClient.queryOneTimeProductPurchases()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean("firstRun", true)) {
            MobileAdsManager.isFirstRun = true
            prefs.edit {
                putBoolean("firstRun", false)
            }
        }

        MobileAdsManager.resume()
    }

    override fun onDestroy() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean("firstRun", true)) {
            prefs.edit {
                putBoolean("firstRun", false)
            }
        }

        MobileAdsManager.destroy()

        super.onDestroy()
    }
}


@Composable
private fun HomeScreenContent(appState: AppState) {

    DestinationsNavHost(
        navGraph = NavGraphs.root,
        navController = appState.navController,
    )

}

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreenContent(rememberAppState())
    }
}