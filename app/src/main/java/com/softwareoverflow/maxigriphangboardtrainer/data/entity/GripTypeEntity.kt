package com.softwareoverflow.maxigriphangboardtrainer.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.HandGripDTO


@Entity(
    tableName = "GripType"
)
class GripTypeEntity(
    @PrimaryKey(autoGenerate = true) val gripTypeId: Long = 0,

    var name: String,

    @Embedded(prefix = "left_") val leftHand: HandGripDTO,
    @Embedded(prefix = "right_") val rightHand: HandGripDTO
)