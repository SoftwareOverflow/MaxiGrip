package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_loader

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.HandGripDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.destinations.WorkoutCreatorScreenDestination
import com.softwareoverflow.maxigriphangboardtrainer.ui.destinations.WorkoutScreenDestination
import com.softwareoverflow.maxigriphangboardtrainer.ui.getFormattedDuration
import com.softwareoverflow.maxigriphangboardtrainer.ui.getTotalRecoverTime
import com.softwareoverflow.maxigriphangboardtrainer.ui.getTotalRestTime
import com.softwareoverflow.maxigriphangboardtrainer.ui.getTotalWorkTime
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.Typography
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.MobileAdsManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AppScreen
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.ColouredIcon
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.DropDownAction
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.DropDownItem
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.TopAppRow
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.findActivity
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.horizontalGradient
import com.softwareoverflow.maxigriphangboardtrainer.ui.view.list_adapter.workout.WorkoutLoaderDomainObject
import com.softwareoverflow.maxigriphangboardtrainer.ui.view.list_adapter.workout.WorkoutLoaderDomainObjectType

@Composable
@Destination
fun LoadWorkoutScreen(
    navigator: DestinationsNavigator, viewModel: WorkoutLoaderViewModel = hiltViewModel()
) {

    val workouts = viewModel.workouts.observeAsState()

    val context = LocalContext.current

    AppScreen(topAppRow = {
        TopAppRow(startIcon = Icons.AutoMirrored.Filled.ArrowBack, onStartPressed = {
            navigator.popBackStack()
        }, title = stringResource(id = R.string.load_saved_workout))
    }, bottomAppRow = null, showBannerAd = true) { modifier ->
        LoadWorkoutScreenContent(
            modifier = modifier,
            workouts.value ?: emptyList(),
            onAction = { action, id ->
                workouts.value?.first { it.dto.id == id }?.let { workout ->
                    when (action) {
                        DropDownAction.START -> {
                            MobileAdsManager.showAdBeforeWorkout(context.findActivity(),
                                onAdClosedCallback = {
                                    navigator.navigate(WorkoutScreenDestination(workout.dto))
                                })
                        }

                        DropDownAction.EDIT -> navigator.navigate(
                            WorkoutCreatorScreenDestination(
                                workout.dto
                            )
                        )

                        DropDownAction.DELETE -> viewModel.deleteWorkout(id)
                        else -> {/* Do Nothing */
                        }
                    }
                }
            })
    }
}

@Composable
private fun LoadWorkoutScreenContent(
    modifier: Modifier,
    workouts: List<WorkoutLoaderDomainObject>,
    onAction: (DropDownAction, Long) -> Unit
) {
    Column(modifier.fillMaxWidth()) {
        LazyColumn {
            items(workouts) { workout ->
                if (workout.type == WorkoutLoaderDomainObjectType.USER) {

                    var isContentMenuVisible by remember { mutableStateOf(false) }
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

                    Row(Modifier
                        .fillMaxWidth()
                        .padding(vertical = MaterialTheme.spacing.small)
                        .horizontalGradient(RoundedCornerShape(MaterialTheme.spacing.extraLarge / 2f))
                        .border(
                            4.dp,
                            Color.White,
                            RoundedCornerShape(MaterialTheme.spacing.extraLarge / 2f)
                        )
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
                                onAction(DropDownAction.EDIT, workout.dto.id!!)
                            })
                        }, verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(Modifier.weight(1f)) {
                            Text(
                                workout.dto.name,
                                Modifier
                                    .padding(MaterialTheme.spacing.small)
                                    .padding(start = MaterialTheme.spacing.small),
                                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colors.onPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = MaterialTheme.spacing.small),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                IconTextRow(
                                    icon = ColouredIcon.WORK, value = workout.dto.getTotalWorkTime()
                                )
                                IconTextRow(
                                    icon = ColouredIcon.REST, value = workout.dto.getTotalRestTime()
                                )
                                IconTextRow(
                                    icon = ColouredIcon.RECOVER,
                                    value = workout.dto.getTotalRecoverTime()
                                )
                            }
                        }

                        Text(
                            workout.dto.getFormattedDuration(),
                            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colors.onPrimary
                        )

                        Icon(Icons.Filled.PlayArrow,
                            contentDescription = stringResource(id = R.string.start_workout),
                            Modifier
                                .clickable {
                                    onAction(DropDownAction.START, workout.dto.id!!)
                                }
                                .padding(horizontal = MaterialTheme.spacing.extraSmall)
                                .size(MaterialTheme.spacing.large),
                            tint = MaterialTheme.colors.onPrimary)

                        val dropDownItems = mutableListOf(
                            DropDownItem(R.string.start_workout, DropDownAction.START),
                            DropDownItem(R.string.edit, DropDownAction.EDIT),
                            DropDownItem(R.string.delete, DropDownAction.DELETE)
                        )

                        DropdownMenu(
                            expanded = isContentMenuVisible,
                            onDismissRequest = { isContentMenuVisible = false },
                            offset = pressOffset.copy(
                                x = pressOffset.x, y = pressOffset.y - itemHeight
                            ),
                            modifier = Modifier
                                .background(MaterialTheme.colors.primary, MaterialTheme.shapes.large)
                        ) {
                            dropDownItems.forEach { item ->

                                DropdownMenuItem(onClick = {
                                    isContentMenuVisible = false
                                    onAction(item.action, workout.dto.id!!)
                                }) {

                                    val iconId = when (item.action) {
                                        DropDownAction.START -> Icons.Filled.PlayArrow
                                        DropDownAction.DELETE -> Icons.Filled.Delete
                                        DropDownAction.EDIT -> Icons.Filled.Edit
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

                } else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.extraExtraLarge)
                            .padding(vertical = MaterialTheme.spacing.small)
                            .horizontalGradient(shape = RoundedCornerShape(MaterialTheme.spacing.extraLarge / 2f))
                            .border(
                                4.dp,
                                Color.White,
                                RoundedCornerShape(MaterialTheme.spacing.extraLarge / 2f)
                            ), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (workout.type == WorkoutLoaderDomainObjectType.PLACEHOLDER_LOCKED) Icons.Filled.Lock else Icons.Filled.LockOpen,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                .background(
                                    MaterialTheme.colors.primary, shape = CircleShape
                                )
                                .border(2.dp, Color.White, CircleShape)
                                .padding(MaterialTheme.spacing.small),
                            tint = Color.White
                        )

                        Text(
                            workout.dto.name,
                            Modifier.padding(MaterialTheme.spacing.small),
                            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colors.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IconTextRow(icon: ColouredIcon, value: Int) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        ColouredIcon(icon = icon)

        Text(
            String.format("%02d:%02d", value / 60, value % 60),
            style = Typography.body1,
            color = MaterialTheme.colors.onPrimary
        )
    }
}

@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Preview(name = "Tablet", device = Devices.PIXEL_C)
@Composable
private fun LoadWorkoutPreview() {
    val list = listOf(
        WorkoutLoaderDomainObject(
            WorkoutDTO(
                1, "Example Workout Name (longest)", workoutSets = mutableListOf(
                    WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            1, "Any", /*"et_icon_run", "#FFFFC02B"*/
                        ), orderInWorkout = 0
                    ), WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            2, "Something", /*"et_icon_flash", "#FF3F51B5"*/
                        )
                    )
                ), numReps = 3, recoveryTime = 30
            )
        ),
        WorkoutLoaderDomainObject(
            WorkoutDTO(
                2, "Another Workout", workoutSets = mutableListOf(
                    WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            1, "Any", leftHand = HandGripDTO(
                                thumb = true,
                                index = false,
                                middle = false,
                                ring = true,
                                little = true
                            ), rightHand = HandGripDTO(
                                thumb = false,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            )
                        ), orderInWorkout = 0
                    ), WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            2, "Front 3", leftHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            ), rightHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            )
                        )
                    ), WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            2, "Front 3", leftHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            ), rightHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            )
                        )
                    ), WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            2, "Front 3", leftHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            ), rightHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            )
                        )
                    ), WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            2, "Front 3", leftHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            ), rightHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            )
                        )
                    ), WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            2, "Front 3", leftHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            ), rightHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            )
                        )
                    ), WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            2, "Front 3", leftHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            ), rightHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            )
                        )
                    ), WorkoutSetDTO(
                        gripTypeDTO = GripTypeDTO(
                            2, "Front 3", leftHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            ), rightHand = HandGripDTO(
                                thumb = true,
                                index = true,
                                middle = true,
                                ring = false,
                                little = false
                            )
                        )
                    )
                ), numReps = 1, recoveryTime = 1000
            )
        ),

        WorkoutLoaderDomainObject.getPlaceholderUnlocked(LocalContext.current),
        WorkoutLoaderDomainObject.getPlaceholderLocked(LocalContext.current)
    )

    AppTheme {
        LoadWorkoutScreenContent(modifier = Modifier, workouts = list, onAction = { _, _ -> })
    }
}
