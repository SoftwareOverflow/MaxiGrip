package com.softwareoverflow.maxigriphangboardtrainer.data.history

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WorkoutHistoryEntity::class], version = 1)
@TypeConverters(HistoryConverters::class)
abstract class WorkoutHistoryDatabase : RoomDatabase() {

    abstract val workoutHistoryDao: WorkoutHistoryDao
}