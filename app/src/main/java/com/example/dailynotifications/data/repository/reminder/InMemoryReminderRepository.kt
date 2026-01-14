package com.example.dailynotifications.data.repository.reminder

import com.example.dailynotifications.data.model.Reminder
import com.example.dailynotifications.data.remote.ReminderApi
import com.example.dailynotifications.data.repository.reminder.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryReminderRepository(
    private val api: ReminderApi
) : ReminderRepository {
    private val mutex = Mutex()
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    override val reminders: Flow<List<Reminder>> = _reminders

    override suspend fun loadReminders() {
        mutex.withLock {
            _reminders.value = api.fetchReminders()
        }
    }

    override suspend fun addReminder(reminder: Reminder) {
        mutex.withLock {
            api.createReminder(reminder)
            _reminders.update { current -> current + reminder }
        }
    }

    override suspend fun getReminder(id: String): Reminder? {
        return _reminders.value.firstOrNull { it.id == id }
    }

    override suspend fun deleteReminder(id: String) {
        _reminders.update { current -> current.filterNot { it.id == id } }
    }
}
