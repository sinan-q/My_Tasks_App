package com.sinxn.mytasks.ui.features.tasks.addedit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.ui.components.ScrollablePicker
import com.sinxn.mytasks.ui.components.TimePickerDialog
import com.sinxn.mytasks.utils.ReminderTrigger
import com.sinxn.mytasks.utils.ReminderTypes
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersSection(
    reminders: List<ReminderModel>,
    isRecurring: Boolean = false,
    isEditing: Boolean,
    onRemoveReminder: (ReminderModel) -> Unit,
    onAddReminder: (ReminderModel) -> Unit
) {
    var reminder by remember { mutableStateOf("0") }
    var reminderType by remember { mutableStateOf(ReminderTypes.MINUTE) }
    var reminderTrigger by remember { mutableStateOf(ReminderTrigger.FROM_END) }
    var customDate by remember { mutableStateOf(java.time.LocalDate.now()) }
    var customTime by remember { mutableStateOf(java.time.LocalTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        customDate = java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                    showTimePicker = true
                }) {
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
        val timePickerState = rememberTimePickerState()
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = {
                customTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                showTimePicker = false
                onAddReminder(
                    ReminderModel(
                        0,
                        ChronoUnit.MINUTES,
                        ReminderTrigger.CUSTOM,
                        LocalDateTime.of(customDate, customTime)
                    )
                )
                showAddDialog = false
            },
            content = {
                TimePicker(state = timePickerState)
            }
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Reminder") },
            text = {
                Column {
                    val triggerOptions = if (isRecurring) {
                        listOf(ReminderTrigger.FROM_END)
                    } else {
                        ReminderTrigger.entries.toList()
                    }
                    val itemHeight = 50.dp
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ScrollablePicker(
                            values = triggerOptions,
                            defaultValue = ReminderTrigger.FROM_END,
                            height = itemHeight,
                            modifier = Modifier
                                .weight(1f)
                                .height(itemHeight)
                        ) {
                            reminderTrigger = it
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (reminderTrigger != ReminderTrigger.CUSTOM) {
                            ScrollablePicker(
                                values = (0..60).toList(),
                                defaultValue = 0,
                                height = itemHeight,
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(itemHeight)
                            ) {
                                reminder = it.toString()
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ScrollablePicker(
                                values = ReminderTypes.entries.toList(),
                                defaultValue = ReminderTypes.MINUTE,
                                height = itemHeight,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(itemHeight)
                            ) {
                                reminderType = it
                            }
                        } else {
                            TextButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Select Custom Date & Time")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (reminderTrigger != ReminderTrigger.CUSTOM) {
                        onAddReminder(
                            ReminderModel(
                                reminder.toInt(),
                                reminderType.unit,
                                reminderTrigger
                            )
                        )
                        showAddDialog = false
                    } else {
                        showDatePicker = true
                    }
                }) {
                    Text(if (reminderTrigger != ReminderTrigger.CUSTOM) "Add" else "Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        reminders.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Reminder",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    val text = when(option.trigger) {
                        ReminderTrigger.FROM_END -> "${option.duration} ${option.unit.name.lowercase().replaceFirstChar { it.uppercase() }} before"
                        ReminderTrigger.FROM_START -> "${option.duration} ${option.unit.name.lowercase().replaceFirstChar { it.uppercase() }} after"
                        ReminderTrigger.CUSTOM -> "At ${option.customDateTime?.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm"))}"
                    }
                    Text(text = text, color = MaterialTheme.colorScheme.onSurface)
                }
                
                if (isEditing) {
                    IconButton(onClick = { onRemoveReminder(option) }) {
                        Icon(Icons.Default.Close, contentDescription = "Delete Reminder")
                    }
                }
            }
        }
        
        if (isEditing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAddDialog = true }
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Reminder",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Add a reminder...", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
