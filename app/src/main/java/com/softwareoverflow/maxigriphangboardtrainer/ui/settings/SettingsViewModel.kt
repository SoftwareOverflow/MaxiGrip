package com.softwareoverflow.maxigriphangboardtrainer.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.consent.UserConsentManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    private val _prepSetEnabled = MutableStateFlow(
        sharedPrefs.getBoolean(
            SharedPreferencesManager.prepSetEnabled, true
        )
    )
    val prepSetEnabled: StateFlow<Boolean> get() = _prepSetEnabled

    private val _prepSetDuration= MutableStateFlow(sharedPrefs.getString(
        SharedPreferencesManager.prepSetTime, "5"
    )?.toInt() ?: 5)
    val prepSetDuration: StateFlow<Int> get() = _prepSetDuration


    private val _finalSeconds = MutableStateFlow(
        sharedPrefs.getStringSet(
            SharedPreferencesManager.finalSecondsVocal,
            setOf("5", "10", "15")
        )!!.toMutableSet()
    )
    val finalSeconds: StateFlow<Set<String>> get() = _finalSeconds

    private val _personalAds = MutableStateFlow(
        sharedPrefs.getBoolean(
            SharedPreferencesManager.personalAds,
            true
        )
    )

    private val _analytics = MutableStateFlow(
        sharedPrefs.getBoolean(
            SharedPreferencesManager.analyticsEnabled,
            true
        )
    )
    val analytics: StateFlow<Boolean> get() = _analytics


    fun onPrepSetEnabledChange(enabled: Boolean) {
        _prepSetEnabled.value = enabled
    }

    fun onPrepDurationChange(value: Int) {
        _prepSetDuration.value = value
    }

    fun onFinalSecondsChange(value: Set<String>) {
        _finalSeconds.value = value.toMutableSet()
    }

    fun onPersonalAdsChange(enabled: Boolean) {
        _personalAds.value = enabled
    }

    fun onAnalyticsChange(enabled: Boolean) {
        _analytics.value = enabled
    }


    fun saveSettings(context: Context, onUpdateComplete: () -> Unit) {
        sharedPrefs.edit()
            .putBoolean(
                SharedPreferencesManager.prepSetEnabled,
                prepSetEnabled.value
            )
            .putString(SharedPreferencesManager.prepSetTime, prepSetDuration.value.toString())
            .putStringSet(SharedPreferencesManager.finalSecondsVocal, finalSeconds.value)
            .putBoolean(SharedPreferencesManager.analyticsEnabled, analytics.value)
            .apply()

        UserConsentManager.setAnalytics(context, analytics.value)

        onUpdateComplete()
    }
}