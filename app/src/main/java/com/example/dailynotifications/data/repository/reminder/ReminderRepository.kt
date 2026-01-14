package com.example.dailynotifications.data.repository.reminder

import com.example.dailynotifications.data.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    val reminders: Flow<List<Reminder>>
    suspend fun loadReminders()
    suspend fun addReminder(reminder: Reminder)
    suspend fun getReminder(id: String): Reminder?
    suspend fun deleteReminder(id: String)
}
