package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.model.Reminder

interface ReminderApi {
    suspend fun fetchReminders(): List<Reminder>
    suspend fun createReminder(reminder: Reminder): Reminder
}
