package com.softwareoverflow.maxigriphangboardtrainer.ui.navigation

import androidx.compose.ui.window.DialogProperties
import com.ramcosta.composedestinations.spec.DestinationStyle

object NonDismissableDialog : DestinationStyle.Dialog {
    override val properties = DialogProperties(
        dismissOnClickOutside = false,
        dismissOnBackPress = false,
    )
}