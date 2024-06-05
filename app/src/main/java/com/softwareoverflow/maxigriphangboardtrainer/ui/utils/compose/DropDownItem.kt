package com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose

import androidx.annotation.StringRes

data class DropDownItem (
    @StringRes val text: Int,
    val action: DropDownAction
)

enum class DropDownAction {
    EDIT,
    MOVE_UP,
    MOVE_DOWN,
    DELETE,
    START
}