package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator

import androidx.lifecycle.ViewModel
import com.softwareoverflow.maxigriphangboardtrainer.repository.IWorkoutRepository
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for creating / editing workouts.
 *
 * @param repo the repository to use for saving the workout
 * @param dto the WorkoutDTO to edit
 */
class WorkoutCreatorViewModel (private val repo: IWorkoutRepository, dto: WorkoutDTO, showSaveWarning: Boolean) :
    ViewModel() {

    private var _workout: MutableStateFlow<WorkoutDTO> = MutableStateFlow(dto)
    val workout: StateFlow<WorkoutDTO> get() = _workout

    val showUnsavedChangesWarning = showSaveWarning

    fun removeWorkoutSetFromWorkout(position: Int) {
        val workoutSets = _workout.value.workoutSets.sortedBy { it.orderInWorkout }.toMutableList()
        val dto = workoutSets.singleOrNull { it.orderInWorkout == position }

        dto?.let {

            workoutSets.remove(dto)
            // Reduce all orderInWorkout values by 1
            for (i in position until workoutSets.size)
                workoutSets[i].orderInWorkout = workoutSets[i].orderInWorkout!! - 1


            _workout.value =
                _workout.value.copy(workoutSets = workoutSets.sortedBy { it.orderInWorkout }
                    .toMutableList())
        }
    }

    fun changeWorkoutSetOrder(fromPosition: Int, toPosition: Int) {
        val currentWorkout = _workout.value.copy()

        if (fromPosition < 0 || toPosition < 0 ||
            fromPosition >= currentWorkout.workoutSets.size || toPosition >= currentWorkout.workoutSets.size
        )
            return // Just ignore any attempts to reorder items in an impossible fashion. See the [WorkoutSetListAdapter] for a to-do to stop this options being enabled.

        val workoutSets = currentWorkout.workoutSets
        val from = workoutSets.singleOrNull { it.orderInWorkout == fromPosition }
        val to = workoutSets.singleOrNull { it.orderInWorkout == toPosition }

        if (from != null && to != null) {
            from.orderInWorkout = toPosition
            to.orderInWorkout = fromPosition
        }

        _workout.value =
            _workout.value.copy(workoutSets = workoutSets.sortedBy { it.orderInWorkout }
                .toMutableList())
    }

    fun getWorkoutSetToEdit(position: Int?): WorkoutSetDTO {
        if (position == null) {
            // Default the values to the previous WorkoutSet when creating a new WorkoutSet
            return WorkoutSetDTO().apply {
                val existingSets = _workout.value.workoutSets
                if (existingSets.any()) {
                    val mostRecentSet = existingSets.last()

                    this.workTime = mostRecentSet.workTime
                    this.restTime = mostRecentSet.restTime
                    this.numReps = mostRecentSet.numReps
                    this.recoverTime = mostRecentSet.recoverTime
                }
            }
        } else {
            return _workout.value.workoutSets[position]
        }
    }

    /**
     * If the workoutSet has an id already present in the list, that entry will be updated.
     * If the workoutSet has an id not already in the list, the item will be appended to the list
     */
    fun addOrUpdateWorkoutSet(dto: WorkoutSetDTO) {
        val currentWorkout = _workout.value.copy()

        dto.orderInWorkout.let {
            when {
                it != null -> {
                    val workoutSets =
                        currentWorkout.workoutSets.sortedBy { dto -> dto.orderInWorkout }
                            .toMutableList()
                    workoutSets.removeAt(it)
                    workoutSets.add(it, dto)

                    currentWorkout.workoutSets = workoutSets
                }

                else -> {
                    dto.orderInWorkout = currentWorkout.workoutSets.size
                    currentWorkout.workoutSets.add(dto)
                }
            }
        }
        _workout.value = currentWorkout
    }

    fun setRepeatCount(repeatCount: Int, recoveryTime: Int) {
        _workout.value = _workout.value.copy(numReps = repeatCount, recoveryTime = recoveryTime)
    }

    fun onWorkoutSaved(id: Long, name: String){
        _workout.value = _workout.value.copy(id = id, name = name)
    }
}

