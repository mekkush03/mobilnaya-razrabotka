package com.example.dailynotifications.backend.dto.response

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val token: String? = null,
)
