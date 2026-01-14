package com.example.dailynotifications.data.repository.auth

import kotlinx.coroutines.flow.StateFlow

data class AuthState(
    val isLoggedIn: Boolean,
    val isGuest: Boolean,
    val token: String?,
    val name: String?,
    val email: String?
)

interface AuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun login(email: String, password: String)
    suspend fun register(name: String, email: String, password: String)
    fun logout()
    fun skipAuth()
    fun startAuth()
}
