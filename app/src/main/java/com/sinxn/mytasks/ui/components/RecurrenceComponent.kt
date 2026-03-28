package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecurrenceComponent(
    recurrenceRule: String?,
    onRecurrenceRuleChange: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val frequencies = listOf("None", "Daily", "Weekly", "Monthly", "Yearly")
    var selectedFrequency by remember { mutableStateOf(recurrenceRule?.let { parseFrequency(it) } ?: "None") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Repeat",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (selectedFrequency == "None") "Does not repeat" else "Repeats $selectedFrequency",
                color = if (selectedFrequency == "None") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            frequencies.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedFrequency = selectionOption
                        expanded = false
                        onRecurrenceRuleChange(if (selectionOption == "None") null else "FREQ=${selectionOption.uppercase()}")
                    }
                )
            }
        }
    }
}

private fun parseFrequency(rrule: String): String {
    val parts = rrule.split(";")
    val freqPart = parts.find { it.startsWith("FREQ=") }
    return freqPart?.substringAfter("=")?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "None"
}
