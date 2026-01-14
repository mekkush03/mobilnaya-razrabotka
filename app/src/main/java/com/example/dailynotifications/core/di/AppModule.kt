package com.example.dailynotifications.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.WorkManager
import com.example.dailynotifications.core.notifications.NotificationScheduler
import com.example.dailynotifications.core.notifications.WorkManagerNotificationScheduler
import com.example.dailynotifications.data.local.AppDatabase
import com.example.dailynotifications.data.local.ReminderDao
import com.example.dailynotifications.data.remote.BackendApi
import com.example.dailynotifications.data.remote.MockBackendApi
import com.example.dailynotifications.data.remote.MockReminderApi
import com.example.dailynotifications.data.remote.ReminderApi
import com.example.dailynotifications.data.repository.auth.AuthRepository
import com.example.dailynotifications.data.repository.auth.SharedPrefsAuthRepository
import com.example.dailynotifications.data.repository.reminder.ReminderRepository
import com.example.dailynotifications.data.repository.reminder.RoomReminderRepository
import com.example.dailynotifications.data.repository.settings.InMemorySettingsRepository
import com.example.dailynotifications.data.repository.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideReminderApi(): ReminderApi = MockReminderApi()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(prefs: SharedPreferences): AuthRepository {
        return SharedPrefsAuthRepository(prefs)
    }

    @Provides
    @Singleton
    fun provideBackendApi(): BackendApi = MockBackendApi()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "reminders.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideReminderDao(database: AppDatabase): ReminderDao = database.reminderDao()

    @Provides
    @Singleton
    fun provideReminderRepository(
        dao: ReminderDao,
        authRepository: AuthRepository,
        backendApi: BackendApi
    ): ReminderRepository {
        return RoomReminderRepository(dao, authRepository, backendApi)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsRepository {
        return InMemorySettingsRepository()
    }

    @Provides
    @Singleton
    fun provideNotificationScheduler(
        workManager: WorkManager
    ): NotificationScheduler {
        return WorkManagerNotificationScheduler(workManager)
    }
}
