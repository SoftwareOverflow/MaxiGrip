package com.softwareoverflow.maxigriphangboardtrainer.ui.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose.ColouredIcon
import kotlin.math.abs

@Composable
fun TriangularAnimation(modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    val t by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "time"
    )

    val t2 = (t + 1f / 3f) % 1f
    val t3 = (t + 2f / 3f) % 1f

    val pos1 = GetTrianglePosByTime(t)
    val pos2 = GetTrianglePosByTime(t2)
    val pos3 = GetTrianglePosByTime(t3)

    val density = LocalDensity.current
    var boxSize by remember {
        mutableStateOf(0.dp)
    }
    var widthOffset by remember {
        mutableStateOf(0.dp)
    }

    val iconSize = boxSize * 0.4f

    Box(modifier.onSizeChanged {
        with(density) {
            boxSize = min(it.height.toDp(), it.width.toDp())
            widthOffset = (it.width.toDp() - boxSize) / 2f
        }
    }) {
        DrawIcon(icon = ColouredIcon.WORK, pos = pos1, iconSize, boxSize, widthOffset)
        DrawIcon(icon = ColouredIcon.REST, pos = pos2, iconSize, boxSize, widthOffset)
        DrawIcon(icon = ColouredIcon.RECOVER, pos = pos3, iconSize, boxSize, widthOffset)
    }
}

@Composable
private fun DrawIcon(
    icon: ColouredIcon,
    pos: Pair<Float, Float>,
    iconSize: Dp,
    boxSize: Dp,
    widthOffset: Dp
) {
    Box(
        Modifier
            .fillMaxSize()
            .offset(
                pos.first.dp * (boxSize.value - iconSize.value) + widthOffset,
                pos.second.dp * (boxSize.value - iconSize.value)
            )
    ) {
        ColouredIcon(
            icon = icon, Modifier
                .size(iconSize)
        )
    }
}

private fun GetTrianglePosByTime(progress: Float): Pair<Float, Float> {

    // Assuming Bottom left -> Bottom right -> Top middle

    if (progress <= 1f / 3f) // Bottom leg
        return Pair(1 * progress * 3f, 1f)
    else if (progress <= 2f / 3f) // bottom right -> top middle
        return Pair(1f - (((progress * 3f)) - 1f) / 2f, 2 - progress * 3f)

    // Top middle -> bottom left
    else return Pair(1f - ((progress * 3f) - 1f) / 2f, abs(2 - progress * 3f))

    // 2/3 -> 0
    // 3/3 -> 1
}


@Composable
@Preview
private fun Preview() {
    TriangularAnimation(Modifier.fillMaxSize())
}