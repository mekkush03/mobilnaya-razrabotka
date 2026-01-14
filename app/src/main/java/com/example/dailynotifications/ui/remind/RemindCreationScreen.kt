package com.example.dailynotifications.ui.remind

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailynotifications.data.model.ReminderFrequency
import com.example.dailynotifications.ui.theme.AppTypography
import com.example.dailynotifications.ui.theme.LightColorScheme
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindCreationScreen(
    editReminderId: String? = null,
    onBack: (() -> Unit)? = null,
    viewModel: RemindCreationViewModel = hiltViewModel()
) {
    val colors = LightColorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val use24Hour by viewModel.use24Hour.collectAsState(initial = true)

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(editReminderId) {
        if (editReminderId.isNullOrBlank()) {
            viewModel.onClear()
        } else {
            viewModel.loadReminder(editReminderId)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val date = LocalDate.ofInstant(
                                Instant.ofEpochMilli(millis),
                                ZoneId.systemDefault()
                            )
                            viewModel.onDatePicked(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val initialTime = remember(viewModel.timeDigits, use24Hour) {
            parseTimeForPicker(viewModel.timeDigits, viewModel.amSelected, viewModel.pmSelected, use24Hour)
        }
        val timePickerState = rememberTimePickerState(
            initialHour = initialTime.first,
            initialMinute = initialTime.second,
            is24Hour = use24Hour
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onTimePicked(
                            timePickerState.hour,
                            timePickerState.minute,
                            use24Hour
                        )
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.onDelete()
                        onBack?.invoke()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Text("Delete this reminder?")
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(colors.surface)
            ) {
                if (editReminderId != null && onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (editReminderId != null) "Edit Reminder" else "Create Reminder",
                        style = AppTypography.titleLarge,
                        color = colors.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .background(colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val titleInteraction = remember { MutableInteractionSource() }
                val titleFocused by titleInteraction.collectIsFocusedAsState()
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = viewModel.title,
                        onValueChange = viewModel::onTitleChange,
                        label = null,
                        textStyle = AppTypography.bodyLarge.copy(fontFamily = FontFamily.Default),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = if (titleFocused) colors.primary else colors.outline,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        interactionSource = titleInteraction,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    FixedLabel(
                        text = "Task Title*",
                        background = colors.surface,
                        modifier = Modifier.offset(x = 12.dp, y = (-8).dp)
                    )
                }

                val dateInteraction = remember { MutableInteractionSource() }
                val dateFocused by dateInteraction.collectIsFocusedAsState()
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = viewModel.dateDigits,
                        onValueChange = viewModel::onDateChange,
                        label = null,
                        visualTransformation = DateMaskVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Pick date",
                                    tint = colors.primary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = if (dateFocused) colors.primary else colors.outline,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        interactionSource = dateInteraction,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    FixedLabel(
                        text = "Reminder Date*",
                        background = colors.surface,
                        modifier = Modifier.offset(x = 12.dp, y = (-8).dp)
                    )
                }

                Column {
                    val timeInteraction = remember { MutableInteractionSource() }
                    val timeFocused by timeInteraction.collectIsFocusedAsState()
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = viewModel.timeDigits,
                            onValueChange = viewModel::onTimeChange,
                            label = null,
                            visualTransformation = TimeMaskVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showTimePicker = true }) {
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = "Pick time",
                                        tint = colors.primary
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = if (timeFocused) colors.primary else colors.outline,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            interactionSource = timeInteraction,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        FixedLabel(
                            text = "Reminder Time*",
                            background = colors.surface,
                            modifier = Modifier.offset(x = 12.dp, y = (-8).dp)
                        )
                    }

                    if (!use24Hour) {
                        Row(
                            modifier = Modifier.padding(top = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = viewModel.amSelected,
                                onCheckedChange = viewModel::onAmSelected
                            )
                            Text("AM", style = AppTypography.bodyMedium)
                            Spacer(modifier = Modifier.width(20.dp))
                            Checkbox(
                                checked = viewModel.pmSelected,
                                onCheckedChange = viewModel::onPmSelected
                            )
                            Text("PM", style = AppTypography.bodyMedium)
                        }
                    }
                }

                val noteInteraction = remember { MutableInteractionSource() }
                val noteFocused by noteInteraction.collectIsFocusedAsState()
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = viewModel.note,
                        onValueChange = viewModel::onNoteChange,
                        label = null,
                        textStyle = AppTypography.bodyLarge.copy(fontFamily = FontFamily.Default),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(
                                width = 2.dp,
                                color = if (noteFocused) colors.primary else colors.outline,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        shape = RoundedCornerShape(10.dp),
                        interactionSource = noteInteraction,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    FixedLabel(
                        text = "Note (optional)",
                        background = colors.surface,
                        modifier = Modifier.offset(x = 12.dp, y = (-8).dp)
                    )
                }

                Text(
                    "Reminder Frequency*",
                    style = AppTypography.bodyLarge.copy(color = colors.primary)
                )

                Column {
                    val options = listOf(
                        ReminderFrequency.ONE_TIME,
                        ReminderFrequency.DAILY,
                        ReminderFrequency.WEEKLY,
                        ReminderFrequency.CUSTOM
                    )
                    options.chunked(2).forEach { row ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            row.forEach { opt ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.onFrequencySelected(opt) },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = viewModel.frequency == opt,
                                            onCheckedChange = { if (it) viewModel.onFrequencySelected(opt) }
                                        )
                                        Text(opt.label, style = AppTypography.bodyLarge)
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = viewModel::onSave,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Save", style = AppTypography.labelLarge, color = colors.onPrimary)
                    }

                    OutlinedButton(
                        onClick = viewModel::onClear,
                        shape = RoundedCornerShape(50),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.5.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(colors.primary)
                        )
                    ) {
                        Text("Clear", style = AppTypography.labelLarge, color = colors.primary)
                    }
                }

                if (editReminderId != null) {
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        shape = RoundedCornerShape(50),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.5.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(colors.primary)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete", style = AppTypography.labelLarge, color = colors.primary)
                    }
                }
            }
        }
    }
}

private fun parseTimeForPicker(
    text: String,
    isAm: Boolean,
    isPm: Boolean,
    is24Hour: Boolean
): Pair<Int, Int> {
    val digits = text.filter { it.isDigit() }.take(4)
    if (digits.length < 3) {
        val now = java.time.LocalTime.now()
        return now.hour to now.minute
    }
    val padded = digits.padEnd(4, '0')
    val hour = padded.substring(0, 2).toIntOrNull()
    val minute = padded.substring(2, 4).toIntOrNull()
    if (hour == null || minute == null) {
        val now = java.time.LocalTime.now()
        return now.hour to now.minute
    }
    if (is24Hour) {
        return hour.coerceIn(0, 23) to minute.coerceIn(0, 59)
    }
    val normalizedHour = when {
        isPm -> (hour % 12) + 12
        isAm -> hour % 12
        else -> hour % 12
    }
    return normalizedHour.coerceIn(0, 23) to minute.coerceIn(0, 59)
}

private class DateMaskVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(8)
        val originalLength = digits.length
        val padded = (digits + "________").take(8)
        val transformed = buildString {
            append(padded.substring(0, 2))
            append('.')
            append(padded.substring(2, 4))
            append('.')
            append(padded.substring(4, 8))
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, originalLength)
                return when {
                    safeOffset <= 2 -> safeOffset
                    safeOffset <= 4 -> safeOffset + 1
                    safeOffset <= 8 -> safeOffset + 2
                    else -> 10
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (originalLength == 0) return 0
                val raw = when {
                    offset <= 2 -> offset
                    offset <= 5 -> (offset - 1).coerceAtLeast(0)
                    offset <= 10 -> (offset - 2).coerceAtLeast(0)
                    else -> 8
                }
                return raw.coerceIn(0, originalLength)
            }
        }
        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}

private class TimeMaskVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(4)
        val originalLength = digits.length
        val padded = (digits + "____").take(4)
        val transformed = buildString {
            append(padded.substring(0, 2))
            append(':')
            append(padded.substring(2, 4))
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, originalLength)
                return when {
                    safeOffset <= 2 -> safeOffset
                    safeOffset <= 4 -> safeOffset + 1
                    else -> 5
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (originalLength == 0) return 0
                val raw = when {
                    offset <= 2 -> offset
                    offset <= 5 -> (offset - 1).coerceAtLeast(0)
                    else -> 4
                }
                return raw.coerceIn(0, originalLength)
            }
        }
        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}

@Composable
private fun FixedLabel(
    text: String,
    background: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = AppTypography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .background(background)
            .padding(horizontal = 6.dp)
    )
}
