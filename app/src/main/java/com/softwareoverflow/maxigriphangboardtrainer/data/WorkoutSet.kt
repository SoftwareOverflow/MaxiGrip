package com.softwareoverflow.maxigriphangboardtrainer.data

import androidx.room.Embedded
import androidx.room.Relation
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.GripTypeEntity
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.WorkoutSetEntity

data class WorkoutSet(
    @Embedded var workoutSet: WorkoutSetEntity,

    @Relation(
        parentColumn = "workoutSetId",
        entityColumn = "id",
        entity = GripTypeEntity::class
    ) var gripType: GripTypeEntity
)