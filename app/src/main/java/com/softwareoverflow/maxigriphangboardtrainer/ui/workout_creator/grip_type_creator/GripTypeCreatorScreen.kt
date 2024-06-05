package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator.grip_type_creator

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.FingerType
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.HandGripDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.HandType
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.AppScreen
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.BottomAppRow
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.CircleCheckbox
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.HandGripDual
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.TopAppRow


@Destination
@Composable
fun GripTypeCreatorScreen(
    gripTypeDTO: GripTypeDTO,
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<Long>,
    viewModel: GripTypeViewModel = viewModel(
        factory = GripTypeViewModelFactory(
            LocalContext.current, gripTypeDTO
        )
    )
) {
    val gripType by viewModel.gripType.collectAsState()
    val gripTypeName = remember { mutableStateOf(gripType.name ?: "") }

    AppScreen(topAppRow = {
        TopAppRow(
            startIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onStartPressed = { navigator.popBackStack() },
            title = stringResource(id = R.string.create_new_grip_type),
            endIcon = null,
            onEndIconPressed = null
        )
    }, bottomAppRow = {

        val isError =
            gripTypeName.value.isBlank() || gripTypeName.value.isEmpty() || gripTypeName.value.length > 30

        BottomAppRow {
            BasicTextField(
                value = gripTypeName.value,
                onValueChange = {
                    if (it.length <= 30) {
                        gripTypeName.value = it
                    }
                },
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.body2,
                cursorBrush = SolidColor(MaterialTheme.colors.onPrimary)
            ) { innerTextField ->

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.extraSmall)
                ) {
                    Box {
                        if (gripTypeName.value.isEmpty()) {
                            Text(
                                stringResource(id = R.string.grip_type_name),
                                style = MaterialTheme.typography.body2
                            )
                        }
                        innerTextField()
                    }
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.extraSmall),
                        color = if (isError) MaterialTheme.colors.error else MaterialTheme.colors.onSurface,
                        thickness = 1.dp
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    if (!isError) {
                        viewModel.updateName(gripTypeName.value)

                        val id = viewModel.saveGripType()

                        resultBackNavigator.navigateBack(result = id)
                    }
                }, backgroundColor = if (isError) Color.Gray else MaterialTheme.colors.secondary
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.save),
                    Modifier.padding(MaterialTheme.spacing.small)
                )
            }
        }
    }, showBannerAd = true) { modifier ->
        GripTypeCreatorContent(
            gripType,
            onChange = { handType, finger, enabled ->
                viewModel.updateHandGrip(handType, finger, enabled)
            },
            modifier = modifier,
        )
    }
}

@Composable
private fun GripTypeCreatorContent(
    gripTypeDTO: GripTypeDTO,
    onChange: (HandType, FingerType, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val leftHand = gripTypeDTO.leftHand
    val rightHand = gripTypeDTO.rightHand

    //BackgroundGradient()
    Column(
        modifier.fillMaxSize()
    ) {
        HandGripDual(left = leftHand,
            right = rightHand,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = MaterialTheme.spacing.medium,
                    horizontal = MaterialTheme.spacing.extraExtraLarge
                )
                .border(4.dp, MaterialTheme.colors.onPrimary, CircleShape)
                .aspectRatio(1f),
            onUpdate = { handType, fingerType, enabled -> onChange(handType, fingerType, enabled) })

        Row {
            Column(Modifier.weight(1f)) {
                FingerCheckbox(
                    name = FingerType.THUMB.toString(),
                    isChecked = leftHand.thumb,
                    onChange = {
                        onChange(HandType.LEFT, FingerType.THUMB, it)
                    })

                FingerCheckbox(
                    name = FingerType.INDEX.toString(),
                    isChecked = leftHand.index,
                    onChange = {
                        onChange(HandType.LEFT, FingerType.INDEX, it)
                    })

                FingerCheckbox(
                    name = FingerType.MIDDLE.toString(),
                    isChecked = leftHand.middle,
                    onChange = {
                        onChange(HandType.LEFT, FingerType.MIDDLE, it)
                    })

                FingerCheckbox(
                    name = FingerType.RING.toString(),
                    isChecked = leftHand.ring,
                    onChange = {
                        onChange(HandType.LEFT, FingerType.RING, it)
                    })

                FingerCheckbox(
                    name = FingerType.LITTLE.toString(),
                    isChecked = leftHand.little,
                    onChange = {
                        onChange(HandType.LEFT, FingerType.LITTLE, it)
                    })
            }

            Column(Modifier.weight(1f)) {
                FingerCheckbox(
                    name = FingerType.THUMB.toString(),
                    isChecked = rightHand.thumb,
                    onChange = {
                        onChange(HandType.RIGHT, FingerType.THUMB, it)
                    })

                FingerCheckbox(
                    name = FingerType.INDEX.toString(),
                    isChecked = rightHand.index,
                    onChange = {
                        onChange(HandType.RIGHT, FingerType.INDEX, it)
                    })

                FingerCheckbox(
                    name = FingerType.MIDDLE.toString(),
                    isChecked = rightHand.middle,
                    onChange = {
                        onChange(HandType.RIGHT, FingerType.MIDDLE, it)
                    })

                FingerCheckbox(
                    name = FingerType.RING.toString(),
                    isChecked = rightHand.ring,
                    onChange = {
                        onChange(HandType.RIGHT, FingerType.RING, it)
                    })

                FingerCheckbox(
                    name = FingerType.LITTLE.toString(),
                    isChecked = rightHand.little,
                    onChange = {
                        onChange(HandType.RIGHT, FingerType.LITTLE, it)
                    })
            }
        }
    }
}

@Composable
private fun FingerCheckbox(name: String, isChecked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        CircleCheckbox(checked = isChecked, onCheckedChange = { onChange(!isChecked) })
        Text(name, style = MaterialTheme.typography.caption)
    }
}

@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun Preview() {
    AppTheme {
        GripTypeCreatorContent(
            GripTypeDTO(
                leftHand = HandGripDTO(
                    thumb = true,
                    index = false,
                    middle = false,
                    ring = true,
                    little = true
                ),
                rightHand = HandGripDTO(
                    thumb = false,
                    index = true,
                    middle = true,
                    ring = false,
                    little = false
                )
            ), { _, _, _ -> })
    }
}