package com.softwareoverflow.maxigriphangboardtrainer.ui.injection

import com.softwareoverflow.maxigriphangboardtrainer.data.history.WorkoutHistoryRoomDb
import com.softwareoverflow.maxigriphangboardtrainer.ui.history.write.HistorySaverLocal
import com.softwareoverflow.maxigriphangboardtrainer.ui.history.write.HistoryWriterLocal
import com.softwareoverflow.maxigriphangboardtrainer.ui.history.write.IHistorySaver
import com.softwareoverflow.maxigriphangboardtrainer.ui.history.write.IHistoryWriter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelInjection {
    @Provides
    @ViewModelScoped
    fun providesHistoryWriter(db: WorkoutHistoryRoomDb) : IHistoryWriter {
        return HistoryWriterLocal(db)
    }

    @Provides
    @ViewModelScoped
    fun providesHistorySaver(writer: IHistoryWriter) : IHistorySaver {
        return HistorySaverLocal(writer)
    }
}