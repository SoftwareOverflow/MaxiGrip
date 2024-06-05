package com.softwareoverflow.maxigriphangboardtrainer.ui.history.write

import com.softwareoverflow.maxigriphangboardtrainer.data.history.WorkoutHistoryRoomDb
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.history.WorkoutHistoryDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HistoryWriterLocal @Inject constructor (private val db: WorkoutHistoryRoomDb) : IHistoryWriter {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun writeHistory(obj: WorkoutHistoryDTO) {
        coroutineScope.launch {
            db.createOrUpdate(obj)
        }
    }
}