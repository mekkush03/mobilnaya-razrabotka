package com.example.dailynotifications.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val title: String,
    val dateTimeMillis: Long,
    val note: String?,
    val frequency: String
)
