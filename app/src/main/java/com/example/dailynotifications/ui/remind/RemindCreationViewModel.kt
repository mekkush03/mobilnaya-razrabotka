package com.example.dailynotifications.ui.remind

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailynotifications.core.notifications.NotificationScheduler
import com.example.dailynotifications.data.model.Reminder
import com.example.dailynotifications.data.model.ReminderFrequency
import com.example.dailynotifications.data.repository.reminder.ReminderRepository
import com.example.dailynotifications.data.repository.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RemindCreationViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: NotificationScheduler,
    settingsRepository: SettingsRepository
) : ViewModel() {

    var title by mutableStateOf("")
        private set
    var dateDigits by mutableStateOf("")
        private set
    var timeDigits by mutableStateOf("")
        private set
    var amSelected by mutableStateOf(false)
        private set
    var pmSelected by mutableStateOf(false)
        private set
    var note by mutableStateOf("")
        private set
    var frequency by mutableStateOf<ReminderFrequency?>(null)
        private set
    private var editingId: String? = null
    val use24Hour: StateFlow<Boolean> = settingsRepository.use24Hour
    val notificationsEnabled: StateFlow<Boolean> = settingsRepository.notificationsEnabled

    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events

    fun onTitleChange(value: String) {
        title = value
    }

    fun onDateChange(value: String) {
        dateDigits = value.filter { it.isDigit() }.take(8)
    }

    fun onDatePicked(date: LocalDate) {
        dateDigits = date.format(DateTimeFormatter.ofPattern("ddMMyyyy"))
    }

    fun onTimeChange(value: String) {
        timeDigits = value.filter { it.isDigit() }.take(4)
    }

    fun onTimePicked(hour: Int, minute: Int, is24Hour: Boolean) {
        if (is24Hour) {
            timeDigits = String.format("%02d%02d", hour, minute)
            amSelected = false
            pmSelected = false
        } else {
            val isPm = hour >= 12
            val hour12 = when (val h = hour % 12) {
                0 -> 12
                else -> h
            }
            timeDigits = String.format("%02d%02d", hour12, minute)
            amSelected = !isPm
            pmSelected = isPm
        }
    }

    fun onNoteChange(value: String) {
        note = value
    }

    fun onAmSelected(value: Boolean) {
        amSelected = value
        if (value) pmSelected = false
    }

    fun onPmSelected(value: Boolean) {
        pmSelected = value
        if (value) amSelected = false
    }

    fun onFrequencySelected(value: ReminderFrequency) {
        frequency = value
    }

    fun onClear() {
        title = ""
        dateDigits = ""
        timeDigits = ""
        note = ""
        frequency = null
        amSelected = false
        pmSelected = false
        editingId = null
    }

    fun onDelete() {
        val id = editingId ?: return
        viewModelScope.launch {
            runCatching {
                repository.deleteReminder(id)
            }.onSuccess {
                onClear()
                _events.emit("Reminder deleted.")
            }.onFailure {
                _events.emit(it.message ?: "Failed to delete reminder.")
            }
        }
    }

    fun loadReminder(id: String) {
        viewModelScope.launch {
            val reminder = repository.getReminder(id) ?: return@launch
            editingId = reminder.id
            title = reminder.title
            note = reminder.note ?: ""
            frequency = reminder.frequency
            val dateText = reminder.dateTime.format(DateTimeFormatter.ofPattern("ddMMyyyy"))
            dateDigits = dateText
            val is24 = use24Hour.value
            if (is24) {
                timeDigits = reminder.dateTime.format(DateTimeFormatter.ofPattern("HHmm"))
                amSelected = false
                pmSelected = false
            } else {
                val hour = reminder.dateTime.hour
                val minute = reminder.dateTime.minute
                val isPm = hour >= 12
                val hour12 = when (val h = hour % 12) {
                    0 -> 12
                    else -> h
                }
                timeDigits = String.format("%02d%02d", hour12, minute)
                amSelected = !isPm
                pmSelected = isPm
            }
        }
    }

    fun onSave() {
        viewModelScope.launch {
            if (title.isBlank()) {
                _events.emit("Title is required.")
                return@launch
            }
            val chosenFrequency = frequency
            if (chosenFrequency == null) {
                _events.emit("Select reminder frequency.")
                return@launch
            }
            val dateTime = parseDateTime(
                formatDateInput(dateDigits).trim(),
                formatTimeInput(timeDigits).trim(),
                amSelected,
                pmSelected,
                use24Hour.value
            )
            if (dateTime == null) {
                _events.emit("Enter date as dd.MM.yyyy and time as HH:mm.")
                return@launch
            }
            val reminder = Reminder(
                id = editingId ?: UUID.randomUUID().toString(),
                title = title.trim(),
                dateTime = dateTime,
                note = note.trim().ifBlank { null },
                frequency = chosenFrequency
            )
            runCatching {
                repository.addReminder(reminder)
            }.onSuccess {
                if (notificationsEnabled.value) {
                    scheduler.schedule(reminder)
                }
                onClear()
                _events.emit("Reminder scheduled.")
            }.onFailure {
                _events.emit(it.message ?: "Failed to save reminder.")
            }
        }
    }

    private fun parseDateTime(
        dateText: String,
        timeText: String,
        isAm: Boolean,
        isPm: Boolean,
        is24Hour: Boolean
    ): LocalDateTime? {
        val date = parseDate(dateText) ?: return null
        val time = parseTime(timeText, isAm, isPm, is24Hour) ?: return null
        return LocalDateTime.of(date, time)
    }

    private fun parseDate(text: String): LocalDate? {
        val patterns = listOf("dd.MM.yyyy", "yyyy-MM-dd")
        for (pattern in patterns) {
            try {
                return LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern))
            } catch (_: DateTimeParseException) {
                // try next
            }
        }
        return null
    }

    private fun formatDateInput(text: String): String {
        val digits = text.filter { it.isDigit() }.take(8)
        val padded = (digits + "________").take(8)
        val day = padded.substring(0, 2)
        val month = padded.substring(2, 4)
        val year = padded.substring(4, 8)
        return "$day.$month.$year"
    }

    private fun parseTime(
        text: String,
        isAm: Boolean,
        isPm: Boolean,
        is24Hour: Boolean
    ): LocalTime? {
        if (text.count { it.isDigit() } < 3) {
            return null
        }
        val pattern = if (text.count { it == ':' } == 1) "H:mm" else "HH:mm"
        val time = try {
            LocalTime.parse(text, DateTimeFormatter.ofPattern(pattern))
        } catch (_: DateTimeParseException) {
            return null
        }
        if (is24Hour) {
            return time
        }
        if (!isAm && !isPm) {
            return null
        }
        val hour = time.hour % 12
        val adjustedHour = if (isPm) hour + 12 else hour
        return time.withHour(adjustedHour)
    }

    private fun formatTimeInput(text: String): String {
        val digits = text.filter { it.isDigit() }.take(4)
        if (digits.length < 3) return ""
        val padded = digits.padEnd(4, '0')
        val hour = padded.substring(0, 2)
        val minute = padded.substring(2, 4)
        return "$hour:$minute"
    }
}
