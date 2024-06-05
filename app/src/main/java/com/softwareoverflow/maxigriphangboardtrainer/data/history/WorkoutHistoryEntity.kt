package com.softwareoverflow.maxigriphangboardtrainer.data.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.softwareoverflow.maxigriphangboardtrainer.ui.workout.WorkoutSection
import java.time.LocalDate

@Entity(tableName = "workout_history", primaryKeys = ["_name", "_type", "_date_string"])
data class WorkoutHistoryEntity(

    @ColumnInfo(name = "_seconds")
    var seconds: Int = 0,

    @ColumnInfo(name = "_name")
    var name: String = "",

    @ColumnInfo(name = "_type")
    var type: WorkoutSection = WorkoutSection.HANG,

    @ColumnInfo(name = "_date_string")
    var date: LocalDate = LocalDate.now() // This will be converted by the converter to a string in the form "yyyyMMdd"
    )