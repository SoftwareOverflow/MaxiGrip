package com.softwareoverflow.maxigriphangboardtrainer.ui.workout

import android.os.CountDownTimer
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.history.write.IHistorySaver
import com.softwareoverflow.maxigriphangboardtrainer.ui.workout.media.WorkoutMediaManager
import timber.log.Timber
import kotlin.math.abs

class WorkoutTimer(
    workoutDurationSeconds: Int,
    workoutSetList: List<WorkoutSetDTO>,
    hasPrepSet: Boolean,
    private val mediaManager: WorkoutMediaManager,
    private val historySaver: IHistorySaver,
    private val observer: IWorkoutObserver
) {
    private lateinit var timer: CountDownTimer
    private var millisecondsRemaining: Long = workoutDurationSeconds * 1000L

    private var isRunning = false
    private var isPaused: Boolean = false

    private var workoutSets = workoutSetList.iterator()
    private var currentSection: WorkoutSection
    private var currentSet = workoutSets.next()
    private var currentSetIndex = 0
    private var currentRep = 1
    private var millisRemainingInSection = getCurrentSetWorkTime()

    init {
        createTimer(millisecondsRemaining)

        currentSection =
            if (hasPrepSet) WorkoutSection.PREPARE
            else WorkoutSection.HANG

        observer.onWorkoutSectionChange(currentSection, currentSet, currentRep)
    }

    /** Skips the current section of the workout **/
    fun skip() {
        millisecondsRemaining -= millisRemainingInSection
        millisRemainingInSection = 0
        /*millisecondsRemaining =
            ((millisecondsRemaining + 999) / 1000) * 1000 // Round up to the nearest second (in millis) to prevent the frequent polling of the timer getting out of sync*/

        if (millisecondsRemaining <= 0) {
            observer.onFinish()

            cancel()

            return
        }

        val currentSound = mediaManager.playSound
        mediaManager.toggleSound(false) // Mute all noises when skipping through
        startNextWorkoutSection()
        mediaManager.toggleSound(currentSound)

        // Update any observers with the new values
        observer.onTimerTick(
            (millisecondsRemaining / 1000).toInt(),
            (millisRemainingInSection / 1000).toInt()
        )

        // Cancel and recreate the timer
        isRunning = false
        timer.cancel()

        if (!isPaused) {
            createTimer(millisecondsRemaining)
            isRunning = true
            timer.start()
        }
    }

    /** Allows the pausing / resuming of the timer**/
    fun togglePause(isPaused: Boolean) {
        this.isPaused = isPaused

        if (isPaused) {
            isRunning = false
            timer.cancel()
        } else if (!isRunning) {
            createTimer(millisecondsRemaining)

            isRunning = true
            timer.start()
        }
    }

    /** Allows muting / un-muting **/
    fun toggleSound(playSound: Boolean) {
        mediaManager.toggleSound(playSound)
    }

    private fun createTimer(millis: Long) {
        timer = object : CountDownTimer(millis, 100L) {
            override fun onFinish() {
                observer.onFinish()

                cancel()
            }

            override fun onTick(millisUntilFinished: Long) {
                val thisTick = millisecondsRemaining - millisUntilFinished
                val didChangeSecond =
                    millisUntilFinished / 1000 != (millisUntilFinished + thisTick) / 1000

                millisRemainingInSection -= thisTick
                millisecondsRemaining -= thisTick

                historySaver.addHistory(
                    thisTick,
                    currentSection,
                    if (currentSection == WorkoutSection.HANG) currentSet.gripTypeDTO.name!! else currentSection.name
                )

                if (didChangeSecond) {
                    if (currentSection == WorkoutSection.HANG)
                        when (millisRemainingInSection / 1000 + 1) { // It gets rounded down, and we want to know as soon
                            15L -> mediaManager.playSound(WorkoutMediaManager.WorkoutSound.SOUND_VOCAL_15)
                            10L -> mediaManager.playSound(WorkoutMediaManager.WorkoutSound.SOUND_VOCAL_10)
                            5L -> mediaManager.playSound(WorkoutMediaManager.WorkoutSound.SOUND_VOCAL_5)
                        }

                    if (millisRemainingInSection > 0) {
                        when (millisRemainingInSection / 1000) {
                            2L, 1L, 0L -> {
                                Timber.d("321 with $millisRemainingInSection")
                                mediaManager.playSound(WorkoutMediaManager.WorkoutSound.SOUND_321)
                            }
                        }
                    }

                    observer.onTimerTick(
                        ((millisecondsRemaining) / 1000).toInt() + 1,
                        ((millisRemainingInSection) / 1000).toInt() + 1
                    )
                }

                if (millisRemainingInSection <= 0 && millisUntilFinished > 0)
                    startNextWorkoutSection()

            }
        }
    }

    private fun startNextWorkoutSection() {
        millisecondsRemaining += abs(millisRemainingInSection) // Add back any overshoot to keep in sync

        if (currentSection == WorkoutSection.PREPARE) {
            // Begin the workout
            currentSection = WorkoutSection.HANG

            if (workoutSets.hasNext())
                currentSet = workoutSets.next()
            millisRemainingInSection = getCurrentSetWorkTime()
        } else if (currentRep == currentSet.numReps) {
            if (currentSection == WorkoutSection.RECOVER) {
                // Start the new workout set
                currentRep = 1
                currentSection = WorkoutSection.HANG

                if (workoutSets.hasNext())
                    currentSet = workoutSets.next()

                millisRemainingInSection = getCurrentSetWorkTime()
                currentSetIndex++

            } else {
                // Start the recovery period
                currentSection = WorkoutSection.RECOVER
                millisRemainingInSection = getCurrentSetRecoverTime()
            }
        } else {
            if (currentSection == WorkoutSection.HANG) {
                // Start the rest period
                currentSection = WorkoutSection.REST
                millisRemainingInSection = getCurrentSetRestTime()

            } else if (currentSection == WorkoutSection.REST) {
                // Start the next rep
                currentRep++
                currentSection = WorkoutSection.HANG
                millisRemainingInSection = getCurrentSetWorkTime()
            }
        }

        Timber.d("sectionSound with $millisRemainingInSection")
        mediaManager.playSound(currentSection)
        observer.onWorkoutSectionChange(currentSection, currentSet, currentRep)
        observer.onTimerTick(
            millisecondsRemaining.toInt() / 1000,
            millisRemainingInSection.toInt() / 1000
        )
    }

    fun start() {
        isRunning = true
        timer.start()
    }

    fun cancel() {
        mediaManager.onDestroy()
        timer.cancel()

        historySaver.write()
    }

    private fun getCurrentSetWorkTime() = currentSet.workTime * 1000L
    private fun getCurrentSetRestTime() = currentSet.restTime * 1000L
    private fun getCurrentSetRecoverTime() = currentSet.recoverTime * 1000L
}

enum class WorkoutSection {
    PREPARE, HANG, REST, RECOVER
}