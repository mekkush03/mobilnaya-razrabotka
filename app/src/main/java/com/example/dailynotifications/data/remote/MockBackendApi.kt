package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.model.Reminder
import kotlinx.coroutines.delay

class MockBackendApi : BackendApi {
    override suspend fun sendReminder(token: String, reminder: Reminder) {
        delay(150)
    }
}
