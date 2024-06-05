package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator.grip_type_creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softwareoverflow.maxigriphangboardtrainer.repository.WorkoutRepositoryFactory
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO

class GripTypeViewModelFactory(
    private val context: Context,
    private val GripTypeDTO: GripTypeDTO
) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GripTypeViewModel::class.java)) {
            val repo = WorkoutRepositoryFactory.getInstance(context)

            return GripTypeViewModel(repo, GripTypeDTO.copy()) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
