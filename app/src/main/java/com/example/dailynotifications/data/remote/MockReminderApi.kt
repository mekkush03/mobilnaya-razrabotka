package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.model.Reminder
import java.util.concurrent.CopyOnWriteArrayList

class MockReminderApi : ReminderApi {
    private val storage = CopyOnWriteArrayList<Reminder>()

    override suspend fun fetchReminders(): List<Reminder> {
        return storage.toList()
    }

    override suspend fun createReminder(reminder: Reminder): Reminder {
        storage.add(reminder)
        return reminder
    }
}
