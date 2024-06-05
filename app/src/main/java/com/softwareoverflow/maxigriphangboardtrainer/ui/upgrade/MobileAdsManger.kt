package com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.AdapterStatus
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.softwareoverflow.maxigriphangboardtrainer.BuildConfig
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.InAppReviewManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

class MobileAdsManager(val context: Context) : OnInitializationCompleteListener {

    private var isInitialized = false

    fun initialize() {
        if (isInitialized) return

        if (!UpgradeManager.isUserUpgraded())
            MobileAds.initialize(context, this)

        val conf = RequestConfiguration.Builder()
            .setTestDeviceIds(BuildConfig.DEV_DEVICES.asList())
            .build()
        MobileAds.setRequestConfiguration(conf)

        MainScope().launch {
            UpgradeManager.userUpgradedFlow.collect {
                if(it){
                    interstitialAds[WorkoutInterstitial.BEFORE_WORKOUT] = null
                    interstitialAds[WorkoutInterstitial.AFTER_WORKOUT] = null
                }
            }
        }

        isInitialized = true

    }

    override fun onInitializationComplete(p0: InitializationStatus) {
        Timber.i("onInitializationComplete: ${p0.adapterStatusMap.map { "${it.key} - ${it.value.initializationState}" }}")

        if (p0.adapterStatusMap.any { adapter -> adapter.value.initializationState == AdapterStatus.State.READY }) {
            loadInterstitial(context.applicationContext, WorkoutInterstitial.BEFORE_WORKOUT)
            loadInterstitial(context.applicationContext, WorkoutInterstitial.AFTER_WORKOUT)
        }
    }

    companion object {

        private const val retryDelay = 2000L
        private var adLoadAttempts = 0


        enum class WorkoutInterstitial {
            BEFORE_WORKOUT,
            AFTER_WORKOUT
        }

        private var interstitialAds: MutableMap<WorkoutInterstitial, InterstitialAd?> =
            mutableMapOf(
                Pair(WorkoutInterstitial.BEFORE_WORKOUT, null),
                Pair(WorkoutInterstitial.AFTER_WORKOUT, null)
            )

        private var bannerAd: AdView? = null

        var isFirstRun = false

        fun setBannerAd(ad: AdView?) {
            bannerAd?.destroy()
            bannerAd = ad
        }


        fun loadInterstitial(context: Context, adType: WorkoutInterstitial) {
            adLoadAttempts++

            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(
                context.applicationContext,
                context.applicationContext.getString(R.string.ad_id_workout_start),
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        interstitialAds[adType] = null

                        if (adLoadAttempts <= 10 && !UpgradeManager.isUserUpgraded()) {
                            Timber.w("Failed to load interstitial advert on attempt $adLoadAttempts -  ${adError.message}.  \n Retrying in ${adLoadAttempts * retryDelay} milliseconds")

                            MainScope().launch {
                                delay(adLoadAttempts * retryDelay)
                                loadInterstitial(context, adType)
                            }
                        } else {
                            Timber.w("Failed to load interstitial advert. Retry attempts exhausted.")
                        }
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        interstitialAds[adType] = interstitialAd
                        adLoadAttempts = 0
                    }
                })

        }

        fun setInterstitialShown(context: Context, adType: WorkoutInterstitial) {
            interstitialAds[adType] = null

            // Load the next advert
            loadInterstitial(context, adType)
        }


        /**
         * Show the interstitial advert.
         * Returns true if the advert is shown, false otherwise
         */
        private fun showAdvert(
            activity: Activity,
            adType: WorkoutInterstitial,
            onAdClosedCallback: () -> Unit
        ): Boolean {
            if (UpgradeManager.isUserUpgraded())
                return false

            interstitialAds[adType]?.let {
                it.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        onAdClosedCallback()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        onAdClosedCallback()
                    }

                    override fun onAdShowedFullScreenContent() {
                        setInterstitialShown(activity, adType)
                    }
                }

                it.show(activity)
                return true
            }

            return false
        }

        fun showAdBeforeWorkout(activity: Activity?, onAdClosedCallback: () -> Unit) {
            // Don't show ads on first run, and only 50% of the time before workout
            if (isFirstRun || Random.nextBoolean()) {
                onAdClosedCallback()
                return
            }

            if (activity != null) {
                if (!showAdvert(activity, WorkoutInterstitial.BEFORE_WORKOUT, onAdClosedCallback)) {
                    onAdClosedCallback()
                }
            } else {
                onAdClosedCallback()
            }
        }

        fun showAdAfterWorkout(activity: Activity?, onAdClosedCallback: () -> Unit) {
            // Don't show the advert if we're about to ask for a review!
            if (InAppReviewManager.willAskForReview) {
                onAdClosedCallback()
                return
            }

            if (activity != null) {
                if (!showAdvert(activity, WorkoutInterstitial.AFTER_WORKOUT, onAdClosedCallback)) {
                    onAdClosedCallback()
                }
            } else {
                onAdClosedCallback()
            }
        }

        fun pause() {
            bannerAd?.pause()
        }

        fun resume() {
            bannerAd?.resume()
        }

        fun destroy() {
            bannerAd?.destroy()
        }
    }
}
