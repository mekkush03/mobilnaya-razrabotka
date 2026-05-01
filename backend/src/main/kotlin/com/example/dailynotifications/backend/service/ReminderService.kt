package com.example.dailynotifications.backend.service

import com.example.dailynotifications.backend.dto.mobile.MobileReminderDto
import com.example.dailynotifications.backend.dto.mobile.MobileReminderRequest
import com.example.dailynotifications.backend.entity.ReminderEntity
import com.example.dailynotifications.backend.repository.ReminderRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReminderService(
    private val reminderRepository: ReminderRepository,
    private val userService: UserService,
) {
    fun getCurrentUserReminders(): List<MobileReminderDto> =
        reminderRepository.findAllByUserEmailOrderByDateTimeDesc(userService.currentAuthenticatedEmail())
            .map { it.toDto() }

    @Transactional
    fun saveCurrentUserReminder(request: MobileReminderRequest): MobileReminderDto {
        val currentEmail = userService.currentAuthenticatedEmail()
        val user = userService.getEntityByEmail(currentEmail)
        val reminder = request.id?.let { reminderRepository.findByIdAndUserEmail(it, currentEmail) }
            ?: ReminderEntity(id = request.id?.ifBlank { UUID.randomUUID().toString() } ?: UUID.randomUUID().toString())

        reminder.user = user
        reminder.title = request.title.trim()
        reminder.dateTime = request.dateTime
        reminder.note = request.note?.trim()?.ifBlank { null }
        reminder.frequency = request.frequency

        return reminderRepository.save(reminder).toDto()
    }

    @Transactional
    fun deleteCurrentUserReminder(id: String) {
        val reminder = reminderRepository.findByIdAndUserEmail(id, userService.currentAuthenticatedEmail())
            ?: throw EntityNotFoundException("Reminder not found")
        reminderRepository.delete(reminder)
    }

    private fun ReminderEntity.toDto(): MobileReminderDto =
        MobileReminderDto(
            id = id,
            title = title,
            dateTime = dateTime,
            note = note,
            frequency = frequency,
        )
}
