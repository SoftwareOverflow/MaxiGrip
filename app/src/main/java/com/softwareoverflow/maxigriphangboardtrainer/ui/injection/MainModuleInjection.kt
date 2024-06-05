package com.softwareoverflow.maxigriphangboardtrainer.ui.injection

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.softwareoverflow.maxigriphangboardtrainer.repository.IWorkoutRepository
import com.softwareoverflow.maxigriphangboardtrainer.repository.WorkoutRepositoryFactory
import com.softwareoverflow.maxigriphangboardtrainer.repository.billing.BillingRepository
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.BillingViewModel
import com.softwareoverflow.maxigriphangboardtrainer.ui.upgrade.MobileAdsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MainModuleInjection {


    @Provides
    fun providesMobileAdsManager(@ApplicationContext context: Context): MobileAdsManager {
        return MobileAdsManager(context)
    }

    @Provides
    fun providesSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

    @Provides
    fun providesWorkoutRepo(@ApplicationContext context: Context): IWorkoutRepository {
        return WorkoutRepositoryFactory.getInstance(context)
    }

    @Provides
    fun providesBillingRepo(@ApplicationContext context: Context): BillingRepository {
        return BillingRepository(context)
    }

    @Provides
    fun providesBillingViewModel(repository: BillingRepository): BillingViewModel {
        return BillingViewModel(repository)
    }
}