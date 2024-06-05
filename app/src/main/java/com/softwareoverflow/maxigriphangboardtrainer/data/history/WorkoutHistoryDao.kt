package com.softwareoverflow.maxigriphangboardtrainer.data.history

import androidx.room.Dao
import androidx.room.Query
import com.softwareoverflow.maxigriphangboardtrainer.data.dao.BaseDao

@Dao
interface WorkoutHistoryDao : BaseDao<WorkoutHistoryEntity> {

    @Query("Select * FROM workout_history ORDER BY _date_string ASC")
    suspend fun getAllHistory(): List<WorkoutHistoryEntity>

    @Query("SELECT * FROM workout_history WHERE _date_string >= :fromDate AND _date_string <= :toDate ORDER BY _date_string ASC")
    suspend fun getHistoryBetweenDates(fromDate: String, toDate: String): List<WorkoutHistoryEntity>

    @Query("SELECT * FROM workout_history WHERE _name = :name AND _type = :type AND _date_string = :date")
    suspend fun getExistingEntry(name: String, type: String, date: String) : WorkoutHistoryEntity?
}