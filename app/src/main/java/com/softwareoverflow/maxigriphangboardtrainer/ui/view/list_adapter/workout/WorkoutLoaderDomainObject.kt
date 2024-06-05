package com.softwareoverflow.maxigriphangboardtrainer.ui.view.list_adapter.workout

import android.content.Context
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO

data class WorkoutLoaderDomainObject(
    val dto: WorkoutDTO,
    val type: WorkoutLoaderDomainObjectType = WorkoutLoaderDomainObjectType.USER
) {
    companion object {


        fun getPlaceholderUnlocked(context: Context) =
            WorkoutLoaderDomainObject(
                // The WorkoutDTO id is used as a key when loading workouts, hence the id in this
                WorkoutDTO(id = -1L, name = context.getString(R.string.workout_slot_free)),
                WorkoutLoaderDomainObjectType.PLACEHOLDER_UNLOCKED
            )

        fun getPlaceholderLocked(context: Context) =
            WorkoutLoaderDomainObject(
                // The WorkoutDTO id is used as a key when loading workouts, hence the id in this
                WorkoutDTO(id = -2L, name = context.getString(R.string.workout_slot_locked)),
                WorkoutLoaderDomainObjectType.PLACEHOLDER_LOCKED
            )
    }
}

/**
 * Enum types for the saved workout.
 * USER -> User created
 * PLACEHOLDER_UNLOCKED -> A placeholder showing an unlocked slot
 * PLACEHOLDER_LOCKED -> A placeholder showing a locked slot (upgrade to PRO version)
 */
enum class WorkoutLoaderDomainObjectType { USER, PLACEHOLDER_UNLOCKED, PLACEHOLDER_LOCKED }