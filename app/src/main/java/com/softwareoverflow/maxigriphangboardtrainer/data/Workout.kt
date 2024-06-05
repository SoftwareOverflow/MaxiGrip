package com.softwareoverflow.maxigriphangboardtrainer.data

import androidx.room.Embedded
import androidx.room.Relation
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.WorkoutEntity
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.WorkoutSetEntity

class Workout(
    @Embedded var workout: WorkoutEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId",
        entity = WorkoutSetEntity::class
    ) var workoutSets: List<WorkoutSetEntity>
)