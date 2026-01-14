package com.example.dailynotifications.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailynotifications.data.model.Reminder
import com.example.dailynotifications.ui.theme.AppTypography
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ReminderListScreen(
    onEdit: (String) -> Unit,
    viewModel: ReminderListViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState(initial = emptyList())
    val use24Hour by viewModel.use24Hour.collectAsState(initial = true)
    val grouped = reminders.groupBy { it.dateTime.toLocalDate() }
    var showUpcoming by rememberSaveable { mutableStateOf(true) }
    var showPast by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Reminders",
                        style = AppTypography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { padding ->
        if (reminders.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No reminders yet.",
                    style = AppTypography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Create one with the + button.",
                    style = AppTypography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val now = LocalDateTime.now()
            val nextReminder = reminders
                .filter { it.dateTime.isAfter(now) }
                .minByOrNull { it.dateTime }
            val timePattern = if (use24Hour) "MMM dd, yyyy HH:mm" else "MMM dd, yyyy hh:mm a"
            val nearestData = nextReminder?.let {
                val duration = Duration.between(now, it.dateTime).coerceAtLeast(Duration.ZERO)
                val days = duration.toDays()
                val hours = duration.minusDays(days).toHours()
                val minutes = duration.minusDays(days).minusHours(hours).toMinutes()
                val dateText = it.dateTime.format(DateTimeFormatter.ofPattern(timePattern))
                val countdown = "${days}d ${hours}h ${minutes}m"
                NearestDisplay(
                    title = it.title,
                    note = it.note?.takeIf { note -> note.isNotBlank() },
                    dateText = dateText,
                    countdown = countdown
                )
            }
            val upcoming = reminders.filter { it.dateTime.isAfter(now) }
            val past = reminders.filterNot { it.dateTime.isAfter(now) }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Nearest date",
                        style = AppTypography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (nearestData == null) {
                                Text(
                                    "No upcoming dates.",
                                    style = AppTypography.bodyLarge
                                )
                            } else {
                                Text(
                                    nearestData.title,
                                    style = AppTypography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                nearestData.note?.let { note ->
                                    Text(note, style = AppTypography.bodyMedium)
                                }
                                Text(
                                    "Date: ${nearestData.dateText}",
                                    style = AppTypography.titleMedium
                                )
                                Text(
                                    "In: ${nearestData.countdown}",
                                    style = AppTypography.titleMedium
                                )
                            }
                        }
                    }
                }
                item {
                    SectionHeader(
                        title = "Active dates",
                        expanded = showUpcoming,
                        onToggle = { showUpcoming = !showUpcoming }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = showUpcoming,
                        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                    ) {
                    val upcomingGrouped = upcoming.groupBy { it.dateTime.year }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        upcomingGrouped.toSortedMap(compareByDescending { it }).forEach { (year, items) ->
                            Text(
                                text = year.toString(),
                                style = AppTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            items.forEach { reminder ->
                                ReminderCard(
                                    reminder = reminder,
                                    use24Hour = use24Hour,
                                    onClick = { onEdit(reminder.id) }
                                )
                            }
                        }
                        if (upcoming.isEmpty()) {
                            Text("No active dates.", style = AppTypography.bodyLarge)
                        }
                    }
                }
                }
                item {
                    SectionHeader(
                        title = "Past dates",
                        expanded = showPast,
                        onToggle = { showPast = !showPast }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = showPast,
                        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                    ) {
                    val pastGrouped = past.groupBy { it.dateTime.year }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        pastGrouped.toSortedMap(compareByDescending { it }).forEach { (year, items) ->
                            Text(
                                text = year.toString(),
                                style = AppTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            items.forEach { reminder ->
                                ReminderCard(
                                    reminder = reminder,
                                    use24Hour = use24Hour,
                                    onClick = { onEdit(reminder.id) }
                                )
                            }
                        }
                        if (past.isEmpty()) {
                            Text("No past dates.", style = AppTypography.bodyMedium)
                        }
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = AppTypography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    use24Hour: Boolean,
    onClick: () -> Unit
) {
    val timeFormatter = if (use24Hour) {
        DateTimeFormatter.ofPattern("HH:mm")
    } else {
        DateTimeFormatter.ofPattern("hh:mm a")
    }
    val dateText = reminder.dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    val timeText = reminder.dateTime.format(timeFormatter)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(reminder.title, style = AppTypography.titleLarge)
                reminder.note?.takeIf { it.isNotBlank() }?.let { note ->
                    Text(note, style = AppTypography.bodyMedium)
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(dateText, style = AppTypography.titleMedium)
                Text(timeText, style = AppTypography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

private data class NearestDisplay(
    val title: String,
    val note: String?,
    val dateText: String,
    val countdown: String
)
