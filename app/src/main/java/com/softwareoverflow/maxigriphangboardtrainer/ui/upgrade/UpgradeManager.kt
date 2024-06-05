package com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UpgradeManager {

    private val userUpgraded = MutableStateFlow(false)
    val userUpgradedFlow : StateFlow<Boolean> get() = userUpgraded

    fun isUserUpgraded() : Boolean {
        return userUpgraded.value
    }

    fun setUserUpgraded(){
        userUpgraded.value = true
    }
}