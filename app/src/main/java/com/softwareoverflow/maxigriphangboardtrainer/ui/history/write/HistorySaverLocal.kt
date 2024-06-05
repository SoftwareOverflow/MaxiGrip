package com.softwareoverflow.maxigriphangboardtrainer.ui.history.write

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.history.WorkoutHistoryDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.workout.WorkoutSection
import java.time.LocalDate
import javax.inject.Inject

class HistorySaverLocal @Inject constructor(private val historyWriter: IHistoryWriter) :
    IHistorySaver {

        // TODO work out how this will work with milliseconds
    var history: WorkoutHistoryDTO? = null

    override fun addHistory(milliseconds: Long, section: WorkoutSection, name: String) {
        try {
            val dateNow = LocalDate.now()

            if (history == null) {
                createDTO(section, name, dateNow)
            }

            history?.apply {
                if (this.date != dateNow || section != this.type || name != this.name) {
                    write()

                    createDTO(section, name, dateNow)
                }

                when (section) {
                    WorkoutSection.HANG -> this.milliseconds += milliseconds
                    WorkoutSection.REST -> this.milliseconds += milliseconds
                    WorkoutSection.RECOVER -> this.milliseconds += milliseconds
                    else -> { /* Do Nothing */
                    }
                }
            }
        } catch (e: Exception) {
            // Blanket catch all - we don't want any problems here to interfere with the main app
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun createDTO(section: WorkoutSection, name: String, date: LocalDate) {
        history = WorkoutHistoryDTO(1000, name, section, date)
    }

    override fun write() {
        try {
            history?.let {
                historyWriter.writeHistory(it.copy())

                history = null
            }
        } catch(e: Exception) {
            // Blanket catch all - we don't want any problems here to interfere with the main app
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}