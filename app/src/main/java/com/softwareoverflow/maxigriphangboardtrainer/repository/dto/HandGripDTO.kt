package com.softwareoverflow.maxigriphangboardtrainer.repository.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HandGripDTO(
    var thumb: Boolean = true,
    var index: Boolean = true,
    var middle: Boolean = true,
    var ring: Boolean = true,
    var little: Boolean = true
) : Parcelable

enum class FingerType(val type: String) {
    THUMB("Thumb"), INDEX("Index Finger"), MIDDLE("Middle Finger"), RING(
        "Ring Finger"
    ),
    LITTLE("Little Finger");

    override fun toString(): String {
        return type
    }
}

enum class HandType { LEFT, RIGHT }
