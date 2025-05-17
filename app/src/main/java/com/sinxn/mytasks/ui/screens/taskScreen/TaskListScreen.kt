package com.sinxn.mytasks.ui.screens.taskScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.components.ListTopAppBar
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.viewModels.TaskViewModel

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    taskViewModel: TaskViewModel,
    onAddTaskClick: (Long?) -> Unit,
    onTaskClick: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var hideLocked by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            RectangleFAB(onClick = { onAddTaskClick(0L) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        topBar = {
            ListTopAppBar(
                expanded = expanded,
                setExpanded = { expanded = it},
                hideLocked = hideLocked,
                setHideLocked = { hideLocked = it }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier
        ) {
            items(tasks) { task ->
                val path = taskViewModel.getPath(task.folderId, hideLocked)
                if (path != null) TaskItem(
                    task = task,
                    path = path,
                    onClick = { onTaskClick(task.id) },
                    onUpdate = { task.id?.let { it1 -> taskViewModel.updateStatusTask(it1,it) } })
            }
        }
    }
}
