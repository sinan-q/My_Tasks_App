package com.sinxn.mytasks.ui.features.tasks.addedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.sinxn.mytasks.ui.components.RectangleButton
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
    isEditing: Boolean,
    onRemoveReminder: (ReminderModel) -> Unit,
    onAddReminder: (ReminderModel) -> Unit
) {
    var reminder by remember { mutableStateOf("0") }
    var reminderType by remember { mutableStateOf(ReminderTypes.MINUTE) }
    var reminderTrigger by remember { mutableStateOf(com.sinxn.mytasks.utils.ReminderTrigger.FROM_END) }
    var customDate by remember { mutableStateOf(java.time.LocalDate.now()) }
    var customTime by remember { mutableStateOf(java.time.LocalTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }


    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
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
                androidx.compose.material3.TextButton(onClick = { showDatePicker = false }) {
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
            },
            content = {
                androidx.compose.material3.TimePicker(state = timePickerState)
            }
        )
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        Text(text = "Reminders on " )
        if (reminders.isEmpty())
            Text(text = "No Remainders set")

        reminders.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditing) {
                    IconButton(onClick = { onRemoveReminder(option) }) {
                        Icon(Icons.Default.Close, contentDescription = "Delete Reminder")
                    }
                }
                val text = when(option.trigger) {
                    ReminderTrigger.FROM_END -> "${option.duration} ${option.unit.name} before due"
                    ReminderTrigger.FROM_START -> "${option.duration} ${option.unit.name} after now"
                    ReminderTrigger.CUSTOM -> "At ${option.customDateTime?.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm"))}"
                }
                Text(text = text)
            }
        }
        if (isEditing) {
            Row {
                val itemHeight = 50.dp
                RectangleButton(
                    modifier = Modifier.height(itemHeight),
                    onClick = {
                        if (reminderTrigger == ReminderTrigger.CUSTOM) {
                            showDatePicker = true
                        } else {
                            onAddReminder(
                                ReminderModel(
                                    reminder.toInt(),
                                    reminderType.unit,
                                    reminderTrigger
                                )
                            )
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, "Add Reminder")
                }
                
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
                    ScrollablePicker(
                        values = ReminderTypes.entries.toList(),
                        defaultValue = ReminderTypes.MINUTE,
                        height = itemHeight,
                        modifier = Modifier
                            .width(100.dp)
                            .height(itemHeight)
                    ) {
                        reminderType = it
                    }
                }
                
                ScrollablePicker(
                    values = ReminderTrigger.entries.toList(),
                    defaultValue = ReminderTrigger.FROM_END,
                    height = itemHeight,
                    modifier = Modifier
                        .width(120.dp)
                        .height(itemHeight)
                ) {
                    reminderTrigger = it
                }
            }
        }
    }
}
