package com.example.dailynotifications.data.repository.settings

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val use24Hour: StateFlow<Boolean>
    val notificationsEnabled: StateFlow<Boolean>
    fun setUse24Hour(enabled: Boolean)
    fun setNotificationsEnabled(enabled: Boolean)
}
