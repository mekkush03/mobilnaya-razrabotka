package com.example.dailynotifications.core.notifications

import com.example.dailynotifications.data.model.Reminder

interface NotificationScheduler {
    fun schedule(reminder: Reminder)
}
