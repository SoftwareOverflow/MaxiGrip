package com.softwareoverflow.maxigriphangboardtrainer.repository

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.softwareoverflow.maxigriphangboardtrainer.data.history.WorkoutHistoryDao
import com.softwareoverflow.maxigriphangboardtrainer.data.history.WorkoutHistoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    private const val historyDbName = "workout_history.db"


    @Singleton
    @Provides
    fun providesHistoryDatabase(@ApplicationContext context: Context) : WorkoutHistoryDatabase {
        val builder = Room.databaseBuilder(context, WorkoutHistoryDatabase::class.java, historyDbName)

        if(Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }

        return builder.build()
    }
}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseDaoModule {
    @Provides
    fun providesHistoryDao(db: WorkoutHistoryDatabase) : WorkoutHistoryDao = db.workoutHistoryDao
}