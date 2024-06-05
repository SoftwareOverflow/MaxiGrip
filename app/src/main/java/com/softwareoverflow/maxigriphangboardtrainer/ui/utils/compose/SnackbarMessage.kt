package com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose

import androidx.compose.material.SnackbarDuration

data class SnackbarMessage(
    val message: String,
    val onDismiss: () -> Unit,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val duration: SnackbarDuration = SnackbarDuration.Long
)