package com.example.dailynotifications.backend.controller

import com.example.dailynotifications.backend.dto.mobile.MobileLoginRequest
import com.example.dailynotifications.backend.dto.mobile.MobileRegisterRequest
import com.example.dailynotifications.backend.dto.mobile.MobileReminderDto
import com.example.dailynotifications.backend.dto.mobile.MobileReminderRequest
import com.example.dailynotifications.backend.dto.mobile.MobileUserDto
import com.example.dailynotifications.backend.dto.response.UserAuthResponse
import com.example.dailynotifications.backend.service.ReminderService
import com.example.dailynotifications.backend.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/mobile")
class MobileController(
    private val userService: UserService,
    private val reminderService: ReminderService,
) {
    @PostMapping("/auth/login")
    fun login(@Valid @RequestBody request: MobileLoginRequest): UserAuthResponse =
        userService.login(request)

    @PostMapping("/auth/register")
    fun register(@Valid @RequestBody request: MobileRegisterRequest): UserAuthResponse =
        userService.register(request)

    @PostMapping("/auth/logout")
    fun logout(): ResponseEntity<Unit> =
        ResponseEntity.ok().build()

    @GetMapping("/users/me")
    fun getCurrentUser(): MobileUserDto {
        val user = userService.getCurrentUser()
        return MobileUserDto(
            id = user.id,
            name = user.name,
            email = user.email,
        )
    }

    @GetMapping("/reminders")
    fun getReminders(): List<MobileReminderDto> =
        reminderService.getCurrentUserReminders()

    @PutMapping("/reminders")
    fun saveReminder(@Valid @RequestBody request: MobileReminderRequest): MobileReminderDto =
        reminderService.saveCurrentUserReminder(request)

    @DeleteMapping("/reminders/{id}")
    fun deleteReminder(@PathVariable id: String): ResponseEntity<Unit> {
        reminderService.deleteCurrentUserReminder(id)
        return ResponseEntity.ok().build()
    }
}
