package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator.workout_set_creator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.compose.BasicTextFieldInt
import com.ramcosta.composedestinations.generated.destinations.WorkoutSetCreatorHelpDestination
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AppScreen
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.ColouredIcon
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.HandGripDual
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.TopAppRow

@Destination<RootGraph>
@Composable
fun WorkoutSetCreatorStep2(
    dto: WorkoutSetDTO,
    resultBackNavigator: ResultBackNavigator<WorkoutSetDTO>,
    navigator: DestinationsNavigator
) {

    val workoutSet = remember { mutableStateOf(dto) }

    AppScreen(topAppRow = {
        TopAppRow(startIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onStartPressed = { navigator.popBackStack() },
            title = stringResource(id = R.string.nav_set_creator_step_2),
            endIcon = Icons.Filled.QuestionMark,
            onEndIconPressed = {
                navigator.navigate(WorkoutSetCreatorHelpDestination)
            })
    }, bottomAppRow = null, showBannerAd = true) { modifier ->

        WorkoutSetCreatorStep2Content(workoutSet = workoutSet.value,
            modifier = modifier,
            onFinished = { workTime, restTime, numReps, recoverTime ->
                val result = workoutSet.value.copy(
                    workTime = workTime,
                    restTime = restTime,
                    numReps = numReps,
                    recoverTime = recoverTime
                )

                resultBackNavigator.navigateBack(result = result)
            })
    }
}

@Composable
private fun WorkoutSetCreatorStep2Content(
    workoutSet: WorkoutSetDTO,
    modifier: Modifier,
    onFinished: (workTime: Int, restTime: Int, numReps: Int, recoverTime: Int) -> Unit
) {
    var workTime by remember { mutableIntStateOf(workoutSet.workTime) }
    var workTimeError by remember { mutableStateOf(false) }

    var restTime by remember { mutableIntStateOf(workoutSet.restTime) }
    var restTimeError by remember { mutableStateOf(false) }

    var numReps by remember { mutableIntStateOf(workoutSet.numReps) }
    var numRepsError by remember { mutableStateOf(false) }

    var recoverTime by remember { mutableIntStateOf(workoutSet.recoverTime) }
    var recoverTimeError by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.small)
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            HandGripDual(
                left = workoutSet.gripTypeDTO.leftHand,
                right = workoutSet.gripTypeDTO.rightHand,
                modifier = Modifier.size(MaterialTheme.spacing.extraExtraExtraLarge)
            )

            Text(
                workoutSet.gripTypeDTO.name!!,
                Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.small),
                style = MaterialTheme.typography.h4,
                maxLines = 2,
                color = MaterialTheme.colors.onPrimary,
                overflow = TextOverflow.Ellipsis,

                )
        }

        Row(
            Modifier
                .weight(1f)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconInput(initialValue = workTime,
                icon = ColouredIcon.WORK,
                name = stringResource(id = R.string.hang_time),
                modifier = Modifier.weight(1f),
                onValueChange = { value, error ->
                    if (!error) workTime = value.toInt()
                    workTimeError = error
                })

            IconInput(initialValue = restTime,
                icon = ColouredIcon.REST,
                name = stringResource(id = R.string.rest_time),
                modifier = Modifier.weight(1f),
                onValueChange = { value, error ->
                    if (!error) restTime = value.toInt()
                    restTimeError = error
                })
        }

        Row(
            Modifier
                .weight(1f)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconInput(initialValue = numReps,
                icon = ColouredIcon.REPEAT,
                name = stringResource(id = R.string.num_reps),
                modifier = Modifier.weight(1f),
                onValueChange = { value, error ->
                    if (!error) numReps = value.toInt()
                    numRepsError = error
                })

            IconInput(initialValue = recoverTime,
                icon = ColouredIcon.RECOVER,
                name = stringResource(id = R.string.recovery_time),
                modifier = Modifier.weight(1f),
                onValueChange = { value, error ->
                    if (!error) recoverTime = value.toInt()
                    recoverTimeError = error
                })
        }

        val isError = workTimeError || restTimeError || numRepsError || recoverTimeError
        val bgColor = if (isError) Color.Gray else MaterialTheme.colors.secondary

        FloatingActionButton(onClick = {
            if (!isError) onFinished(workTime, restTime, numReps, recoverTime)
        }, Modifier.align(Alignment.End), backgroundColor = bgColor, shape = CircleShape) {
            Icon(
                Icons.Filled.Check,
                contentDescription = stringResource(id = R.string.content_desc_add_set_to_workout)
            )
        }
    }
}

@Composable
private fun IconInput(
    initialValue: Int,
    icon: ColouredIcon,
    name: String,
    modifier: Modifier,
    onValueChange: (String, Boolean) -> Unit
) {

    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        ColouredIcon(
            icon = icon,
            Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.medium)
        )
        Column(Modifier.fillMaxHeight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
            Spacer(Modifier.weight(1f))

            BasicTextFieldInt(
                initialValue = initialValue, MaterialTheme.typography.h4.copy(
                    textAlign = TextAlign.Center
                ), onChange = onValueChange,
            )

            Text(
                name,
                Modifier
                    .padding(MaterialTheme.spacing.small)
                    .weight(1f),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Center,
            )
        }

    }
}

/*@Preview(name = "Tablet", device = Devices.PIXEL_C)*/
@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Preview(name = "Phone 2", device = Devices.PIXEL)
@Composable
private fun Preview() {
    val workoutSet = WorkoutSetDTO(
        GripTypeDTO(
            3, "Jugs",
        ), 40, 20, 6, 120
    )

    AppTheme {
        WorkoutSetCreatorStep2Content(
            workoutSet = workoutSet,
            Modifier,
            onFinished = { _, _, _, _ -> })
    }
}