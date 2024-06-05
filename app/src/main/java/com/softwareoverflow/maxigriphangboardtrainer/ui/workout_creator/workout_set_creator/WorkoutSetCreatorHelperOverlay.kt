package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator.workout_set_creator

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.ColouredIcon
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.DialogOverlay

@Composable
@Destination(style = DestinationStyle.Dialog::class)
fun WorkoutSetCreatorHelp(navigator: DestinationsNavigator) {

    DialogOverlay(
        icon = Icons.Filled.Info,
        title = R.string.workout_set_help,
        negativeButtonText = R.string.close,
        onNegativePress = { navigator.popBackStack() },
        positiveButtonText = null,
        onPositivePress = null
    ) { modifier ->

        Column(modifier) {

            Text(
                stringResource(id = R.string.workout_set_help_details),
                style = MaterialTheme.typography.caption
            )

            Spacer(Modifier.height(MaterialTheme.spacing.small))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    IconTextColum(
                        icon = ColouredIcon.WORK,
                        text = R.string.hang,
                        Modifier.weight(1f),
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        Modifier.weight(0.3f),
                        tint = MaterialTheme.colors.onPrimary
                    )
                    IconTextColum(
                        icon = ColouredIcon.REST,
                        text = R.string.rest,
                        Modifier.weight(1f),
                    )
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    Modifier.weight(0.25f),
                    tint = MaterialTheme.colors.onPrimary
                )

                Row(
                    Modifier
                        .weight(1f), verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTextColum(
                        icon = ColouredIcon.WORK,
                        text = R.string.hang,
                        Modifier.weight(1f),
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        Modifier.weight(0.3f),
                        tint = MaterialTheme.colors.onPrimary
                    )
                    IconTextColum(
                        icon = ColouredIcon.REST,
                        text = R.string.rest,
                        Modifier.weight(1f),
                    )
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    Modifier.weight(0.25f),
                    tint = MaterialTheme.colors.onPrimary
                )

                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    IconTextColum(
                        icon = ColouredIcon.WORK,
                        text = R.string.hang,
                        Modifier.weight(1f),
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        Modifier.weight(0.3f),
                        tint = MaterialTheme.colors.onPrimary
                    )
                    IconTextColum(
                        icon = ColouredIcon.RECOVER,
                        text = R.string.recover,
                        Modifier.weight(1f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTextColumn(
                    icon = ColouredIcon.REPEAT,
                    text = "1/3",
                    modifier = Modifier.weight(2.5f),
                )

                Spacer(modifier = Modifier.weight(1f))

                IconTextColumn(
                    icon = ColouredIcon.REPEAT,
                    text = "2/3",
                    modifier = Modifier.weight(2.5f),
                )

                Spacer(modifier = Modifier.weight(1f))

                IconTextColumn(
                    icon = ColouredIcon.REPEAT,
                    text = "3/3",
                    modifier = Modifier.weight(2.5f),
                )
            }
        }
    }
}

@Composable
private fun IconTextColum(
    icon: ColouredIcon, @StringRes text: Int, modifier: Modifier,
) {
    IconTextColumn(icon = icon, text = stringResource(id = text), modifier = modifier)
}

@Composable
private fun IconTextColumn(icon: ColouredIcon, text: String, modifier: Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        ColouredIcon(icon = icon, Modifier
            .background(
                MaterialTheme.colors.background,
                CircleShape.copy(CornerSize(MaterialTheme.spacing.extraExtraExtraExtraLarge))
            )
            .padding(MaterialTheme.spacing.extraExtraSmall))

        Text(
            text = text,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onPrimary
        )
    }
}


@Preview(name = "Tablet", device = Devices.PIXEL_C)
@Preview(name = "Phone", device = Devices.PIXEL_4_XL)
@Composable
private fun Preview() {
    AppTheme {
        WorkoutSetCreatorHelp(EmptyDestinationsNavigator)
    }
}
