package com.softwareoverflow.maxigriphangboardtrainer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.softwareoverflow.maxigriphangboardtrainer.data.dao.GripTypeDao
import com.softwareoverflow.maxigriphangboardtrainer.data.dao.WorkoutDao
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.GripTypeEntity
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.WorkoutEntity
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.WorkoutSetEntity

@Database(
    entities = [WorkoutEntity::class, WorkoutSetEntity::class, GripTypeEntity::class],
    version = 1,
    exportSchema = true
)
abstract class WorkoutDatabase : RoomDatabase() {

    /**
     * The DAO to be able to access the data in the database
     */
    abstract val workoutDao: WorkoutDao

    /**
     * DAO to access data related to exercise types"
     */
    abstract val gripTypeDao: GripTypeDao

    companion object {

        private const val DATABASE_NAME = "WorkoutDatabase"

        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getInstance(context: Context): WorkoutDatabase {

            // Multiple threads could ask for instances at the same time
            synchronized(this) {
                var instance = INSTANCE // Copy so kotlin can use smart cast
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WorkoutDatabase::class.java,
                        DATABASE_NAME
                    )
                        .createFromAsset("DefaultWorkoutDatabase")
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}