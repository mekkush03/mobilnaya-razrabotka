package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.remote.dto.AuthRequest
import com.example.dailynotifications.data.remote.dto.AuthResponse
import com.example.dailynotifications.data.remote.dto.AuthSession
import com.example.dailynotifications.data.remote.dto.RegisterRequest
import com.example.dailynotifications.data.remote.dto.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class KtorAuthApi(
    private val client: HttpClient,
    private val baseUrl: String
) : AuthApi {
    override suspend fun login(email: String, password: String): AuthSession {
        val response = client.post("$baseUrl/api/mobile/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(email = email, password = password))
        }.bodyOrThrow<AuthResponse>()
        return response.toSession()
    }

    override suspend fun register(name: String, email: String, password: String): AuthSession {
        val response = client.post("$baseUrl/api/mobile/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(name = name, email = email, password = password))
        }.bodyOrThrow<AuthResponse>()
        return response.toSession()
    }

    override suspend fun getCurrentUser(token: String): UserProfile {
        return client.get("$baseUrl/api/mobile/users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.bodyOrThrow()
    }
}

private fun AuthResponse.toSession(): AuthSession {
    return AuthSession(
        token = token,
        name = user.name,
        email = user.email
    )
}
