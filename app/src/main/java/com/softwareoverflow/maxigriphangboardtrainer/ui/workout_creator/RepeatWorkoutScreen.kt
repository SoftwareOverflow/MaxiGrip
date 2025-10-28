package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.chargemap.compose.numberpicker.NumberPicker
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.compose.BasicTextFieldInt
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.DialogOverlay

@Destination<RootGraph>(style = DestinationStyle.Dialog::class)
@Composable
fun RepeatWorkoutDialog(workout: WorkoutDTO, resultBackNavigator: ResultBackNavigator<WorkoutDTO>) {
    var isRestBetweenError by remember { mutableStateOf(false) }
    var repeatPickerValue by remember { mutableStateOf(workout.numReps) }
    var restBetweenValue by remember { mutableStateOf(workout.recoveryTime) }

    DialogOverlay(icon = R.drawable.icon_repeat,
        title = R.string.repeat_workout,
        negativeButtonText = R.string.cancel,
        onNegativePress = { resultBackNavigator.navigateBack() },
        positiveButtonText = R.string.save,
        onPositivePress = {
            if (!isRestBetweenError) {
                workout.numReps = repeatPickerValue
                workout.recoveryTime = restBetweenValue

                resultBackNavigator.navigateBack(result = workout)
            }

        }) { modifier ->

        RepeatWorkoutDialogContent(repeatTimes = repeatPickerValue,
            restBetween = restBetweenValue,
            modifier,
            onRepsChange = { repeatPickerValue = it },
            onRestBetweenChange = { value, error ->
                if(!error)
                    restBetweenValue = value.toInt()

                isRestBetweenError = error
            })
    }
}

@Composable
private fun RepeatWorkoutDialogContent(
    repeatTimes: Int,
    restBetween: Int,
    modifier: Modifier,
    onRepsChange: (Int) -> Unit,
    onRestBetweenChange: (String, Boolean) -> Unit
) {

    Column(
        modifier
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(id = R.string.complete_workout),
                color = MaterialTheme.colors.onPrimary,
            )

            NumberPicker(
                value = repeatTimes,
                range = 1..10,
                onValueChange = {
                    onRepsChange(it)
                },
                dividersColor = MaterialTheme.colors.secondary
            )

            Text(
                stringResource(id = R.string.times),
                color = MaterialTheme.colors.onPrimary,
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(id = R.string.with),
                color = MaterialTheme.colors.onPrimary,
            )

            BasicTextFieldInt(initialValue = restBetween, textStyle = MaterialTheme.typography.body2.copy(
                textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
            ), onChange = onRestBetweenChange)

            Text(
                stringResource(id = R.string.s_rest_in_between),
                color = MaterialTheme.colors.onPrimary,
            )
        }
    }
}

@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun Preview() {
    AppTheme {
        RepeatWorkoutDialogContent(repeatTimes = 5, restBetween = 120, Modifier, {}, {_, _ ->})
    }
}

