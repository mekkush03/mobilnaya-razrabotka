package com.example.dailynotifications.backend.repository

import com.example.dailynotifications.backend.entity.ReminderEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReminderRepository : JpaRepository<ReminderEntity, String> {
    fun findAllByUserEmailOrderByDateTimeDesc(email: String): List<ReminderEntity>
    fun findByIdAndUserEmail(id: String, email: String): ReminderEntity?
}
