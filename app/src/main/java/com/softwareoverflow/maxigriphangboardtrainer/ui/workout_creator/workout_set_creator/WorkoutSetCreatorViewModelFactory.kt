
package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator.workout_set_creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.softwareoverflow.maxigriphangboardtrainer.repository.WorkoutRepositoryFactory
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO

class WorkoutSetCreatorViewModelFactory(private val workoutSet: WorkoutSetDTO, private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val repo = WorkoutRepositoryFactory.getInstance(context)

        // Making a copy of the workoutSet object here so we don't modify the original in the case of a later cancel / back press
        return WorkoutSetCreatorViewModel(workoutSet.copy(), repo) as T
    }
}
