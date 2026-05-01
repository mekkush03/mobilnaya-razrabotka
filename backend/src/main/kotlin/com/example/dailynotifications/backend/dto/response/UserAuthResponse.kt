package com.example.dailynotifications.backend.dto.response

data class UserAuthResponse(
    val token: String,
    val user: UserResponse,
)
