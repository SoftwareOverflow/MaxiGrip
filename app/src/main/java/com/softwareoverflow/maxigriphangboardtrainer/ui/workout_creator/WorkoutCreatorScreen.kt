package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import com.ramcosta.composedestinations.generated.destinations.RepeatWorkoutDialogDestination
import com.ramcosta.composedestinations.generated.destinations.UnsavedChangesWarningScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WorkoutSaverScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WorkoutScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WorkoutSetCreatorStep1Destination
import com.softwareoverflow.maxigriphangboardtrainer.ui.getFormattedDuration
import com.softwareoverflow.maxigriphangboardtrainer.ui.navigation.NavigationResultActionBasic
import com.softwareoverflow.maxigriphangboardtrainer.ui.navigation.NavigationSaveResult
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.Typography
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.MobileAdsManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AppScreen
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.ColouredIcon
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.DropDownAction
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.DropDownItem
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.HandGripDual
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.TopAppRow
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.findActivity
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.horizontalGradient

@Destination<RootGraph>
@Composable
fun WorkoutCreatorScreen(
    workoutDTO: WorkoutDTO,
    navigator: DestinationsNavigator,
    workoutSetResult: ResultRecipient<WorkoutSetCreatorStep1Destination, WorkoutSetDTO>,
    unsavedChangesResult: ResultRecipient<UnsavedChangesWarningScreenDestination, NavigationResultActionBasic>,
    repeatDialogResult: ResultRecipient<RepeatWorkoutDialogDestination, WorkoutDTO>,
    saveWorkoutResult: ResultRecipient<WorkoutSaverScreenDestination, NavigationSaveResult>,
    viewModel: WorkoutCreatorViewModel = viewModel(
        factory = WorkoutCreatorViewModelFactory(
            LocalContext.current, workoutDTO
        )
    )
) {
    val workout by viewModel.workout.collectAsState()
    val context = LocalContext.current

    BackHandler {
        if(viewModel.showUnsavedChangesWarning)
            navigator.navigate(UnsavedChangesWarningScreenDestination)
        else navigator.popBackStack()
    }

    unsavedChangesResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {/* Do Nothing */
            }

            is NavResult.Value -> {
                if (result.value == NavigationResultActionBasic.ACTION_POSITIVE) navigator.popBackStack()
            }
        }
    }

    workoutSetResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {/* Do Nothing */
            }

            is NavResult.Value -> {
                viewModel.addOrUpdateWorkoutSet(result.value)
            }
        }
    }

    repeatDialogResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {/* Do Nothing */
            }

            is NavResult.Value -> {
                viewModel.setRepeatCount(result.value.numReps, result.value.recoveryTime)
            }
        }
    }

    saveWorkoutResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {/* Do Nothing */
            }

            is NavResult.Value -> {
                viewModel.onWorkoutSaved(result.value.id, result.value.name)
            }
        }
    }

    val screenTitle = if (workout.id != null) stringResource(
        id = R.string.edit_your_workout, workout.name
    ) else stringResource(id = R.string.create_your_workout)

    AppScreen(topAppRow = {
        TopAppRow(startIcon = {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name,
                Modifier.clickable {
                    if(viewModel.showUnsavedChangesWarning)
                        navigator.navigate(UnsavedChangesWarningScreenDestination)
                    else navigator.popBackStack()
                },
                tint = MaterialTheme.colors.onPrimary
            )
        }, title = screenTitle, endIcon = {
            Icon(
                painterResource(id = R.drawable.icon_save_options),
                contentDescription = stringResource(
                    id = R.string.save
                ),
                Modifier.clickable {
                    if (workout.workoutSets.any()) navigator.navigate(
                        WorkoutSaverScreenDestination(workout)
                    )
                },
                tint = if (workout.workoutSets.any()) MaterialTheme.colors.onPrimary else Color.Gray
            )
        })
    }, bottomAppRow = {

    }, showBannerAd = true
    ) { modifier ->
        WorkoutCreatorScreenContent(workout = workout, onAddWorkoutSetClicked = {
            val workoutSet = viewModel.getWorkoutSetToEdit(null)
            navigator.navigate(WorkoutSetCreatorStep1Destination(workout, workoutSet))
        }, onDropDownItemSelected = { position, action ->
            when (action) {
                DropDownAction.EDIT -> {
                    val workoutSet = viewModel.getWorkoutSetToEdit(position)
                    navigator.navigate(WorkoutSetCreatorStep1Destination(workout, workoutSet))
                }

                DropDownAction.DELETE -> viewModel.removeWorkoutSetFromWorkout(position)
                DropDownAction.MOVE_UP -> viewModel.changeWorkoutSetOrder(
                    position, position - 1
                )

                DropDownAction.MOVE_DOWN -> viewModel.changeWorkoutSetOrder(
                    position, position + 1
                )

                else -> { /* Do Nothing */
                }
            }

        }, onRepeatDialogClicked = {
            navigator.navigate(RepeatWorkoutDialogDestination(workout))
        }, onStartWorkout = {
            if (workout.workoutSets.isNotEmpty()) {
                MobileAdsManager.showAdBeforeWorkout(
                    context.findActivity(),
                    onAdClosedCallback = {
                        navigator.navigate(
                            WorkoutScreenDestination(
                                workout
                            )
                        )
                    })
            }
        }, modifier = modifier
        )
    }
}

@Composable
private fun WorkoutCreatorScreenContent(
    workout: WorkoutDTO,
    onAddWorkoutSetClicked: () -> Unit,
    onDropDownItemSelected: (position: Int, action: DropDownAction) -> Unit,
    onRepeatDialogClicked: () -> Unit,
    onStartWorkout: () -> Unit,
    modifier: Modifier
) {
    val workoutSets = workout.workoutSets

    Scaffold(bottomBar = {
        Row(
            modifier = Modifier
                .height(MaterialTheme.spacing.extraExtraLarge)
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingActionButton(
                onClick = { onRepeatDialogClicked() },
                modifier = Modifier.size(MaterialTheme.spacing.extraLarge)
            ) {
                Icon(
                    painterResource(id = R.drawable.icon_repeat),
                    contentDescription = null,
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(MaterialTheme.spacing.small)
                )
            }
            Text(
                "${workout.numReps}x",
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(MaterialTheme.spacing.small)
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = stringResource(
                    id = R.string.duration_text, workout.getFormattedDuration()
                ),
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(MaterialTheme.spacing.small)
            )

            val fabColor =
                if (workout.workoutSets.isEmpty()) Color.Gray else MaterialTheme.colors.secondary
            FloatingActionButton(
                onClick = {
                    onStartWorkout()
                },
                backgroundColor = fabColor,
                modifier = Modifier.size(MaterialTheme.spacing.extraLarge)
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(MaterialTheme.spacing.small)
                )
            }
        }
    }) { paddingValues ->
        //BackgroundGradient()
        Box(
            modifier
                .padding(paddingValues)
                .fillMaxWidth(), contentAlignment = Alignment.TopCenter
        ) {
            if (workoutSets.isNotEmpty()) LazyColumn(Modifier.padding(bottom = MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.extraSmall)) {
                itemsIndexed(workoutSets.sortedBy { it.orderInWorkout }) { position, workoutSet ->
                    val dropDownItems = mutableListOf<DropDownItem>()

                    if (position > 0) dropDownItems.add(
                        DropDownItem(
                            R.string.move_up, DropDownAction.MOVE_UP
                        )
                    )

                    if (position < workoutSets.size - 1) dropDownItems.add(
                        DropDownItem(
                            R.string.move_down, DropDownAction.MOVE_DOWN
                        )
                    )

                    dropDownItems.add(DropDownItem(R.string.edit, DropDownAction.EDIT))
                    dropDownItems.add(DropDownItem(R.string.delete, DropDownAction.DELETE))

                    WorkoutSetListItem(workoutSet = workoutSet,
                        dropDownItems = dropDownItems,
                        onDropDownSelected = {
                            onDropDownItemSelected(position, it)
                        })
                }
            }

            val iconSizes = MaterialTheme.spacing.extraLarge
            val iconCircle = CircleShape.copy(CornerSize(iconSizes))

            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = { onAddWorkoutSetClicked() },
                    Modifier.size(iconSizes),
                    shape = iconCircle
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }

                if (workoutSets.isEmpty()) Text(
                    stringResource(id = R.string.add_workout_set_hint),
                    Modifier.padding(top = MaterialTheme.spacing.small),
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}


@Composable
private fun WorkoutSetListItem(
    workoutSet: WorkoutSetDTO,
    dropDownItems: List<DropDownItem>,
    onDropDownSelected: (DropDownAction) -> Unit
) {
    val iconSizes = MaterialTheme.spacing.extraExtraLarge

    var isContentMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val density = LocalDensity.current

    val gripType = workoutSet.gripTypeDTO

    // TODO try wrapping the whole Card into a Button and clipping it to shape. Put the click features on the Button, not the card
    Card(Modifier
        .fillMaxWidth()
        .padding(MaterialTheme.spacing.extraSmall)
        .horizontalGradient(RoundedCornerShape(iconSizes))
        .border(2.dp, MaterialTheme.colors.onPrimary, RoundedCornerShape(iconSizes))
        .indication(interactionSource, LocalIndication.current)
        .onSizeChanged {
            with(density) {
                itemHeight = it.height.toDp()
            }
        }
        .pointerInput(true) {
            detectTapGestures(onLongPress = {
                isContentMenuVisible = true
                pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
            }, onPress = {
                val press = PressInteraction.Press(it)
                interactionSource.emit(press)
                tryAwaitRelease()
                interactionSource.emit(PressInteraction.Release(press))
            }, onTap = {
                onDropDownSelected(DropDownAction.EDIT)
            })
        }, shape = RoundedCornerShape(iconSizes)

    ) {
        Row(Modifier.fillMaxWidth()) {
            HandGripDual(
                left = gripType.leftHand,
                right = gripType.rightHand,
                modifier = Modifier
                    .size(iconSizes)
                    .border(1.dp, MaterialTheme.colors.onPrimary, CircleShape)
            )

            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(MaterialTheme.spacing.extraSmall)
                    .padding(vertical = MaterialTheme.spacing.extraExtraSmall),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    gripType.name ?: "",
                    Modifier.padding(horizontal = MaterialTheme.spacing.extraSmall),
                    style = MaterialTheme.typography.h5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconTextRow(
                        icon = ColouredIcon.WORK, value = workoutSet.workTime
                    )
                    IconTextRow(
                        icon = ColouredIcon.REST, value = workoutSet.restTime
                    )
                    IconTextRow(
                        icon = ColouredIcon.REPEAT, value = workoutSet.numReps
                    )
                    IconTextRow(
                        icon = ColouredIcon.RECOVER, value = workoutSet.recoverTime
                    )

                }
            }
        }

        DropdownMenu(
            expanded = isContentMenuVisible,
            onDismissRequest = { isContentMenuVisible = false },
            offset = pressOffset.copy(x = pressOffset.x, y = pressOffset.y - itemHeight),
            modifier = Modifier
                .background(MaterialTheme.colors.primary, MaterialTheme.shapes.large)
        ) {
            dropDownItems.forEach { item ->
                DropdownMenuItem(onClick = {
                    onDropDownSelected(item.action)
                    isContentMenuVisible = false
                }) {
                    val iconId = when (item.action) {
                        DropDownAction.DELETE -> Icons.Filled.Delete
                        DropDownAction.EDIT -> Icons.Filled.Edit
                        DropDownAction.MOVE_UP -> Icons.Filled.ArrowUpward
                        DropDownAction.MOVE_DOWN -> Icons.Filled.ArrowDownward
                        else -> null
                    }

                    iconId?.let {
                        Icon(
                            iconId,
                            contentDescription = stringResource(id = item.text),
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }

                    Text(stringResource(id = item.text), style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}

@Composable
private fun IconTextRow(icon: ColouredIcon, value: Int) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        ColouredIcon(icon = icon)

        Text(value.toString(), style = Typography.body1)
    }
}

@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun WorkoutCreatorScreenPreview() {
    val dto = WorkoutDTO(
        null, "This is MY workout!", workoutSets = mutableListOf(
            WorkoutSetDTO(
                gripTypeDTO = GripTypeDTO(
                    0, "Example ET But really long name", /*"et_icon_dumbbell", "#008296"*/
                ), 15, 5, 6, 60, orderInWorkout = 2
            ), WorkoutSetDTO(
                gripTypeDTO = GripTypeDTO(
                    null, "Number 1 ET", /*"et_icon_run", "#FFEA4DB0"*/
                ), 7, 3, 6, 90, orderInWorkout = 1
            )
        )
    )

    AppTheme {
        WorkoutCreatorScreenContent(workout = dto, {}, { _, _ -> }, {}, {}, Modifier
        )
    }
}

@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun PreviewNoSets() {
    val dto = WorkoutDTO(
        null, "This is MY workout!", workoutSets = mutableListOf()
    )

    AppTheme {
        WorkoutCreatorScreenContent(workout = dto, {}, { _, _ -> }, {}, {}, Modifier
        )
    }
}