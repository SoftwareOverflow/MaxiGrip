package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_complete

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.MobileAdsManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.InAppReviewManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.workout.media.WorkoutCompleteMediaManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator.UnsavedChangesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutCompleteViewModel @Inject constructor(
    private val workoutCompleteMediaManager: WorkoutCompleteMediaManager,
    sharedPreferences: SharedPreferences
) : ViewModel() {

    private var isInitialized = false
    private var tryShowAdvert = true

    fun showUnsavedChangesWarning(context: Context) : Boolean {
        return UnsavedChangesViewModel.showWarning(context)
    }

    fun initialize(context: Context, activity: Activity?) {
        if (!isInitialized) {
            isInitialized = true

            playWorkoutCompleteSound(context, activity)
        }
    }

    private fun playWorkoutCompleteSound(context: Context, activity: Activity?) {
        viewModelScope.launch {
            workoutCompleteMediaManager.playWorkoutCompleteSound(onSoundPlayed = {
                activity?.let {
                    showAdvert(context, activity)
                }
            })
        }
    }

    private fun showAdvert(context: Context, activity: Activity?) {
        if (InAppReviewManager.willAskForReview) {
            tryShowAdvert = false
            activity?.let {
                InAppReviewManager.askForReview(context, activity, onFailure = {
                    tryShowAdvert = true // Try and show the advert if we failed to ask for a review
                })
            }
        }

        if (tryShowAdvert) {
            MobileAdsManager.showAdAfterWorkout(activity, onAdClosedCallback = {
                tryShowAdvert = false
            })
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        workoutCompleteMediaManager.cancel()
        super.onCleared()
    }
}