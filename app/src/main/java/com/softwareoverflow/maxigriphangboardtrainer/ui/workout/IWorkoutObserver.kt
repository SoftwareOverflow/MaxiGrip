package com.softwareoverflow.maxigriphangboardtrainer.ui.workout

import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO

interface IWorkoutObserver {

    /** Called whenever the timer ticks a full second
     * @param workoutRemaining seconds remaining in the workout
     * @param workoutSectionRemaining the seconds remaining in the current [WorkoutSection]
     **/
    fun onTimerTick(workoutRemaining: Int, workoutSectionRemaining: Int)

    /** Called when the [WorkoutSection] changed. Provides the new section, the current WorkoutSet, the new rep and the new set numbers **/
    fun onWorkoutSectionChange(section: WorkoutSection, currentSet: WorkoutSetDTO, currentRep: Int)

    /** Called when the workout timer completes **/
    fun onFinish()
}