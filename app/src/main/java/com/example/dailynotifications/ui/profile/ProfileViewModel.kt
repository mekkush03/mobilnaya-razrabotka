package com.example.dailynotifications.ui.profile

import androidx.lifecycle.ViewModel
import com.example.dailynotifications.data.repository.auth.AuthRepository
import com.example.dailynotifications.data.repository.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    val use24Hour = settingsRepository.use24Hour
    val notificationsEnabled = settingsRepository.notificationsEnabled
    val authState = authRepository.authState


    fun onTimeFormatSelected(use24: Boolean) {
        settingsRepository.setUse24Hour(use24)
    }

    fun onNotificationsToggle(enabled: Boolean) {
        settingsRepository.setNotificationsEnabled(enabled)
    }

    fun onStartAuth() {
        authRepository.startAuth()
    }

    fun onLogout() {
        authRepository.logout()
    }
}
