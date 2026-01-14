package com.example.dailynotifications.data.repository.reminder

import com.example.dailynotifications.data.local.ReminderDao
import com.example.dailynotifications.data.local.ReminderEntity
import com.example.dailynotifications.data.model.Reminder
import com.example.dailynotifications.data.model.ReminderFrequency
import com.example.dailynotifications.data.remote.BackendApi
import com.example.dailynotifications.data.repository.auth.AuthRepository
import com.example.dailynotifications.data.repository.reminder.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class RoomReminderRepository(
    private val dao: ReminderDao,
    private val authRepository: AuthRepository,
    private val backendApi: BackendApi
) : ReminderRepository {
    override val reminders: Flow<List<Reminder>> =
        authRepository.authState
            .map { state -> state.email?.takeIf { state.isLoggedIn } ?: OWNER_GUEST }
            .distinctUntilChanged()
            .flatMapLatest { ownerId ->
                dao.observeRemindersByOwner(ownerId).map { entities -> entities.map { it.toModel() } }
            }

    override suspend fun loadReminders() {
        // Room Flow emits automatically.
    }

    override suspend fun addReminder(reminder: Reminder) {
        val ownerId = authRepository.authState.value.email?.takeIf {
            authRepository.authState.value.isLoggedIn
        } ?: OWNER_GUEST
        dao.insertReminder(reminder.toEntity(ownerId))
        val token = authRepository.authState.value.token
        if (!token.isNullOrBlank()) {
            runCatching { backendApi.sendReminder(token, reminder) }
        }
    }

    override suspend fun getReminder(id: String): Reminder? {
        return dao.getReminderById(id)?.toModel()
    }

    override suspend fun deleteReminder(id: String) {
        dao.deleteReminderById(id)
    }
}

private fun ReminderEntity.toModel(): Reminder {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(dateTimeMillis),
        ZoneId.systemDefault()
    )
    val freq = runCatching { ReminderFrequency.valueOf(frequency) }
        .getOrElse { ReminderFrequency.ONE_TIME }
    return Reminder(
        id = id,
        title = title,
        dateTime = dateTime,
        note = note,
        frequency = freq
    )
}

private fun Reminder.toEntity(ownerId: String): ReminderEntity {
    val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return ReminderEntity(
        id = id,
        ownerId = ownerId,
        title = title,
        dateTimeMillis = millis,
        note = note,
        frequency = frequency.name
    )
}

private const val OWNER_GUEST = "guest"
