package com.softwareoverflow.maxigriphangboardtrainer.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class NavigationResultActionBasic {
    CANCELLED, ACTION_POSITIVE
}

@Parcelize
data class NavigationSaveResult (val id: Long, val name: String) : Parcelable