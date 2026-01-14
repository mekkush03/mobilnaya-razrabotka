package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.model.Reminder

interface BackendApi {
    suspend fun sendReminder(token: String, reminder: Reminder)
}
