package com.softwareoverflow.maxigriphangboardtrainer.repository

import androidx.lifecycle.LiveData
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO

/**
 * Repository layer of abstraction from the backing data source
 */
interface IWorkoutRepository {

    //region Workout
    fun getAllWorkouts() : LiveData<List<WorkoutDTO>>

    suspend fun getWorkoutCount(): Int

    suspend fun deleteWorkoutById(workoutId: Long)

    suspend fun getWorkoutById(workoutId: Long) : WorkoutDTO

    suspend fun createOrUpdateWorkout(dto: WorkoutDTO) : Long
    //endregion

    // region GripType
    fun getAllGripTypes(): LiveData<List<GripTypeDTO>>

    fun getGripTypeById(gripTypeId: Long?) : LiveData<GripTypeDTO>

    suspend fun createOrUpdateGripType(gripTypeDTO: GripTypeDTO) :Long

    @Throws(IllegalStateException::class)
    suspend fun deleteGripType(dto: GripTypeDTO)
    //endregion
}