package com.example.dailynotifications.data.remote

import kotlinx.serialization.json.Json

val networkJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
