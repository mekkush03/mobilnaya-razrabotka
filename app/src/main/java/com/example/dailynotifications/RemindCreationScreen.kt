@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dailynotifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailynotifications.ui.theme.AppTypography
import com.example.dailynotifications.ui.theme.LightColorScheme


@Preview(showBackground = true)
@Composable
fun RemindCreationScreen() {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var amSelected by remember { mutableStateOf(false) }
    var pmSelected by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }

    val colors = LightColorScheme

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(colors.primary),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "Reminders",
                    style = AppTypography.titleLarge,
                    color = colors.onPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    ) { padding ->
        Card(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title*", style = AppTypography.labelLarge) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    )
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Reminder Date*", style = AppTypography.labelLarge) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Pick date",
                            tint = colors.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    )
                )

                Column {
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Reminder Time*", style = AppTypography.labelLarge) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline
                        )
                    )

                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = amSelected, onCheckedChange = {
                            amSelected = it; if (it) pmSelected = false
                        })
                        Text("AM", style = AppTypography.bodyMedium)
                        Spacer(modifier = Modifier.width(20.dp))
                        Checkbox(checked = pmSelected, onCheckedChange = {
                            pmSelected = it; if (it) amSelected = false
                        })
                        Text("PM", style = AppTypography.bodyMedium)
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)", style = AppTypography.labelLarge) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    )
                )

                Text(
                    "Reminder Frequency*",
                    style = AppTypography.bodyLarge.copy(color = colors.primary)
                )

                Column {
                    val options = listOf("One time", "Daily", "Weekly", "Custom")
                    options.chunked(2).forEach { row ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            row.forEach { opt ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = frequency == opt,
                                        onCheckedChange = { if (it) frequency = opt }
                                    )
                                    Text(opt, style = AppTypography.bodyMedium)
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
                        onClick = { /* Save */ },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Save", style = AppTypography.labelLarge, color = colors.onPrimary)
                    }

                    OutlinedButton(
                        onClick = {
                            title = ""; date = ""; time = ""; note = ""; frequency = ""
                            amSelected = false; pmSelected = false
                        },
                        shape = RoundedCornerShape(50),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.5.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(colors.primary)
                        )
                    ) {
                        Text("Clear", style = AppTypography.labelLarge, color = colors.primary)
                    }
                }
            }
        }
    }
}
