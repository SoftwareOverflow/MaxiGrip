package com.softwareoverflow.maxigriphangboardtrainer.ui.history.write

import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.history.WorkoutHistoryDTO

interface IHistoryWriter {

    fun writeHistory(obj: WorkoutHistoryDTO)

}