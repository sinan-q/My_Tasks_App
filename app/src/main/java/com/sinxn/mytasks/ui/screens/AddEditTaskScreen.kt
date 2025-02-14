package com.yourpackage.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.screens.viewmodel.TaskViewModel

@Composable
fun AddEditTaskScreen(
    modifier: Modifier = Modifier,
    taskId: Long = -1L,
    taskViewModel: TaskViewModel,
    onSaveTask: () -> Unit,
    onCancel: () -> Unit,
) {
    val taskState by taskViewModel.task.collectAsState()
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(taskId) {
        if (taskId != -1L) {
            taskViewModel.fetchTaskById(taskId)
        }
    }
    LaunchedEffect(taskState) {
        taskState?.let { task ->
            title = TextFieldValue(task.title)
            description = TextFieldValue(task.description)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onCancel) { Text("Cancel") }
            Button(onClick = {
                if (title.text.isNotEmpty() && description.text.isNotEmpty()) {
                    taskViewModel.insertTask(
                        Task(
                            id = if (taskId == -1L) null else taskId,
                            title = title.text,
                            description = description.text
                        )
                    )
                    onSaveTask()
                } else {
                    // Handle empty fields, e.g., show a Toast or Snackbar
                }
            }) { Text("Save") }
        }
    }
}
