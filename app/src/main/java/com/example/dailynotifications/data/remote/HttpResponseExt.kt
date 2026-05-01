package com.example.dailynotifications.data.remote

import com.example.dailynotifications.data.remote.dto.ApiErrorResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    if (status.isSuccess()) {
        return body()
    }
    val fallbackMessage = status.description.ifBlank { "Request failed with status ${status.value}" }
    val responseText = runCatching { bodyAsText() }.getOrNull()
    val message = runCatching {
        responseText?.let { networkJson.decodeFromString(ApiErrorResponse.serializer(), it).message }
    }.getOrNull() ?: responseText?.takeIf { it.isNotBlank() } ?: fallbackMessage
    throw ApiException(message)
}
