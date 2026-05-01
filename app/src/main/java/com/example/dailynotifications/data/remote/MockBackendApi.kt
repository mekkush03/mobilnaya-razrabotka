package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.model.Reminder
import kotlinx.coroutines.delay

class MockBackendApi : BackendApi {
    override suspend fun fetchReminders(token: String): List<Reminder> {
        delay(150)
        return emptyList()
    }

    override suspend fun upsertReminder(token: String, reminder: Reminder): Reminder {
        delay(150)
        return reminder
    }

    override suspend fun deleteReminder(token: String, reminderId: String) {
        delay(150)
    }
}
