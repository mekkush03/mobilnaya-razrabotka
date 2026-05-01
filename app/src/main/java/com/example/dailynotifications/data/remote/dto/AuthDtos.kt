package com.example.dailynotifications.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserProfile
)

@Serializable
data class UserProfile(
    val id: Long,
    val name: String,
    val email: String
)

data class AuthSession(
    val token: String,
    val name: String,
    val email: String
)
