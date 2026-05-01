package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.remote.dto.AuthSession
import com.example.dailynotifications.data.remote.dto.UserProfile

interface AuthApi {
    suspend fun login(email: String, password: String): AuthSession
    suspend fun register(name: String, email: String, password: String): AuthSession
    suspend fun getCurrentUser(token: String): UserProfile
}
