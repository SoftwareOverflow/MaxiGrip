package com.softwareoverflow.maxigriphangboardtrainer.data.history

import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.history.WorkoutHistoryDTO

fun List<WorkoutHistoryDTO>.toEntity() : List<WorkoutHistoryEntity> {
    return this.map { it.toEntity() }
}

fun WorkoutHistoryDTO.toEntity(): WorkoutHistoryEntity {
    return WorkoutHistoryEntity(
        seconds = this.milliseconds.toInt() / 1000, this.name, this.type, this.date
    )
}

fun List<WorkoutHistoryEntity>.toDto() : List<WorkoutHistoryDTO> {
    return this.map { it.toDto() }
}

fun WorkoutHistoryEntity.toDto(): WorkoutHistoryDTO {
    return WorkoutHistoryDTO(milliseconds =  this.seconds * 1000L, this.name, this.type, this.date)
}