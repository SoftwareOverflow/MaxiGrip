package com.softwareoverflow.maxigriphangboardtrainer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Workout")
class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    var name: String = "",
    var numReps: Int = 1,
    var recoveryTime: Int = 120
)