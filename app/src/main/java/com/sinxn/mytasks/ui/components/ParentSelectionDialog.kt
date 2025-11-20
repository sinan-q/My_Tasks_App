package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.domain.models.Event
import com.sinxn.mytasks.domain.models.RelationItemType
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.models.Task

data class ParentItemOption(
    val id: Long,
    val title: String,
    val type: RelationItemType
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentSelectionDialog(
    onDismiss: () -> Unit,
    onSelect: (ParentItemOption) -> Unit,
    tasks: List<Task>,
    events: List<Event>,
    notes: List<Note>,
    currentId: Long? = null, // To avoid selecting itself
    currentType: RelationItemType? = null
) {
    val sheetState = rememberModalBottomSheetState()
    var searchQuery by remember { mutableStateOf("") }

    val allOptions = remember(tasks, events, notes) {
        val options = mutableListOf<ParentItemOption>()
        options.addAll(tasks.map { ParentItemOption(it.id ?: 0, it.title, RelationItemType.TASK) })
        options.addAll(events.map { ParentItemOption(it.id ?: 0, it.title, RelationItemType.EVENT) })
        options.addAll(notes.map { ParentItemOption(it.id ?: 0, it.title, RelationItemType.NOTE) })
        
        // Filter out itself if editing
        if (currentId != null && currentType != null) {
            options.filterNot { it.id == currentId && it.type == currentType }
        } else {
            options
        }
    }

    val filteredOptions = remember(allOptions, searchQuery) {
        if (searchQuery.isBlank()) {
            allOptions
        } else {
            allOptions.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Select Parent Item",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(filteredOptions) { option ->
                    ParentItemRow(option = option, onClick = { onSelect(option) })
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ParentItemRow(
    option: ParentItemOption,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "[${option.type.name}]",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = option.title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
