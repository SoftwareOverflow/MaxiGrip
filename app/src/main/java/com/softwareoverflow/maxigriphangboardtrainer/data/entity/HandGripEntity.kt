/*
package com.softwareoverflow.maxigriphangboardtrainer.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "HandGrip", primaryKeys = ["gripTypeId", "handGripType"], foreignKeys = [
        ForeignKey(
            entity = GripTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["gripTypeId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
class HandGripEntity(
    var gripTypeId: Long = 0,
    var handGripType: HandGripType,
    var thumb: Boolean,
    var index: Boolean,
    var middle: Boolean,
    var ring: Boolean,
    var little: Boolean
)

enum class HandGripType {
    LEFT, RIGHT
}*/
