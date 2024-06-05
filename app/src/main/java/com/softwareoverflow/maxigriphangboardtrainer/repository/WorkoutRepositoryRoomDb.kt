package com.softwareoverflow.maxigriphangboardtrainer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.data.WorkoutDatabase
import com.softwareoverflow.maxigriphangboardtrainer.data.mapper.*
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WorkoutRepositoryRoomDb(val context: Context) : IWorkoutRepository {

    private val database = WorkoutDatabase.getInstance(context.applicationContext)
    private val workoutDao = database.workoutDao
    private val gripTypeDao = database.gripTypeDao

    override fun getAllWorkouts(): LiveData<List<WorkoutDTO>> {
        return workoutDao.getAllWorkouts().switchMap {
            MutableLiveData(it.toDTO())
        }
    }

    override suspend fun getWorkoutCount(): Int {
        return workoutDao.getWorkoutCount()
    }

    override suspend fun deleteWorkoutById(workoutId: Long) {
        val entity = getWorkoutById(workoutId).toWorkoutEntity()
        workoutDao.delete(entity)
    }

    override suspend fun getWorkoutById(workoutId: Long): WorkoutDTO {
        val workout = workoutDao.getWorkoutById(workoutId)
        return workout.toDTO()
    }

    override suspend fun createOrUpdateWorkout(dto: WorkoutDTO): Long {
        val workoutEntity = dto.toWorkoutEntity()
        val workoutSetEntityList = dto.workoutSets.toWorkoutSetEntity(
            dto.id ?: -1L
        ) // If the workout doesn't yet have an id, this will get populated later

        return workoutDao.createOrUpdate(workoutEntity, workoutSetEntityList)
    }

    override fun getAllGripTypes(): LiveData<List<GripTypeDTO>> {
        return gripTypeDao.getAllGripTypes().switchMap {
            MutableLiveData(it.map { gripTypeEntity ->  gripTypeEntity.toDTO() })
        }
    }

    override fun getGripTypeById(gripTypeId: Long?): LiveData<GripTypeDTO> {
        if (gripTypeId == null) {
            return MutableLiveData(GripTypeDTO())
        }

        // TODO - fix the case where the number passed doesn't reflect a db record and causes an IllegalStateException
        return gripTypeDao.getGripTypeById(gripTypeId).switchMap {
            MutableLiveData(it.toDTO())
        }
    }

    override suspend fun createOrUpdateGripType(gripTypeDTO: GripTypeDTO): Long {
        return gripTypeDao.createOrUpdate(gripTypeDTO.toEntity())
    }

    @Throws(IllegalStateException::class)
    override suspend fun deleteGripType(dto: GripTypeDTO) {
        withContext(Dispatchers.IO) {
            val workoutNames = workoutDao.getGripTypeWorkoutNames(dto.id!!);
            if (workoutNames.any()) {
                val error = workoutNames.toHashSet().joinToString(", ",
                    context.getString(R.string.error_delete_et_prefix, dto.name),
                    limit = 2,
                    truncated = context.getString(R.string.error_delete_et_truncated),
                    transform = { "'${it}'" },
                postfix = context.getString(R.string.error_delete_et_postfix))

                throw IllegalStateException(error);
            }

            gripTypeDao.delete(dto.toEntity())
        }
    }
}