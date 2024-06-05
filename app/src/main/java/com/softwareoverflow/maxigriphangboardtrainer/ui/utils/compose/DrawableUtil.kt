package com.softwareoverflow.maxigriphangboardtrainer.ui.utils.compose

import android.content.Context
import androidx.annotation.DrawableRes
import com.softwareoverflow.maxigriphangboardtrainer.R


@DrawableRes
fun getDrawableId(name: String, context: Context): Int {
    return context.resources.getIdentifier(
        name,
        "drawable",
        context.packageName
    )
}
