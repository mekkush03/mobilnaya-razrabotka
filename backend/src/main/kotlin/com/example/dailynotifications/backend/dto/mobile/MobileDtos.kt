package com.example.dailynotifications.backend.dto.mobile

import com.example.dailynotifications.backend.util.ReminderFrequency
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class MobileLoginRequest(
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
)

data class MobileRegisterRequest(
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
    @field:NotBlank
    val name: String,
)

data class MobileUserDto(
    val id: Long,
    val name: String,
    val email: String,
)

data class MobileReminderRequest(
    val id: String? = null,
    @field:NotBlank
    val title: String,
    @field:NotNull
    @JsonProperty("date_time")
    val dateTime: LocalDateTime,
    val note: String? = null,
    @field:NotNull
    val frequency: ReminderFrequency,
)

data class MobileReminderDto(
    val id: String,
    val title: String,
    @JsonProperty("date_time")
    val dateTime: LocalDateTime,
    val note: String? = null,
    val frequency: ReminderFrequency,
)
