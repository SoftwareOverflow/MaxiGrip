package com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade

import android.app.Activity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpgradeViewModel @Inject constructor(private val billingViewModel: BillingViewModel) : ViewModel() {

    fun upgrade(activity: Activity?) {
        activity?.let {
            billingViewModel.purchasePro(activity)
        }
    }

}