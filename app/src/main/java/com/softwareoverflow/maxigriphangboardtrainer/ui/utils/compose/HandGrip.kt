package com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.FingerType
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.HandGripDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.HandType
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.AppTheme

@Composable
fun HandGripDual(
    left: HandGripDTO,
    right: HandGripDTO,
    modifier: Modifier,
    onUpdate: ((HandType, FingerType, Boolean) -> Unit)? = null
) {
    Row(
        modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colors.primary, CircleShape)
    ) {
        HandGrip(left, false, Modifier.weight(1f), onUpdate)
        HandGrip(right, true, Modifier.weight(1f), onUpdate)
    }
}

@Composable
fun HandGrip(
    grip: HandGripDTO, isRightHand: Boolean, modifier: Modifier,
    onUpdate: ((HandType, FingerType, Boolean) -> Unit)? = null
) {
    Box(
        modifier.fillMaxSize().then(
            if(isRightHand) Modifier
            else Modifier.scale(-1f, 1f) // Flip the left hand
        )
    ) {
/*        Row(Modifier.fillMaxSize()){
            Box(Modifier.background(Color.Gray).alpha(0.5f).fillMaxHeight().weight(6f))
            Box(Modifier.background(Color.Green).alpha(0.5f).fillMaxHeight().weight(22.5f))
            Box(Modifier.background(Color.Blue).alpha(0.5f).fillMaxHeight().weight(14f))
            Box(Modifier.background(Color.Cyan).alpha(0.5f).fillMaxHeight().weight(14.5f))
            Box(Modifier.background(Color.Magenta).alpha(0.5f).fillMaxHeight().weight(13.75f))
            Box(Modifier.background(Color.DarkGray).alpha(0.5f).fillMaxHeight().weight(13.75f))
            Box(Modifier.background(Color.Yellow).alpha(0.5f).fillMaxHeight().weight(15.5f))
        }*/

        Image(
            painter = painterResource(id = R.drawable.hand_base),
            contentDescription = null,
            Modifier.fillMaxSize(),
        )

        if (grip.thumb)
            FingerOverlay(id = R.drawable.finger_thumb, fingerType = FingerType.THUMB)

        if (grip.index)
            FingerOverlay(id = R.drawable.finger_index, fingerType = FingerType.INDEX)

        if (grip.middle)
            FingerOverlay(id = R.drawable.finger_middle, fingerType = FingerType.MIDDLE)
        
        if (grip.ring)
            FingerOverlay(id = R.drawable.finger_ring, fingerType = FingerType.RING)

        if (grip.little)
            FingerOverlay(id = R.drawable.finger_little, fingerType = FingerType.LITTLE)


        if(onUpdate != null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
            ) {
                val handType = if (isRightHand) HandType.RIGHT else HandType.LEFT

                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(6f)
                )
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(22.5f)
                        .clickable(interactionSource, null) {
                            onUpdate(handType, FingerType.THUMB, !grip.thumb)
                        })
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(14f)
                        .clickable(interactionSource, null) {
                            onUpdate(handType, FingerType.INDEX, !grip.index)
                        })
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(14.5f)
                        .clickable(interactionSource, null) {
                            onUpdate(handType, FingerType.MIDDLE, !grip.middle)
                        })
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(13.75f)
                        .clickable(interactionSource, null) {
                            onUpdate(handType, FingerType.RING, !grip.ring)
                        })
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(13.75f)
                        .clickable(interactionSource, null) {
                            onUpdate(handType, FingerType.LITTLE, !grip.little)
                        })
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(15.5f)
                )
            }
        }
    }
}

@Composable
private fun FingerOverlay(@DrawableRes id: Int, fingerType: FingerType) {
    Image(
        painter = painterResource(id = id),
        contentDescription = fingerType.toString(),
        Modifier.fillMaxSize(),
    )
}

@Composable
@Preview(device = Devices.PIXEL_4)
private fun HandGripPreview() {
    AppTheme {
        HandGripDual(
            left = HandGripDTO(true, true, true, false, false),
            right = HandGripDTO(true, false, true, true, true),
            modifier = Modifier.fillMaxWidth(1f)
        )
    }
}