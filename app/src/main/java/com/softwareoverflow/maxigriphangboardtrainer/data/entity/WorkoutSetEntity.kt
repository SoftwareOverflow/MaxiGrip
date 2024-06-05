package com.softwareoverflow.maxigriphangboardtrainer.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "WorkoutSet",
    primaryKeys = ["workoutId", "orderInWorkout"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
class WorkoutSetEntity(
    // The composite primary key is comprised on workoutId and orderInWorkout as no 2 entities can be related to the same workout and have the same position in said workout
    var workoutId: Long,
    var orderInWorkout: Int,

    var workTime: Int,
    var restTime: Int,
    var numReps: Int,
    var recoverTime: Int,

    @Embedded
    var gripType: GripTypeEntity
)