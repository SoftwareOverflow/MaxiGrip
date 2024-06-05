package com.softwareoverflow.maxigriphangboardtrainer.repository.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutSetDTO(
    var gripTypeDTO: GripTypeDTO = GripTypeDTO(),
    var workTime: Int = 7,
    var restTime: Int = 3,
    var numReps: Int = 6,
    var recoverTime: Int = 120,
    var orderInWorkout: Int? = null
) : Parcelable