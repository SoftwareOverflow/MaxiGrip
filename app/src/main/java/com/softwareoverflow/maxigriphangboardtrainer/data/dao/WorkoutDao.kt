package com.softwareoverflow.maxigriphangboardtrainer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.softwareoverflow.maxigriphangboardtrainer.data.Workout
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.WorkoutEntity
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.WorkoutSetEntity

@Dao
interface WorkoutDao :
    BaseDao<WorkoutEntity> {

    @Query("SELECT * FROM Workout WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Long) : Workout

    @Query("SELECT * FROM Workout")
    fun getAllWorkouts(): LiveData<List<Workout>>

    @Query("SELECT COUNT(*) FROM workout")
    suspend fun getWorkoutCount(): Int

    @Transaction
    suspend fun createOrUpdate(workout: WorkoutEntity, workoutSets: List<WorkoutSetEntity>): Long {
        val id = createOrUpdate(workout)

        workoutSets.forEach {
            it.workoutId = id
        }

        createOrUpdateWorkoutSets(workoutSets)

        return id
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createOrUpdateWorkoutSets(workoutSets: List<WorkoutSetEntity>)

    @Query("SELECT workout.name FROM Workout workout JOIN WorkoutSet workout_set ON workout.id = workout_set.workoutId WHERE workout_set.gripTypeId = :gripTypeId")
    fun getGripTypeWorkoutNames(gripTypeId: Long) : List<String>
}