package com.example.dailynotifications.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailynotifications.data.repository.reminder.ReminderRepository
import com.example.dailynotifications.data.repository.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val repository: ReminderRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    val reminders = repository.reminders
    val use24Hour = settingsRepository.use24Hour

    init {
        viewModelScope.launch {
            runCatching { repository.loadReminders() }
        }
    }
}
