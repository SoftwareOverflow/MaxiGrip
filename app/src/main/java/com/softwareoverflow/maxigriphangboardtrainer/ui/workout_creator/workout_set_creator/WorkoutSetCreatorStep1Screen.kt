@file:OptIn(ExperimentalMaterialApi::class)

package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator.workout_set_creator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.HandGripDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.SnackbarManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.destinations.GripTypeCreatorScreenDestination
import com.softwareoverflow.maxigriphangboardtrainer.ui.destinations.WorkoutSetCreatorStep2Destination
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AppScreen
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.DropDownAction
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.DropDownItem
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.HandGripDual
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.TopAppRow
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.verticalBackground

@Destination
@Composable
fun WorkoutSetCreatorStep1(
    workoutDTO: WorkoutDTO,
    workoutSetDTO: WorkoutSetDTO,
    viewModel: WorkoutSetCreatorViewModel = viewModel(
        factory = WorkoutSetCreatorViewModelFactory(
            workoutSetDTO, LocalContext.current
        )
    ),
    navigator: DestinationsNavigator,
    etResultRecipient: ResultRecipient<GripTypeCreatorScreenDestination, Long>,
    resultBackNavigator: ResultBackNavigator<WorkoutSetDTO>,
    resultRecipient: ResultRecipient<WorkoutSetCreatorStep2Destination, WorkoutSetDTO>
) {

    val gripTypes = viewModel.allGripTypes.observeAsState()

    val selectedId = viewModel.selectedGripTypeId.observeAsState()

    val screenTitle = if (workoutSetDTO.orderInWorkout == null) stringResource(
        id = R.string.nav_set_creator_step_1
    ) else stringResource(
        id = R.string.nav_set_editor_step_1
    )

    val snackbarMessage by viewModel.unableToDeleteGripType.observeAsState()
    if (!snackbarMessage.isNullOrBlank()) {
        SnackbarManager.showMessage(snackbarMessage.toString())
        viewModel.unableToDeleteGripTypeWarningShown()
    }


    AppScreen(topAppRow = {
        TopAppRow(
            startIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onStartPressed = { navigator.popBackStack() },
            title = screenTitle
        )
    }, bottomAppRow = null, showBannerAd = true) { modifier ->
        WorkoutSetCreatorStep1Content(
            gripTypes = gripTypes.value ?: emptyList(),
            selectedId = selectedId.value,
            onItemSelected = {
                viewModel.setChosenGripTypeId(it)
            },
            onDropDownSelected = { id, action ->
                when (action) {
                    DropDownAction.DELETE -> {
                        id?.let {
                            viewModel.deleteGripTypeById(
                                id, workoutDTO.workoutSets
                            )
                        }
                    }

                    DropDownAction.EDIT -> {
                        viewModel.setChosenGripTypeId(id)
                        navigator.navigate(GripTypeCreatorScreenDestination(gripTypes.value!!.single { it.id == id }))
                    }

                    else -> { /* Do Nothing */
                    }
                }

            },
            onSearchFilterChanged = viewModel::setFilterText,
            onOrderChanged = viewModel::changeSortOrder,
            onCreateNewGripType = {
                navigator.navigate(GripTypeCreatorScreenDestination(GripTypeDTO()))
            },
            onStepComplete = {
                viewModel.setChosenGripTypeId(it)
                navigator.navigate(WorkoutSetCreatorStep2Destination(viewModel.workoutSet.value!!))
            },
            modifier = modifier
        )
    }


    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                val completedWorkoutSet = result.value
                resultBackNavigator.navigateBack(result = completedWorkoutSet)
            }
        }
    }

    etResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                viewModel.setChosenGripTypeId(result.value)
            }
        }
    }
}

@Composable
private fun WorkoutSetCreatorStep1Content(
    gripTypes: List<GripTypeDTO>,
    selectedId: Long?,
    onItemSelected: (id: Long) -> Unit,
    onDropDownSelected: (id: Long?, action: DropDownAction) -> Unit,
    onSearchFilterChanged: (search: String) -> Unit,
    onOrderChanged: () -> Unit,
    onCreateNewGripType: () -> Unit,
    onStepComplete: (id: Long) -> Unit,
    modifier: Modifier
) {
    //BackgroundGradient()
    Column(modifier.fillMaxWidth()) {
        TopBar(
            onSearchFilterChanged = onSearchFilterChanged, onOrderChanged = onOrderChanged
        )

        GripTypeGrid(
            gripTypes = gripTypes,
            selectedId = selectedId,
            onItemSelected = onItemSelected,
            onDropDownSelected = onDropDownSelected,
            Modifier
                .padding(MaterialTheme.spacing.extraSmall)
                .weight(1f)
        )

        Row(
            Modifier.height(MaterialTheme.spacing.extraExtraLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingActionButton(onClick = {
                onCreateNewGripType()
            }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.create_new_grip_type)
                )
            }

            Text(
                stringResource(id = R.string.create_new_grip_type),
                Modifier.padding(MaterialTheme.spacing.extraSmall),
                style = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.weight(1f))

            val bgColor =
                if (selectedId == null) Color.Gray else MaterialTheme.colors.secondary

            FloatingActionButton(
                onClick = {
                    selectedId?.let {
                        onStepComplete(it)
                    }
                }, backgroundColor = bgColor
            ) {

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.continue_to_step_2),
                )
            }
        }


    }
}

@Composable
private fun GripTypeGrid(
    gripTypes: List<GripTypeDTO>,
    selectedId: Long?,
    onItemSelected: (id: Long) -> Unit,
    onDropDownSelected: (id: Long, action: DropDownAction) -> Unit,
    modifier: Modifier,
) {
    val density = LocalDensity.current

    var selected = if (selectedId == null) 0 else gripTypes.indexOfFirst { it.id == selectedId }
    if (selected == -1) selected = 0

    val gridState = rememberLazyGridState(selected)

    val dropDownItems = mutableListOf(
        DropDownItem(R.string.edit, DropDownAction.EDIT),
        DropDownItem(R.string.delete, DropDownAction.DELETE)
    )

    var contentMenuForId by rememberSaveable {
        mutableLongStateOf(-1L)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp), modifier = modifier, state = gridState
    ) {
        items(gripTypes, key = { et -> et.id!! }) { gripType ->

            var pressOffset by remember {
                mutableStateOf(DpOffset.Zero)
            }
            var itemHeight by remember {
                mutableStateOf(0.dp)
            }
            val interactionSource = remember {
                MutableInteractionSource()
            }

            val cornerSize = MaterialTheme.spacing.extraExtraExtraLarge
            val bgShape = RoundedCornerShape(CornerSize(cornerSize / 10.0f))

            val selectedLevel = if (gripType.id == selectedId) 1f else 0f
            val selectedState =
                animateFloatAsState(targetValue = selectedLevel, label = "Selected Level")

            Box(
                Modifier
                    .padding(MaterialTheme.spacing.extraSmall)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.25f), bgShape)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalBackground(bgShape)
                        /*.background(MaterialTheme.colors.primary, bgShape)*/

                        //.radialBackground(bgShape, true)
                        .border(
                            BorderStroke(
                                (selectedState.value * 4).dp,
                                MaterialTheme.colors.primary
                            ),
                            bgShape
                        )
                        .border(2.dp, MaterialTheme.colors.onPrimary, bgShape)
                        .padding(MaterialTheme.spacing.small)
                        .focusable(enabled = false)
                        .indication(
                            interactionSource, null
                        )
                        .onSizeChanged {
                            with(density) {
                                itemHeight = it.height.toDp()
                            }
                        }
                        .pointerInput(true) {
                            detectTapGestures(onLongPress = {
                                contentMenuForId = gripType.id!!
                                pressOffset = DpOffset(
                                    it.x.toDp(), it.y.toDp()
                                )
                            }, onPress = {
                                val press = PressInteraction.Press(it)
                                interactionSource.emit(press)
                                tryAwaitRelease()
                                interactionSource.emit(PressInteraction.Release(press))
                            }, onTap = {
                                onItemSelected(gripType.id!!)
                            })
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    HandGripDual(
                        left = gripType.leftHand,
                        right = gripType.rightHand,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Box(
                        Modifier
                            .height(MaterialTheme.spacing.extraExtraLarge)
                            .padding(vertical = MaterialTheme.spacing.small)
                            .fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            gripType.name ?: "-",
                            style = MaterialTheme.typography.body1,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Icon(
                    Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.continue_to_step_2),
                    Modifier
                        .align(Alignment.BottomEnd)
                        .background(
                            MaterialTheme.colors.primary.copy(selectedState.value),
                            CircleShape.copy(
                                CornerSize(cornerSize / 6.0f)
                            )
                        ),
                    tint = MaterialTheme.colors.onPrimary.copy(alpha = selectedState.value)
                )


                DropdownMenu(
                    expanded = contentMenuForId == gripType.id,
                    onDismissRequest = { contentMenuForId = -1L },
                    offset = pressOffset.copy(x = pressOffset.x, y = pressOffset.y - itemHeight),
                    modifier = Modifier
                        .background(MaterialTheme.colors.primary, MaterialTheme.shapes.large)
                ) {
                    dropDownItems.forEach { item ->
                        DropdownMenuItem(onClick = {
                            onDropDownSelected(gripType.id!!, item.action)
                            contentMenuForId = -1L
                        }) {
                            val iconId = when (item.action) {
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
        }
    }
}


@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Preview(name = "Tablet", device = Devices.PIXEL_C)
@Composable
private fun Preview() {
    val list = mutableListOf<GripTypeDTO>()
    list.add(
        GripTypeDTO(
            0, "Full Crimp",
        )
    )
    list.add(
        GripTypeDTO(
            1,
            "Back 3",
            HandGripDTO(false, false, true, true, true),
            HandGripDTO(false, false, true, true, true)
        )
    )
    list.add(
        GripTypeDTO(
            2,
            "Front 2 / Back 2",
            HandGripDTO(false, true, true, false, false),
            HandGripDTO(false, false, false, true, true)
        )
    )
    list.add(
        GripTypeDTO(
            3,
            "Jugs",
            HandGripDTO(false, true, true, true, true),
            HandGripDTO(false, true, true, true, true)
        )
    )
    list.add(
        GripTypeDTO(
            4,
            "Mid 2",
            HandGripDTO(false, false, true, true, false),
            HandGripDTO(false, false, true, true, false)
        )
    )

    AppTheme {
        WorkoutSetCreatorStep1Content(
            gripTypes = list,
            selectedId = 3,
            onItemSelected = {},
            onDropDownSelected = { _, _ -> },
            onSearchFilterChanged = {},
            onOrderChanged = {},
            onCreateNewGripType = {},
            onStepComplete = {},
            Modifier
        )
    }
}

@Composable
private fun TopBar(
    onSearchFilterChanged: (search: String) -> Unit, onOrderChanged: () -> Unit
) {

    var filter by remember { mutableStateOf("") }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val focusManager = LocalFocusManager.current

        BasicTextField(
            value = filter, onValueChange = {
                if (it.length <= 20) {
                    filter = it
                    onSearchFilterChanged(it)
                }
            }, singleLine = true, modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.caption,
            cursorBrush = SolidColor(MaterialTheme.colors.onPrimary)
        ) { innerTextField ->


            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Search, contentDescription = null,
                    tint = MaterialTheme.colors.onPrimary
                )

                innerTextField()

                Spacer(modifier = Modifier.weight(1f))

                if (filter.isNotEmpty()) Icon(
                    Icons.Filled.Close,
                    contentDescription = null,
                    Modifier.clickable {
                        filter = ""
                        onSearchFilterChanged(filter)
                        focusManager.clearFocus()
                    },
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        }

        Modifier.weight(1f)
        Icon(
            Icons.Filled.SortByAlpha,
            contentDescription = null,
            Modifier.clickable { onOrderChanged() }, tint = MaterialTheme.colors.onPrimary,
        )
    }
}
