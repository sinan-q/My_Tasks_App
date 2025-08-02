package com.sinxn.mytasks.ui.screens.taskScreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinxn.mytasks.R
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.RectangleButton
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.components.ScrollablePicker
import com.sinxn.mytasks.ui.components.TimePickerDialog
import com.sinxn.mytasks.ui.components.rememberPressBackTwiceState
import com.sinxn.mytasks.ui.screens.folderScreen.FolderDropDown
import com.sinxn.mytasks.ui.viewModels.TaskViewModel
import com.sinxn.mytasks.utils.ReminderTypes
import com.sinxn.mytasks.utils.addTimerPickerState
import com.sinxn.mytasks.utils.formatDate
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    modifier: Modifier = Modifier,
    taskId: Long = -1L,
    folderId: Long = 0,
    taskViewModel: TaskViewModel = hiltViewModel(),
    onFinish: () -> Unit,
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for dialog

    val context = LocalContext.current

    var taskInputState by remember { mutableStateOf(Task()) }
    val reminders by taskViewModel.reminders.collectAsState(

    )
    var reminder by remember { mutableStateOf("0") }
    var expandedDropDown by remember { mutableStateOf(false) }
    var reminderType by remember { mutableStateOf(ReminderTypes.MINUTE) }


    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(taskId == -1L) }

    val taskState by taskViewModel.task.collectAsState()
    val folder by taskViewModel.folder.collectAsState()
    val folders by taskViewModel.folders.collectAsState()

    fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    LaunchedEffect(key1 = Unit) { // key1 = Unit makes it run once on composition
        taskViewModel.toastMessage.collectLatest { message -> // or .collect {
            showToast(message)
        }
    }

    val handleBackPressAttempt = rememberPressBackTwiceState(
        enabled = isEditing, // Only require double press if currently editing
        onExit = onFinish,
    )
    BackHandler(onBack = handleBackPressAttempt)

    LaunchedEffect(taskId, folderId) {
        if (taskId != -1L) {
            taskViewModel.fetchTaskById(taskId)
        } else {
            taskInputState = Task()
            taskViewModel.fetchFolderById(folderId)
        }
    }

    LaunchedEffect(taskState) {
        taskState.let { task ->
            taskInputState = task.copy()
        }
    }

    Scaffold(
        floatingActionButton = {
            RectangleFAB(
                onClick = {
                    if (isEditing) {
                        if (taskInputState.title.isNotEmpty() || taskInputState.description.isNotEmpty()) {
                            val taskToSave = taskInputState.copy(
                                id = if (taskId == -1L) null else taskId,
                            )
                            taskViewModel.insertTask(taskToSave, reminders)
                            isEditing = false
                        } else {
                            Toast.makeText(context, "Title or Description cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        isEditing = true
                    }
                }
            ) {
                Icon(
                    if (!isEditing) Icons.Default.Edit else Icons.Default.Check,
                    contentDescription = if (!isEditing) "Edit Task" else "Save Task"
                )
            }
        },
        topBar = {
            MyTasksTopAppBar(
                title = { Text(if (taskId == -1L) "Add Task" else "Edit Task")},
                onNavigateUp = handleBackPressAttempt,
                actions = {
                    if (taskId != -1L) {
                        IconButton(onClick = {
                            showDeleteConfirmationDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = taskInputState.title,
                onValueChange = { taskInputState = taskInputState.copy(title = it) },
                label = { Text("Title") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            FolderDropDown(
                onClick = {folderId ->
                    taskViewModel.fetchFolderById(folderId)
                },
                isEditing = isEditing,
                folder = folder,
                folders = folders
            )
            OutlinedTextField(
                value = taskInputState.description,
                onValueChange = { taskInputState = taskInputState.copy(description = it) },
                label = { Text("Description") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = taskInputState.due?.formatDate() ?: "No Due",
                onValueChange = {},
                label = { Text("Due Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = isEditing }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Due Date"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (taskInputState.due != null) {
                Text(text = "Set Reminders on " )
                reminders.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { taskViewModel.removeReminder(option) }) {
                            Icon(Icons.Default.Close, contentDescription = "Delete Reminder")
                        }
                        Text(text = "${option.first} ${option.second.name}")
                    }
                }
                Row  {
                    val itemHeight = 50.dp
                    RectangleButton(
                        modifier = Modifier.height(itemHeight),
                        onClick = {
                            taskViewModel.addReminder(Pair(reminder.toInt(), reminderType.unit))
                        }
                    ) {
                        Icon(Icons.Default.Add, "Add Reminder")
                    }
                    ScrollablePicker (
                        values = (0..60).toList(),
                        defaultValue = 0,
                        height = itemHeight,
                        modifier = Modifier.width(70.dp).height(itemHeight)
                    ) {
                        reminder = it.toString()
                    }
//                    OutlinedTextField(
//                        value = reminder,
//                        onValueChange = { reminder = it},
//                        modifier = Modifier.width(70.dp).height(itemHeight),
//                        singleLine = true
//                    )
                    ScrollablePicker (
                        values = ReminderTypes.entries,
                        defaultValue = ReminderTypes.MINUTE,
                        height = itemHeight,
                        modifier = Modifier.width(100.dp).height(itemHeight)
                    ) {
                        reminderType = it
                    }
//                    ExposedDropdownMenuBox(
//                        expanded = expandedDropDown,
//                        onExpandedChange = { expandedDropDown = !expandedDropDown },
//                    ) {
//                        OutlinedTextField(
//                            value = reminderType.label,
//                            onValueChange = {},
//                            readOnly = true,
//                            trailingIcon = {
//                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropDown)
//                            },
//                            modifier = Modifier
//                                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
//                                .width(150.dp)
//                        )
////                        ExposedDropdownMenu(
////                            expanded = expandedDropDown,
////                            onDismissRequest = { expandedDropDown = false },
////                        ) {
//
////                            ReminderTypes.entries.forEach { option ->
////
////                                DropdownMenuItem(
////                                    text = { Text(option.label) },
////                                    onClick = {
////                                        reminderType = option
////                                        expandedDropDown = false
////                                    }
////                                )
////                            }
//                       // }
//                    }

                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = taskInputState.due?.toMillis() ?: LocalDateTime.now().plusDays(1).toMillis()
                )

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                taskInputState = taskInputState.copy(
                                    due = datePickerState.selectedDateMillis?.let { fromMillis(it) }
                                )
                                showDatePicker = false
                                showTimePicker = true
                            }
                        ) {
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
                        taskInputState = taskInputState.copy(
                            due = taskInputState.due?.addTimerPickerState(timePickerState))
                        showTimePicker = false
                    }
                ) {
                    TimePicker(
                        state = timePickerState,
                    )
                }
            }
        }
    }

    ConfirmationDialog(
        showDialog = showDeleteConfirmationDialog,
        onDismiss = { showDeleteConfirmationDialog = false },
        onConfirm = {
            taskViewModel.deleteTask(taskState)
            showDeleteConfirmationDialog = false
            onFinish()
        },
        title = stringResource(R.string.delete_confirmation_title),
        message = stringResource(R.string.delete_item_message)
    )
}

@Preview
@Composable
fun PreviewEditTaskScreen() {
    AddEditTaskScreen(
        onFinish = {}
    )
}