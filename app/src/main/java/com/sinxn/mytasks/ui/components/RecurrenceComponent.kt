package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceComponent(
    recurrenceRule: String?,
    onRecurrenceRuleChange: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val frequencies = listOf("None", "Daily", "Weekly", "Monthly", "Yearly")
    var selectedFrequency by remember { mutableStateOf(recurrenceRule?.let { parseFrequency(it) } ?: "None") }

    Column(modifier = Modifier.padding(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                readOnly = true,
                value = selectedFrequency,
                onValueChange = { },
                label = { Text("Repeats") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                frequencies.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectionOption) },
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
}

private fun parseFrequency(rrule: String): String {
    val parts = rrule.split(";")
    val freqPart = parts.find { it.startsWith("FREQ=") }
    return freqPart?.substringAfter("=")?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "None"
}
