package com.sinxn.mytasks.ui.features.tasks

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinxn.mytasks.R
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.MyTextField
import com.sinxn.mytasks.ui.components.RectangleButton
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.components.ScrollablePicker
import com.sinxn.mytasks.ui.components.TimePickerDialog
import com.sinxn.mytasks.ui.components.rememberPressBackTwiceState
import com.sinxn.mytasks.ui.features.folders.FolderDropDown
import com.sinxn.mytasks.utils.Constants
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

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    val uiState by taskViewModel.uiState.collectAsState()
    var reminder by remember { mutableStateOf("0") }
    var reminderType by remember { mutableStateOf(ReminderTypes.MINUTE) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(taskId == -1L) }

    fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        if (message in listOf(Constants.SAVE_SUCCESS, Constants.DELETE_SUCCESS, Constants.NOT_FOUND)) onFinish()
    }
    LaunchedEffect(key1 = Unit) {
        taskViewModel.toastMessage.collectLatest { message ->
            showToast(message)
        }
    }

    val handleBackPressAttempt = rememberPressBackTwiceState(
        enabled = isEditing,
        onExit = onFinish,
    )
    BackHandler(onBack = handleBackPressAttempt)

    LaunchedEffect(taskId, folderId) {
        if (taskId != -1L) {
            taskViewModel.onAction(AddEditTaskAction.FetchTaskById(taskId))
        } else {
            taskViewModel.onAction(AddEditTaskAction.FetchFolderById(folderId))
        }
    }

    when (val state = uiState) {
        is TaskScreenUiState.Loading -> {
            Text("Loading...")
        }
        is TaskScreenUiState.Error -> {
            Text(state.message)
        }
        is TaskScreenUiState.Success -> {
            val taskInputState = state.task
            val reminders = state.reminders
            val folder = state.folder
            val folders = state.folders

            LaunchedEffect(Unit) {
                if (taskId == -1L) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            }

            Scaffold(
                floatingActionButton = {
                    RectangleFAB(
                        onClick = {
                            if (isEditing) {
                                taskViewModel.onAction(AddEditTaskAction.InsertTask(taskInputState, reminders))
                                isEditing = false
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
                modifier = Modifier.imePadding()
            ) { innerPadding ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    MyTextField(
                        value = taskInputState.title,
                        onValueChange = { taskViewModel.onAction(AddEditTaskAction.UpdateTask(taskInputState.copy(title = it))) },
                        placeholder = "Title",
                        readOnly = !isEditing,
                        singleLine = true,
                        textStyle = TextStyle.Default.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    FolderDropDown(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        onClick = {folderId ->
                            taskViewModel.onAction(AddEditTaskAction.FetchFolderById(folderId))
                        },
                        isEditing = isEditing,
                        folder = folder,
                        folders = folders
                    )
                    HorizontalDivider()
                    MyTextField(
                        value = taskInputState.description,
                        onValueChange = { taskViewModel.onAction(AddEditTaskAction.UpdateTask(taskInputState.copy(description = it))) },
                        placeholder = "Description",
                        readOnly = !isEditing,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 20.dp)
                    )
                    taskInputState.due?.let { dueDate ->
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                            Text(text = "Reminders on " )
                            if (reminders.isEmpty())
                                Text(text = "No Remainders set")

                            reminders.forEach { option ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isEditing) {
                                        IconButton(onClick = { taskViewModel.onAction(AddEditTaskAction.RemoveReminder(option)) }) {
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
                                            taskViewModel.onAction(AddEditTaskAction.AddReminder(Pair(reminder.toInt(), reminderType.unit)))
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

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = taskInputState.due?.toMillis() ?: LocalDateTime.now().plusDays(1).toMillis()
                        )

                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        taskViewModel.onAction(AddEditTaskAction.UpdateTask(taskInputState.copy(
                                            due = datePickerState.selectedDateMillis?.let { fromMillis(it) }
                                        )))
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
                                taskViewModel.onAction(AddEditTaskAction.UpdateTask(taskInputState.copy(
                                    due = taskInputState.due?.addTimerPickerState(timePickerState))))
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
                    taskViewModel.onAction(AddEditTaskAction.DeleteTask(taskInputState))
                    showDeleteConfirmationDialog = false
                    onFinish()
                },
                title = stringResource(R.string.delete_confirmation_title),
                message = stringResource(R.string.delete_item_message)
            )
        }
    }
}

@Preview
@Composable
fun PreviewEditTaskScreen() {
    AddEditTaskScreen(
        onFinish = {}
    )
}
