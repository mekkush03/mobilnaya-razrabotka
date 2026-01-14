package com.example.dailynotifications.data.model

import java.time.LocalDateTime
import java.util.UUID

data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dateTime: LocalDateTime,
    val note: String?,
    val frequency: ReminderFrequency
)
