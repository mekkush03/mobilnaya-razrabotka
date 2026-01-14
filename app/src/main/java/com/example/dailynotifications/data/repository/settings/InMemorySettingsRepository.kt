package com.example.dailynotifications.data.repository.settings

import com.example.dailynotifications.data.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InMemorySettingsRepository : SettingsRepository {
    private val _use24Hour = MutableStateFlow(true)
    override val use24Hour: StateFlow<Boolean> = _use24Hour
    private val _notificationsEnabled = MutableStateFlow(true)
    override val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    override fun setUse24Hour(enabled: Boolean) {
        _use24Hour.value = enabled
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }
}
