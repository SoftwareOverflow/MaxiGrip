package com.softwareoverflow.maxigriphangboardtrainer.repository.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GripTypeDTO(
    val id: Long? = null,
    val name: String? = null,
    val leftHand: HandGripDTO = HandGripDTO(),
    val rightHand: HandGripDTO = HandGripDTO()
) : Parcelable