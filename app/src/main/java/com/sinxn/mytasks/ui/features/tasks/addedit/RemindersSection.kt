package com.sinxn.mytasks.ui.features.tasks.addedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.sinxn.mytasks.utils.ReminderTypes
import java.time.temporal.ChronoUnit

@Composable
fun RemindersSection(
    reminders: List<Pair<Int, ChronoUnit>>,
    isEditing: Boolean,
    onRemoveReminder: (Pair<Int, ChronoUnit>) -> Unit,
    onAddReminder: (Pair<Int, ChronoUnit>) -> Unit
) {
    var reminder by remember { mutableStateOf("0") }
    var reminderType by remember { mutableStateOf(ReminderTypes.MINUTE) }

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
                Text(text = "${option.first} ${option.second.name}")
            }
        }
        if (isEditing) {
            Row {
                val itemHeight = 50.dp
                RectangleButton(
                    modifier = Modifier.height(itemHeight),
                    onClick = {
                        onAddReminder(Pair(reminder.toInt(), reminderType.unit))
                    }
                ) {
                    Icon(Icons.Default.Add, "Add Reminder")
                }
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
        }
    }
}
