package com.softwareoverflow.maxigriphangboardtrainer.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.ui.theme.spacing


@Composable
fun MountainBackground(modifier: Modifier) {
    val bgImg = if (isSystemInDarkTheme()) R.drawable.mtn_bg_dark
    else R.drawable.mtn_bg_light

    Box(
        Modifier.fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = bgImg),
            contentDescription = null,
            modifier = modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun Modifier.drawWithoutRect(rect: Rect?) =
    drawWithContent {
        if (rect != null) {
            clipRect(
                left = rect.left,
                top = rect.top,
                right = rect.right,
                bottom = rect.bottom,
                clipOp = ClipOp.Difference,
            ) {
                this@drawWithContent.drawContent()
            }
        } else {
            drawContent()
        }
    }

@Composable
fun BasicTextFieldInt(initialValue: Int, textStyle: TextStyle, onChange: (String, Boolean) -> Unit){

    var input by remember { mutableStateOf(initialValue.toString()) }
    var isError by remember { mutableStateOf(false) }

    BasicTextField(
        value = input,
        onValueChange = {
            if (it.length <= 3) {
                try {
                    input = it
                    isError = input.toInt() <= 0
                } catch (_: NumberFormatException) {
                    isError = true
                }

                onChange(input, isError)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .width(
                IntrinsicSize.Min
            )
            .padding(MaterialTheme.spacing.extraExtraSmall),
        cursorBrush = SolidColor(MaterialTheme.colors.onPrimary),
        decorationBox = { innerTextField ->
            Column(
                Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                innerTextField()
                Divider(
                    Modifier.width(MaterialTheme.spacing.extraExtraLarge),
                    thickness = 3.dp,
                    color = if (isError) MaterialTheme.colors.error else MaterialTheme.colors.onPrimary
                )
            }
        },
        textStyle = textStyle,
    )
}