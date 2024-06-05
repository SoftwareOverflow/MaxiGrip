package com.softwareoverflow.maxigriphangboardtrainer.repository.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutDTO (
    var id: Long? = null,
    var name: String = "",
    var workoutSets: MutableList<WorkoutSetDTO> = ArrayList(),
    var numReps: Int = 1,
    var recoveryTime: Int = 120
) : Parcelable