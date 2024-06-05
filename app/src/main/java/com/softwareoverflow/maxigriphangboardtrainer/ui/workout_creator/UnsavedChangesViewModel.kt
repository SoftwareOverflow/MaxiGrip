package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.SharedPreferencesManager

class UnsavedChangesViewModel : ViewModel() {

    fun setDisableWarningState(disableWarning: Boolean, context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext).edit()
            .putBoolean(SharedPreferencesManager.unsavedChangesWarning, !disableWarning)
            .apply()
    }

    companion object {
        fun showWarning(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
                .getBoolean(SharedPreferencesManager.unsavedChangesWarning, true)
        }
    }
}
