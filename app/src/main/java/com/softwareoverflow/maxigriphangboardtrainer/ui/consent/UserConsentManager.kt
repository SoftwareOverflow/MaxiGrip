package com.softwareoverflow.maxigriphangboardtrainer.ui.consent

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.SharedPreferencesManager

class UserConsentManager {

    companion object {
        fun userGaveConsent(context: Context) {
            setAnalytics(context, true)
        }


        fun setAnalytics(context: Context, boolean: Boolean) {
            FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(boolean)

            PreferenceManager.getDefaultSharedPreferences(context.applicationContext).edit()
                .putBoolean(SharedPreferencesManager.analyticsEnabled, boolean)
                .apply()
        }

    }
}