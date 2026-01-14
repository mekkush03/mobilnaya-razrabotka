package com.example.dailynotifications.core.notifications

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dailynotifications.data.model.Reminder
import com.example.dailynotifications.data.model.ReminderFrequency
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerNotificationScheduler @Inject constructor(
    private val workManager: WorkManager
) : NotificationScheduler {
    override fun schedule(reminder: Reminder) {
        val now = LocalDateTime.now()
        val data = Data.Builder()
            .putString(NotificationConstants.KEY_TITLE, reminder.title)
            .putString(NotificationConstants.KEY_BODY, reminder.note ?: "Reminder is due.")
            .build()
        when (reminder.frequency) {
            ReminderFrequency.DAILY, ReminderFrequency.WEEKLY -> {
                val intervalDays = if (reminder.frequency == ReminderFrequency.DAILY) 1L else 7L
                val delay = Duration.between(now, reminder.dateTime).toMillis().coerceAtLeast(0)
                val request = PeriodicWorkRequestBuilder<ReminderNotificationWorker>(
                    intervalDays, TimeUnit.DAYS
                )
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build()
                workManager.enqueue(request)
            }
            ReminderFrequency.ONE_TIME, ReminderFrequency.CUSTOM -> {
                val delay = Duration.between(now, reminder.dateTime).toMillis().coerceAtLeast(0)
                val request = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build()
                workManager.enqueue(request)
            }
        }
    }
}
