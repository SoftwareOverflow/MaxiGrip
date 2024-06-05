package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softwareoverflow.maxigriphangboardtrainer.repository.WorkoutRepositoryFactory
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO

class WorkoutCreatorViewModelFactory(val context: Context, val dto: WorkoutDTO) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(WorkoutCreatorViewModel::class.java)) {

            val repo = WorkoutRepositoryFactory.getInstance(context)


            return WorkoutCreatorViewModel(repo, dto, UnsavedChangesViewModel.showWarning(context)) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
