package com.softwareoverflow.maxigriphangboardtrainer.ui.history.write

import com.softwareoverflow.maxigriphangboardtrainer.ui.workout.WorkoutSection

interface IHistorySaver {

    fun addHistory(milliseconds: Long, section: WorkoutSection, name: String)

    fun write()
}