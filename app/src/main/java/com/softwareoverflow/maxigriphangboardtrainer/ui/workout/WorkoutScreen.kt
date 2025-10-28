package com.softwareoverflow.maxigriphangboardtrainer.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.compose.TriangularAnimation
import com.ramcosta.composedestinations.generated.destinations.WorkoutCompleteScreenDestination
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AppScreen
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.ColouredIcon
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.HandGrip
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.HandGripDual
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.KeepScreenOn
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.TopAppRow


@Destination<RootGraph>
@Composable
fun WorkoutScreen(
    workout: WorkoutDTO,
    viewModel: WorkoutViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current

    // This MUST be done first, so it can't go in a LaunchedEffect block
    viewModel.initialize(context, workout)

    val isWorkoutFinished by viewModel.isWorkoutFinished.collectAsState()
    val uiState = viewModel.uiState.collectAsState()

    if (isWorkoutFinished) {
        navigator.navigate(WorkoutCompleteScreenDestination(workout))
    }

    KeepScreenOn()

    AppScreen(topAppRow = {
        TopAppRow(
            startIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onStartPressed = { navigator.popBackStack() },
            title = stringResource(id = R.string.nav_workout)
        )
    }, bottomAppRow = null, showBannerAd = true) { modifier ->
        Content(
            modifier = modifier,
            uiState.value,
            toggleSound = viewModel::toggleSound,
            togglePause = viewModel::togglePause,
            skipSection = viewModel::skipSection
        )
    }

}

@Composable
private fun Content(
    modifier: Modifier,
    uiState: WorkoutViewModel.UiState,
    toggleSound: () -> Unit,
    togglePause: () -> Unit,
    skipSection: () -> Unit
) {

    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            /*Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.extraExtraLarge)
                .padding(MaterialTheme.spacing.extraSmall)
                .background(
                MaterialTheme.colors.primary, CircleShape)
                .border(4.dp, MaterialTheme.colors.onPrimary, CircleShape), verticalAlignment = Alignment.CenterVertically
        */
        ) {


            Text(
                text = uiState.currentGripType.name!!,
                Modifier
                    .weight(1f)
                    .padding(vertical = MaterialTheme.spacing.small),
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4
            )


        }

        Row(Modifier.padding(MaterialTheme.spacing.medium)) {

            HandGrip(
                grip = uiState.currentGripType.leftHand,
                isRightHand = false,
                modifier = Modifier
                    .sizeIn(
                        MaterialTheme.spacing.extraExtraLarge,
                        MaterialTheme.spacing.extraExtraLarge,
                        MaterialTheme.spacing.extraExtraExtraLarge,
                        MaterialTheme.spacing.extraExtraExtraLarge
                    )
                    .background(MaterialTheme.colors.primary, CircleShape)
                    .border(2.dp, MaterialTheme.colors.onPrimary, CircleShape)
                    .padding(MaterialTheme.spacing.small)
            )

            Box(
                Modifier
                    .weight(1f)
                    .widthIn(
                        MaterialTheme.spacing.large,
                        MaterialTheme.spacing.extraExtraExtraLarge
                    ), contentAlignment = Alignment.Center
            ) {
                ColouredIcon(
                    icon = ColouredIcon.REPEAT,
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f, false)
                )
                Text(uiState.currentRep, style = MaterialTheme.typography.h4)
            }

            HandGrip(
                grip = uiState.currentGripType.rightHand,
                isRightHand = true,
                modifier = Modifier
                    .sizeIn(
                        MaterialTheme.spacing.extraExtraLarge,
                        MaterialTheme.spacing.extraExtraLarge,
                        MaterialTheme.spacing.extraExtraExtraLarge,
                        MaterialTheme.spacing.extraExtraExtraLarge
                    )
                    .background(MaterialTheme.colors.primary, CircleShape)
                    .border(2.dp, MaterialTheme.colors.onPrimary, CircleShape)
                    .padding(MaterialTheme.spacing.small)
            )
        }

        Row(
            Modifier
                .height(MaterialTheme.spacing.extraExtraLarge)
                .fillMaxWidth()
        ) {
            AnimatedVisibility(visible = uiState.showUpNextGripType) {
                Row(
                    Modifier
                        .height(MaterialTheme.spacing.extraExtraLarge)
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colors.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(MaterialTheme.spacing.large)
                        )
                        .padding(MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiState.upNextGripType.let {
                        Text(
                            text = stringResource(id = R.string.up_next),
                            Modifier.padding(end = MaterialTheme.spacing.small)
                        )
                        HandGripDual(
                            left = it.leftHand,
                            right = it.rightHand,
                            modifier = Modifier.fillMaxHeight()
                        )
                        Text(
                            it.name!!,
                            Modifier.padding(start = MaterialTheme.spacing.extraSmall),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Text(uiState.sectionTimeRemaining, style = MaterialTheme.typography.h1)

        val sectionIcon = when (uiState.currentSection) {
            WorkoutSection.PREPARE -> ColouredIcon.PREPARE
            WorkoutSection.HANG -> ColouredIcon.WORK
            WorkoutSection.REST -> ColouredIcon.REST
            WorkoutSection.RECOVER -> ColouredIcon.RECOVER
        }

        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            this@Column.AnimatedVisibility(visible = uiState.currentSection == WorkoutSection.PREPARE) {
                TriangularAnimation(modifier = Modifier.fillMaxSize())
            }

            if (uiState.currentSection != WorkoutSection.PREPARE) {
                ColouredIcon(
                    icon = sectionIcon, Modifier
                        .size(MaterialTheme.spacing.extraExtraExtraExtraLarge)
                )
            } else {
                Spacer(Modifier.fillMaxSize())
            }
        }

        Text(
            uiState.currentSection.name,
            style = MaterialTheme.typography.h3
        )

        Text(
            text = "${stringResource(id = R.string.remaining_time)} ${uiState.workoutTimeRemaining}",
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.h5
        )

        Surface(contentColor = MaterialTheme.colors.onPrimary) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.large)
            ) {
                val soundIcon =
                    if (uiState.isSoundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff
                val pauseIcon =
                    if (uiState.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause

                Icon(soundIcon, contentDescription = soundIcon.name,
                    Modifier
                        .weight(1f)
                        .clickable {
                            toggleSound()
                        })
                Icon(pauseIcon, contentDescription = pauseIcon.name,
                    Modifier
                        .weight(1f)
                        .clickable {
                            togglePause()
                        })
                Icon(Icons.Filled.FastForward,
                    contentDescription = Icons.Filled.SkipNext.name,
                    Modifier
                        .weight(1f)
                        .clickable {
                            skipSection()
                        })
            }
        }
    }
}

@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun Preview() {
    AppTheme {
        Content(
            modifier = Modifier,

            WorkoutViewModel.UiState(
                currentWorkoutSet = WorkoutSetDTO(
                    GripTypeDTO(
                        1L,
                        "Bicep Curls really long name...",
                    )
                ),
                upNextGripType = GripTypeDTO(
                    1L,
                    "Bicep Curls really long name...",
                ),
                showUpNextGripType = true,
                currentSection = WorkoutSection.RECOVER,
                sectionTimeRemainingValue = 7,
                workoutTimeRemainingValue = 156,
                currentRepValue = 3,

                isPaused = false,
                isSoundOn = true
            ),
            {}, {}, {}
        )
    }
}

@Preview(name = "Phone2", device = Devices.PIXEL_4_XL)
@Composable
private fun PreviewDark() {
    AppTheme(darkTheme = true) {
        Content(
            modifier = Modifier,

            WorkoutViewModel.UiState(
                currentWorkoutSet = WorkoutSetDTO(
                    GripTypeDTO(
                        1L, "Bicep Curls", /*"et_icon_dumbbell", colorHex = "#FFFD5C29"*/
                    )
                ),
                upNextGripType = GripTypeDTO(
                    1L, "Bicep Curls", /*"et_icon_dumbbell", colorHex = "#FFFD5C29"*/
                ),
                showUpNextGripType = false,
                currentSection = WorkoutSection.PREPARE,
                sectionTimeRemainingValue = 7,
                workoutTimeRemainingValue = 156,
                currentRepValue = 3,

                isPaused = false,
                isSoundOn = true
            ), {}, {}, {}
        )
    }
}