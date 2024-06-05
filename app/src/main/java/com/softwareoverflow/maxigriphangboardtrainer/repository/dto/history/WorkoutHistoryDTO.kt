package com.softwareoverflow.maxigriphangboardtrainer.repository.dto.history

import android.os.Parcelable
import com.softwareoverflow.maxigriphangboardtrainer.ui.workout.WorkoutSection
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class WorkoutHistoryDTO(
    var milliseconds: Long, var name: String, val type: WorkoutSection, val date: LocalDate
) : Parcelable