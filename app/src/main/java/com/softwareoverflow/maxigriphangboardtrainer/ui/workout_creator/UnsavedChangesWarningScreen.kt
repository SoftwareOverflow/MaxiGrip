package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.ui.navigation.NavigationResultActionBasic
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.CircleCheckbox
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.DialogOverlay

@Destination<RootGraph>(style = DestinationStyle.Dialog::class)
@Composable
fun UnsavedChangesWarningScreen(
    resultNavigator: ResultBackNavigator<NavigationResultActionBasic>,
    viewModel: UnsavedChangesViewModel = viewModel()
) {

    val context = LocalContext.current
    var checkedState by remember { mutableStateOf(false) }


    DialogOverlay(icon = Icons.Filled.PriorityHigh,
        title = R.string.unsaved_changes_warning,
        negativeButtonText = R.string.cancel,
        onNegativePress = { resultNavigator.navigateBack(result = NavigationResultActionBasic.CANCELLED) },
        positiveButtonText = R.string.continue_anyway,
        onPositivePress = {
            viewModel.setDisableWarningState(checkedState, context)
            resultNavigator.navigateBack(result = NavigationResultActionBasic.ACTION_POSITIVE)
        }) { modifier ->

        Row(
            modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            CircleCheckbox(checked = checkedState,
                onCheckedChange = { checkedState = it })
            Text(
                stringResource(R.string.dont_show_again),
                Modifier.padding(start = MaterialTheme.spacing.extraSmall),
                style = MaterialTheme.typography.caption.copy(MaterialTheme.colors.onPrimary)
            )
        }
    }
}

@Preview(name = "Tablet", device = Devices.PIXEL_C)
@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun UnsavedChangesWarningPreview() {
    AppTheme {
        UnsavedChangesWarningScreen(EmptyResultBackNavigator())
    }
}