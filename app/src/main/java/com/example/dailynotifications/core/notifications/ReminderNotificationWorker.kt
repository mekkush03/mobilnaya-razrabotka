package com.example.dailynotifications.core.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val title = inputData.getString(NotificationConstants.KEY_TITLE) ?: "Reminder"
        val body = inputData.getString(NotificationConstants.KEY_BODY)
        val helper = NotificationHelper(applicationContext)
        helper.ensureChannel()
        val notification = helper.buildNotification(title, body).build()
        NotificationManagerCompat.from(applicationContext)
            .notify(System.currentTimeMillis().toInt(), notification)
        return Result.success()
    }
}
