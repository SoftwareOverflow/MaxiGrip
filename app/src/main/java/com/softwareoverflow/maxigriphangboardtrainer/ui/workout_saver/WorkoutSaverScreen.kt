package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_saver

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.SnackbarManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.compose.drawWithoutRect
import com.softwareoverflow.maxigriphangboardtrainer.ui.navigation.NavigationSaveResult
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AddCheckbox
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AppScreen
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.BottomAppRow
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.CircleCheckbox
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.TopAppRow
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.findActivity
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.pxToDp

@Destination<RootGraph>
@Composable
fun WorkoutSaverScreen(
    workout: WorkoutDTO,
    navigator: ResultBackNavigator<NavigationSaveResult>,
    viewModel: WorkoutSaverViewModel = hiltViewModel()
) {

    // Only want to do this once. This is not the ideal solution
    // but I need a combination of injection and parameters in the ViewModel
    LaunchedEffect(Unit) {
        viewModel.initialize(workout.id)
    }

    val activity = LocalContext.current.findActivity()

    val existingWorkouts = viewModel.savedWorkouts.observeAsState()
    val selectedId = viewModel.currentSelectedId.collectAsState()
    val canSaveWorkout by viewModel.canSaveWorkout.collectAsState()

    var name by remember { mutableStateOf(workout.name) }

    AppScreen(topAppRow = {
        TopAppRow(
            startIcon = Icons.AutoMirrored.Filled.ArrowBack, onStartPressed = {
                navigator.navigateBack()
            }, title = stringResource(
                id = R.string.save_workout
            )
        )
    }, bottomAppRow = {
        BottomAppRow {
            val iconSizes = MaterialTheme.spacing.extraLarge
            val iconCircle = CircleShape.copy(CornerSize(iconSizes))

            Spacer(modifier = Modifier.weight(1f))

            val snackbarSuccessMessage = stringResource(id = R.string.workout_saved, name)
            val snackbarFailureMessage = stringResource(id = R.string.workout_save_fail)
            val noWorkoutSlotsMessage = stringResource(id = R.string.no_free_workout_slots_warning)
            val noWorkoutSlotsAction = stringResource(id = R.string.upgrade)
            FloatingActionButton(
                onClick = {
                    if (canSaveWorkout) {
                        if (name.isNotBlank()) {
                            val savedId = viewModel.saveWorkout(workout, name, selectedId.value)
                            if (savedId != null) {

                                SnackbarManager.showMessage(snackbarSuccessMessage)
                                navigator.navigateBack(NavigationSaveResult(savedId, name))
                            } else {
                                SnackbarManager.showMessage(snackbarFailureMessage)
                            }
                        }
                    } else {
                        SnackbarManager.showMessage(noWorkoutSlotsMessage,
                            actionText = noWorkoutSlotsAction,
                            onAction = {
                                activity?.let {
                                    viewModel.upgrade(activity)
                                }
                            })
                    }
                }, Modifier.size(iconSizes), shape = iconCircle,
                backgroundColor = if (!canSaveWorkout || name.isBlank()) Color.Gray else MaterialTheme.colors.secondary
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.save_workout)
                )
            }
        }
    }) { modifier ->
        Content(modifier = modifier,
            existingWorkouts = existingWorkouts.value ?: emptyList(),
            selectedId = selectedId.value,
            onSelectionChange = viewModel::setCurrentlySelectedId,
            onNameUpdate = {
                name = it
            })
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    existingWorkouts: List<WorkoutDTO>,
    selectedId: Long?,
    onSelectionChange: (Long?) -> Unit,
    onNameUpdate: (String) -> Unit
) {

    Column(
        modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium)
    ) {

        BoxBorderRoundText(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            title = stringResource(id = R.string.save_new_workout)
        ) {
            var text by remember {
                mutableStateOf(
                    existingWorkouts.firstOrNull { it.id == selectedId }?.name ?: ""
                )
            }

            val focusRequester = remember { FocusRequester() }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AddCheckbox(checked = selectedId == null, onCheckedChange = {
                    if (it) {
                        onSelectionChange(null)
                        onNameUpdate(text)
                        focusRequester.requestFocus()
                    }
                })
                BasicTextField(
                    value = text,
                    onValueChange = {
                        if (it.length <= 30) {
                            text = it
                            onNameUpdate(text)
                        }
                    },
                    textStyle = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.hasFocus) {
                                onSelectionChange(null)
                                onNameUpdate(text)
                            }
                        },
                    maxLines = 1,
                    cursorBrush = SolidColor(MaterialTheme.colors.onPrimary)
                ) { innerTextField ->
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                        Box {
                            if (text.isEmpty()) Text(
                                stringResource(id = R.string.new_workout_name),
                                style = MaterialTheme.typography.caption
                            )
                            innerTextField()
                        }

                        Divider(Modifier.fillMaxWidth(), thickness = 1.dp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        if (existingWorkouts.any()) {
            BoxBorderRoundText(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                title = stringResource(id = R.string.overwrite_existing_workout)
            ) {

                LazyColumn(
                    Modifier.fillMaxWidth()
                ) {
                    items(existingWorkouts) { workout ->

                        var text by remember { mutableStateOf(workout.name) }

                        val focusRequester = remember { FocusRequester() }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircleCheckbox(checked = workout.id == selectedId,
                                onCheckedChange = {
                                    if (it) {
                                        onSelectionChange(workout.id)
                                        text = workout.name
                                        onNameUpdate(text)
                                        focusRequester.requestFocus()
                                    }
                                })

                            BasicTextField(
                                value = text, onValueChange = {
                                    if (it.length <= 30) {
                                        text = it
                                        onNameUpdate(text)
                                    }
                                },
                                Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .onFocusChanged {
                                        if (it.hasFocus) {
                                            onSelectionChange(workout.id)
                                            text = workout.name
                                            onNameUpdate(text)
                                        }
                                    }, textStyle = MaterialTheme.typography.body2,
                                maxLines = 1,
                                cursorBrush = SolidColor(MaterialTheme.colors.onPrimary)
                            ) { innerTextField ->
                                Column {
                                    innerTextField()

                                    Divider(Modifier.fillMaxWidth(), thickness = 1.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxBorderRoundText(
    modifier: Modifier,
    title: String,
    content: @Composable (BoxScope.() -> Unit)
) {
    var titleSize by remember { mutableStateOf(IntSize.Zero) }
    var titleCoordinates by remember { mutableStateOf<Rect?>(null) }

    Box {
        Box(
            modifier
                .drawWithoutRect(titleCoordinates)
                .padding(vertical = titleSize.height.pxToDp() / 2f)
                .border(
                    1.dp,
                    MaterialTheme.colors.onPrimary,
                    RoundedCornerShape(MaterialTheme.spacing.medium)
                )
        ) {
            content()
        }

        val titlePadding = MaterialTheme.spacing.large
        with(LocalDensity.current) {
            Text(title,
                Modifier
                    .onSizeChanged { titleSize = it }
                    .onGloballyPositioned {
                        titleCoordinates =
                            it
                                .boundsInParent()
                                .copy(left = it.boundsInParent().left + titlePadding.toPx())
                    }
                    .padding(start = titlePadding)
                    .padding(horizontal = MaterialTheme.spacing.extraSmall),
                style = MaterialTheme.typography.body2)
        }
    }
}

@Preview(name = "Tablet", device = Devices.PIXEL_C)
@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun Preview() {
    val workouts = listOf(
        WorkoutDTO(0, "Example Workout 1"),
        WorkoutDTO(3, "Another Saved workout"),
        WorkoutDTO(4, "And again"),
        WorkoutDTO(5, "And again.."),
        WorkoutDTO(6, "And again again.."),
        WorkoutDTO(7, "And again another......"),
        WorkoutDTO(7, "And again again"),
        WorkoutDTO(7, "And again another one here"),
    )

    AppTheme {
        Content(modifier = Modifier,
            existingWorkouts = workouts,
            selectedId = 3,
            onSelectionChange = {},
            onNameUpdate = {})
    }
}