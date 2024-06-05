package com.softwareoverflow.maxigriphangboardtrainer.data.mapper

import com.softwareoverflow.maxigriphangboardtrainer.data.Workout
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.GripTypeEntity
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.WorkoutEntity
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.WorkoutSetEntity
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO

fun Workout.toDTO(): WorkoutDTO {

    return WorkoutDTO(
        workout.id,
        workout.name,
        workoutSets.toWorkoutSetDTO(),
        workout.numReps,
        workout.recoveryTime
    )
}

fun List<Workout>.toDTO() = this.map { it.toDTO() }

fun WorkoutDTO.toWorkoutEntity(): WorkoutEntity {
    return WorkoutEntity(this.id, this.name, this.numReps, this.recoveryTime)
}

fun List<WorkoutSetDTO>.toWorkoutSetEntity(workoutId: Long): List<WorkoutSetEntity> {
    return this.map {
        WorkoutSetEntity(
            workoutId, // This will be updated later
            it.orderInWorkout!!,
            it.workTime,
            it.restTime,
            it.numReps,
            it.recoverTime,
            it.gripTypeDTO!!.toEntity()
        )
    }
}

fun List<WorkoutSetEntity>.toWorkoutSetDTO(): MutableList<WorkoutSetDTO> {
    val dtoList = ArrayList<WorkoutSetDTO>()
    forEach { dtoList.add(it.toDTO()) }
    return dtoList.sortedBy { it.orderInWorkout }.toMutableList()
}

fun WorkoutSetEntity.toDTO(): WorkoutSetDTO {
    return WorkoutSetDTO(
        gripType.toDTO(),
        this.workTime,
        this.restTime,
        this.numReps,
        this.recoverTime,
        this.orderInWorkout
    )
}

fun GripTypeEntity.toDTO(): GripTypeDTO {
    return GripTypeDTO(
        id = this.gripTypeId, name = this.name, this.leftHand, this.rightHand
    )
}


fun GripTypeDTO.toEntity(): GripTypeEntity {
    val id: Long = if (id == null) 0 else id!!
    return GripTypeEntity(id, name!!, rightHand, leftHand)
}