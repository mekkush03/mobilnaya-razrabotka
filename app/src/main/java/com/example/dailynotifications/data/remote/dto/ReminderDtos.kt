package com.example.dailynotifications.data.remote.dto

import com.example.dailynotifications.data.model.Reminder
import com.example.dailynotifications.data.model.ReminderFrequency
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ReminderDto(
    val id: String,
    val title: String,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val dateTime: LocalDateTime,
    val note: String? = null,
    val frequency: String
)

fun ReminderDto.toModel(): Reminder {
    return Reminder(
        id = id,
        title = title,
        dateTime = dateTime,
        note = note,
        frequency = runCatching { ReminderFrequency.valueOf(frequency) }
            .getOrElse { ReminderFrequency.ONE_TIME }
    )
}

fun Reminder.toDto(): ReminderDto {
    return ReminderDto(
        id = id,
        title = title,
        dateTime = dateTime,
        note = note,
        frequency = frequency.name
    )
}
