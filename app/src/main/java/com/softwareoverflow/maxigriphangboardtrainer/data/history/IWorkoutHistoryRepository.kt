package com.softwareoverflow.maxigriphangboardtrainer.data.history

import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.history.WorkoutHistoryDTO
import java.time.LocalDate

interface IWorkoutHistoryRepository {

    suspend fun createOrUpdate(workoutHistory: WorkoutHistoryDTO)

    suspend fun getAllHistory() : List<WorkoutHistoryDTO>

    suspend fun getHistoryBetweenDates(from: LocalDate, to: LocalDate) : List<WorkoutHistoryDTO>
}