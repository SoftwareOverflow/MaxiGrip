package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_loader

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwareoverflow.maxigriphangboardtrainer.repository.IWorkoutRepository
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.BillingViewModel
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.UpgradeManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.view.list_adapter.workout.WorkoutLoaderDomainObject
import com.softwareoverflow.maxigriphangboardtrainer.ui.view.list_adapter.workout.WorkoutLoaderDomainObjectType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutLoaderViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val workoutRepo: IWorkoutRepository,
    private val billingViewModel: BillingViewModel
) : ViewModel() {

    private val _workouts = workoutRepo.getAllWorkouts()
    private val _workoutsSorted = MediatorLiveData<List<WorkoutLoaderDomainObject>>()
    val workouts: LiveData<List<WorkoutLoaderDomainObject>>
        get() = _workoutsSorted


    init {
        viewModelScope.launch {
            _workoutsSorted.addSource(_workouts) {
                _workoutsSorted.value = getWorkoutsToDisplay(context)
            }
        }
    }

    private fun getWorkoutsToDisplay(context: Context): List<WorkoutLoaderDomainObject> {
        val workouts = _workouts.value ?: arrayListOf()
        val domainObjects = workouts.map { WorkoutLoaderDomainObject(it) }.toMutableList().apply {
            this.forEachIndexed { index, obj ->
                if (obj.type != WorkoutLoaderDomainObjectType.USER) {
                    obj.dto.id = index * -1L
                }
            }
        }

        val placeholderUnlocked =
            WorkoutLoaderDomainObject.getPlaceholderUnlocked(context.applicationContext)
        val placeholderLocked =
            WorkoutLoaderDomainObject.getPlaceholderLocked(context.applicationContext)

        if ((!UpgradeManager.isUserUpgraded())) {
            while (domainObjects.size < billingViewModel.getMaxWorkoutSlots())
                domainObjects.add(placeholderUnlocked)

            domainObjects.add(placeholderLocked)
        }

        return domainObjects
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch {
            workoutRepo.deleteWorkoutById(id)
        }
    }
}

