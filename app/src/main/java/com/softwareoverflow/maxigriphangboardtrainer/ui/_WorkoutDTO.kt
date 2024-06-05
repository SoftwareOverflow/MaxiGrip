package com.softwareoverflow.maxigriphangboardtrainer.ui

import android.content.Context
import androidx.preference.PreferenceManager
import com.softwareoverflow.maxigriphangboardtrainer.R
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.SharedPreferencesManager

/**
 * Gets the duration of the workout, in seconds
 */
fun WorkoutDTO.getDuration(): Int {
    var totalTime = 0

    for (i in 0 until workoutSets.size) {
        val workoutSet = workoutSets[i]

        totalTime += (workoutSet.workTime + workoutSet.restTime) * workoutSet.numReps
        totalTime -= workoutSet.restTime // There is no rest period at the end of a workout set

        if (i != workoutSets.size - 1)
            totalTime += workoutSet.recoverTime
    }

    totalTime += recoveryTime
    totalTime *= numReps
    totalTime -= recoveryTime // Don't need to recover after the last cycle

    return totalTime
}

fun WorkoutDTO.getFormattedDuration(): String {
    val duration = getDuration()
    return String.format("%02d:%02d", duration / 60, duration % 60)
}

fun WorkoutDTO.getFullWorkoutSets(context: Context) : List<WorkoutSetDTO> {
    val updatedWorkoutSets = ArrayList(workoutSets)

    for(i in 1 until numReps) {
        updatedWorkoutSets.last().recoverTime = this.recoveryTime // Set the final recovery (otherwise unused) to be the workouts recovery time
        updatedWorkoutSets.addAll(this.workoutSets)
    }

    updatedWorkoutSets.forEachIndexed { i, dto ->
        dto.orderInWorkout = i
    }

    val prepSet = getWorkoutPrepSet(context)
    if(prepSet != null){
        updatedWorkoutSets[0].gripTypeDTO.let{
            prepSet.gripTypeDTO = prepSet.gripTypeDTO.copy(leftHand = it.leftHand, rightHand = it.rightHand)

            it.name?.let { name ->
            prepSet.gripTypeDTO = prepSet.gripTypeDTO.copy(name = name)
        }}


        updatedWorkoutSets.add(0, prepSet)
    }

    return updatedWorkoutSets
}

fun getWorkoutPrepSet(context: Context): WorkoutSetDTO? {
    val sp = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    val isEnabled = sp.getBoolean(SharedPreferencesManager.prepSetEnabled, true)

    if(isEnabled){
        return WorkoutSetDTO(
            GripTypeDTO(
                null,
                context.getString(R.string.get_ready),
            ),
            sp.getString(SharedPreferencesManager.prepSetTime, "5")!!.toInt(),
            0,
            1,
            0
        )
    }
    else {
        return null
    }
}

fun getWorkoutCompleteGripType(context: Context): GripTypeDTO = GripTypeDTO(
    null,
    context.getString(R.string.workout_complete),
)

fun WorkoutDTO.getTotalWorkTime(): Int {
    var totalWorkTime = 0

    for (i in 0 until workoutSets.size) {
        val workoutSet = workoutSets[i]

        totalWorkTime += workoutSet.workTime * workoutSet.numReps
    }

    return totalWorkTime * numReps
}

fun WorkoutDTO.getTotalRestTime(): Int {
    var totalRestTime = 0

    for (i in 0 until workoutSets.size) {
        val workoutSet = workoutSets[i]

        totalRestTime += workoutSet.restTime * (workoutSet.numReps - 1) // The final rep of the workoutSet is a recover
    }

    return totalRestTime * numReps
}

fun WorkoutDTO.getTotalRecoverTime(): Int {
    var totalRecoveryTime = 0

    for (i in 0 until workoutSets.size - 1) { // No recovery on the final workoutSet as it's the end of the workout, or a looped workout.
        val workoutSet = workoutSets[i]

        totalRecoveryTime += workoutSet.recoverTime

    }

    totalRecoveryTime *= numReps // Repeat the number of reps

    totalRecoveryTime += (numReps - 1) * recoveryTime // Add the recovery times between repeats

    return totalRecoveryTime
}