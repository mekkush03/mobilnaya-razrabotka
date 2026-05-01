package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.model.Reminder

interface BackendApi {
    suspend fun fetchReminders(token: String): List<Reminder>
    suspend fun upsertReminder(token: String, reminder: Reminder): Reminder
    suspend fun deleteReminder(token: String, reminderId: String)
}
