package com.softwareoverflow.maxigriphangboardtrainer.data.history

import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.history.WorkoutHistoryDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.workout.WorkoutSection
import java.time.LocalDate
import javax.inject.Inject

class WorkoutHistoryRoomDb @Inject constructor(private val historyDao: WorkoutHistoryDao) :
    IWorkoutHistoryRepository {

    override suspend fun createOrUpdate(workoutHistory: WorkoutHistoryDTO) {
        if (workoutHistory.type == WorkoutSection.PREPARE) return

        // We need to update the name before we search for the matching entry in the database
        workoutHistory.name =
            when (workoutHistory.type) {
                WorkoutSection.RECOVER -> WorkoutSection.RECOVER.name
                WorkoutSection.REST -> WorkoutSection.REST.name
                else -> workoutHistory.name
            }

        val dateString = HistoryConverters.historyDateFormat.format(workoutHistory.date)

        val existingData =
            historyDao.getExistingEntry(workoutHistory.name, workoutHistory.type.name, dateString)

        if (existingData != null) {
            // If we have a match we need to update the time
            workoutHistory.milliseconds += existingData.seconds * 1000
        }

        historyDao.createOrUpdate(workoutHistory.toEntity())
    }

    override suspend fun getAllHistory(): List<WorkoutHistoryDTO> {
        return historyDao.getAllHistory().map { it.toDto() }
    }

    override suspend fun getHistoryBetweenDates(
        from: LocalDate, to: LocalDate
    ): List<WorkoutHistoryDTO> {
        val fromString = HistoryConverters.historyDateFormat.format(from)
        val toString = HistoryConverters.historyDateFormat.format(to)
        return historyDao.getHistoryBetweenDates(fromString, toString).map { item -> item.toDto() }
    }
}