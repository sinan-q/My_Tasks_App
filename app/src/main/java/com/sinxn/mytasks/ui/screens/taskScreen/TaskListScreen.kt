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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.components.RectangleFAB

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    taskViewModel: TaskViewModel,
    onAddTaskClick: (Long?) -> Unit,
    onTaskClick: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            RectangleFAB(onClick = { onAddTaskClick(0L) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    path = taskViewModel.getPath(task.folderId),
                    onClick = { onTaskClick(task.id) },
                    onUpdate = { task.id?.let { it1 -> taskViewModel.updateStatusTask(it1,it) } })
            }
        }
    }
}
