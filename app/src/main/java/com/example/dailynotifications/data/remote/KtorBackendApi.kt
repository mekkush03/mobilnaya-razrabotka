package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.model.Reminder
import com.example.dailynotifications.data.remote.dto.ReminderDto
import com.example.dailynotifications.data.remote.dto.toDto
import com.example.dailynotifications.data.remote.dto.toModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class KtorBackendApi(
    private val client: HttpClient,
    private val baseUrl: String
) : BackendApi {
    override suspend fun fetchReminders(token: String): List<Reminder> {
        return client.get("$baseUrl/api/mobile/reminders") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.bodyOrThrow<List<ReminderDto>>().map { it.toModel() }
    }

    override suspend fun upsertReminder(token: String, reminder: Reminder): Reminder {
        return client.put("$baseUrl/api/mobile/reminders") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(reminder.toDto())
        }.bodyOrThrow<ReminderDto>().toModel()
    }

    override suspend fun deleteReminder(token: String, reminderId: String) {
        client.delete("$baseUrl/api/mobile/reminders/$reminderId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.bodyOrThrow<Unit>()
    }
}
