package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_saver

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwareoverflow.maxigriphangboardtrainer.repository.IWorkoutRepository
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.BillingViewModel
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.UpgradeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
open class WorkoutSaverViewModel @Inject constructor(
    private val workoutRepo: IWorkoutRepository,
    private val billingViewModel: BillingViewModel
) : ViewModel() {
    val savedWorkouts = workoutRepo.getAllWorkouts()

    private val _currentSelectedId = MutableStateFlow<Long?>(null)
    val currentSelectedId: StateFlow<Long?> get() = _currentSelectedId

    private var isInitialized = false

    private var saveSlotsLeft = MutableStateFlow(false)

    private var _canSaveWorkout = MutableStateFlow(false)
    val canSaveWorkout : StateFlow<Boolean>
        get() = _canSaveWorkout


    fun setCurrentlySelectedId(selected: Long?) {
        _currentSelectedId.value = selected

        viewModelScope.launch {
            checkCanSaveWorkout()
        }
    }

    fun initialize(idToOverwrite: Long?) {
        if (!isInitialized) {
            setCurrentlySelectedId(idToOverwrite)
            isInitialized = true
        }
    }

    init {
        viewModelScope.launch {
            UpgradeManager.userUpgradedFlow.collect {
                Timber.d("userUpgraded: $it")
                checkCanSaveWorkout()
            }
        }

    }

    private suspend fun checkCanSaveWorkout() {
        val isOverwriting = _currentSelectedId.value != null
        if(isOverwriting) {
            _canSaveWorkout.value = true
        }
        else {
            val numSavedWorkouts =  workoutRepo.getWorkoutCount()
            saveSlotsLeft.value = numSavedWorkouts < billingViewModel.getMaxWorkoutSlots()
            _canSaveWorkout.value = saveSlotsLeft.value
        }
    }

    open fun saveWorkout(
        workout: WorkoutDTO,
        newWorkoutName: String,
        idToOverwrite: Long? = null
    ): Long? {
        if (idToOverwrite == null && !saveSlotsLeft.value) {
            return null
        }

        if (newWorkoutName.length > 30 || newWorkoutName.isBlank()) {
            return null
        }

        val workoutSets = workout.workoutSets.sortedBy { it.orderInWorkout }.toMutableList()
        for (i in 0 until workoutSets.size) {
            workoutSets[i].orderInWorkout = i
        }

        val workoutToSave = workout.copy(
            id = idToOverwrite,
            name = newWorkoutName,
            workoutSets = workoutSets
        )
        return runBlocking {
            val id = workoutRepo.createOrUpdateWorkout(workoutToSave)

            viewModelScope.launch {
                val numSavedWorkouts =  workoutRepo.getWorkoutCount()

                saveSlotsLeft.value = numSavedWorkouts < billingViewModel.getMaxWorkoutSlots()
                checkCanSaveWorkout()
            }

            return@runBlocking id
        }
    }

    fun upgrade(activity: Activity) {
        billingViewModel.purchasePro(activity)
    }
}