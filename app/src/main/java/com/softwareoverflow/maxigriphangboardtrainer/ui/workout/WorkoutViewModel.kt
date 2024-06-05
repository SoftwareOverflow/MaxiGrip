package com.softwareoverflow.maxigriphangboardtrainer.ui.workout

import android.content.Context
import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.getDuration
import com.softwareoverflow.maxigriphangboardtrainer.ui.getFullWorkoutSets
import com.softwareoverflow.maxigriphangboardtrainer.ui.getWorkoutCompleteGripType
import com.softwareoverflow.maxigriphangboardtrainer.ui.getWorkoutPrepSet
import com.softwareoverflow.maxigriphangboardtrainer.ui.history.write.IHistorySaver
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.SharedPreferencesManager
import com.softwareoverflow.maxigriphangboardtrainer.ui.workout.media.WorkoutMediaManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val mediaManager: WorkoutMediaManager,
    private val historySaver: IHistorySaver
) : IWorkoutObserver, ViewModel() {
    private val workoutCompleteGripType = getWorkoutCompleteGripType(context)

    private val _workout = MutableStateFlow(WorkoutDTO())

    val workout: StateFlow<WorkoutDTO> get() = _workout

    private lateinit var timer: WorkoutTimer

    private val _fullWorkoutSets by lazy {
        _workout.value.getFullWorkoutSets(context)
    }

    // Start with a default UI state. Not a nice solution but it will do for now.
    private lateinit var _uiState: MutableStateFlow<UiState>
    val uiState: StateFlow<UiState> get() = _uiState

    private val _isFinished = MutableStateFlow(false)
    val isWorkoutFinished: StateFlow<Boolean> get() = _isFinished

    private var isInitialized = false

    fun initialize(context: Context, workoutDto: WorkoutDTO) {
        if (isInitialized) return

        _workout.value = workoutDto


        var duration = workoutDto.getDuration()
        val allSets = workoutDto.getFullWorkoutSets(context.applicationContext)

        val prepSet = getWorkoutPrepSet(context.applicationContext)
        prepSet?.let {
            duration += it.workTime
        }
        val hasPrepSet = prepSet != null

        _workout.value = _workout.value.copy(
            workoutSets = allSets.toMutableList() // Deep copy the list so we don't end up saving the modified version
        )

        _uiState = MutableStateFlow(
            UiState(
                currentWorkoutSet = allSets.first(),
                upNextGripType = if(allSets.size > 1) allSets[1].gripTypeDTO else workoutCompleteGripType,
                showUpNextGripType = false,
                currentSection = if (hasPrepSet) WorkoutSection.PREPARE else WorkoutSection.HANG,
                sectionTimeRemainingValue = allSets.first().workTime,
                workoutTimeRemainingValue = _workout.value.getDuration(),
                currentRepValue = 1,

                isPaused = false,
                isSoundOn = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
                    .getBoolean(SharedPreferencesManager.playWorkoutSounds, true)
            )
        )

        timer = WorkoutTimer(
            duration,
            allSets,
            hasPrepSet,
            mediaManager,
            historySaver,
            this
        )
        timer.start()


        isInitialized = true
    }


    private fun cancel() {
        timer.cancel()
    }

    override fun onCleared() {
        cancel()
        super.onCleared()
    }

    data class UiState(
        val currentWorkoutSet: WorkoutSetDTO,
        val upNextGripType: GripTypeDTO,
        val showUpNextGripType: Boolean,
        val currentSection: WorkoutSection = WorkoutSection.PREPARE,
        val sectionTimeRemainingValue: Int = 0,
        val workoutTimeRemainingValue: Int = 0,
        val currentRepValue: Int = 0,

        val isPaused: Boolean = false,
        val isSoundOn: Boolean = true
    ) {
        val currentGripType: GripTypeDTO = currentWorkoutSet.gripTypeDTO

        val sectionTimeRemaining: String = DateUtils.formatElapsedTime(
            sectionTimeRemainingValue.toLong()
        )

        val workoutTimeRemaining: String = DateUtils.formatElapsedTime(
            workoutTimeRemainingValue.toLong()
        )

        val currentRep: String = "${currentRepValue}/${currentWorkoutSet.numReps}"
    }

    // region user controlled buttons
    fun toggleSound() {
        _uiState.value = _uiState.value.copy(
            isSoundOn = !_uiState.value.isSoundOn
        )
        timer.toggleSound(_uiState.value.isSoundOn)
    }

    fun togglePause() {
        _uiState.value = _uiState.value.copy(
            isPaused = !_uiState.value.isPaused
        )
        timer.togglePause(_uiState.value.isPaused)
    }

    fun skipSection() {
        timer.skip()
    }
    //endregion

    //region IWorkoutObserver Overrides
    override fun onTimerTick(workoutRemaining: Int, workoutSectionRemaining: Int) {
        // Show the upcoming exercise type

        val currentSet = _uiState.value.currentWorkoutSet


        val currentIndex = _fullWorkoutSets.indexOf(currentSet)

        val showNextGripType =
            if (_uiState.value.currentSection == WorkoutSection.RECOVER && _uiState.value.sectionTimeRemainingValue <= 10 &&
                currentSet.gripTypeDTO.id != null
            ) { // We don't want it popping up between the end of prepare, so check for a valid gripTypeId
                true
            } else (currentIndex == _fullWorkoutSets.size - 1) && uiState.value.currentRepValue == currentSet.numReps

        _uiState.value = _uiState.value.copy(
            sectionTimeRemainingValue = workoutSectionRemaining,
            workoutTimeRemainingValue = workoutRemaining,
            showUpNextGripType = showNextGripType
        )
    }

    override fun onWorkoutSectionChange(
        section: WorkoutSection, currentSet: WorkoutSetDTO, currentRep: Int
    ) {

        var sectionUiState = _uiState.value.currentSection
        var workoutSetUiState = _uiState.value.currentWorkoutSet
        var upNextGripTypeUiState = _uiState.value.upNextGripType
        var repUiState = _uiState.value.currentRepValue

        if (sectionUiState != section) sectionUiState = section

        if (workoutSetUiState != currentSet) {
            workoutSetUiState = currentSet

            val currentIndex = _fullWorkoutSets.indexOf(currentSet)

            // Reset the up next icon as the set has just changed
            upNextGripTypeUiState =
                if (currentIndex < _fullWorkoutSets.size - 1) _fullWorkoutSets[currentIndex + 1].gripTypeDTO else workoutCompleteGripType

        }

        if (repUiState != currentRep) repUiState = currentRep

        /*        // Show the up next icon for prepare / recover
                if ((section == WorkoutSection.PREPARE || section == WorkoutSection.RECOVER)) {
                    val nextWorkoutSetIndex = _fullWorkoutSets.indexOf(workoutSetUiState) + 1

                    if (nextWorkoutSetIndex != _fullWorkoutSets.size) {
                        upNextGripTypeUiState = _fullWorkoutSets[nextWorkoutSetIndex].gripTypeDTO
                    }
                }*/

        _uiState.value = _uiState.value.copy(
            currentWorkoutSet = workoutSetUiState,
            currentSection = sectionUiState,
            upNextGripType = upNextGripTypeUiState,
            currentRepValue = repUiState,

            )
    }

    override fun onFinish() {
        _isFinished.value = true
        cancel()
    }

//endregion
}
