import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.screens.addTimerPickerState
import com.sinxn.mytasks.ui.screens.formatDate
import com.sinxn.mytasks.ui.screens.fromMillis
import com.sinxn.mytasks.ui.screens.toMillis
import com.sinxn.mytasks.ui.screens.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    modifier: Modifier = Modifier,
    taskId: Long = -1L,
    folderId: Long = 0,
    taskViewModel: TaskViewModel,
    onFinish: () -> Unit,
) {


    var taskInputState by remember { mutableStateOf(Task()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(taskId == -1L) }

    val taskState by taskViewModel.task.collectAsState()
    val folder by taskViewModel.folder.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Use a single LaunchedEffect for fetching data
    LaunchedEffect(taskId, folderId) {
        if (taskId != -1L) {
            taskViewModel.fetchTaskById(taskId)
        } else {
            taskViewModel.fetchFolderById(folderId)
        }
    }
    // Update the input state when the task state changes
    LaunchedEffect(taskState) {
        taskState?.let { task ->
            taskInputState = taskInputState.copy(
                title = (task.title),
                folderId = task.folderId,
                description = (task.description),
                due = task.due,
                timestamp = task.timestamp,
                isCompleted = task.isCompleted,
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEditing) {
                        if (taskInputState.title.isNotEmpty() || taskInputState.description.isNotEmpty()) {
                            val taskToSave = Task(
                                id = if (taskId == -1L) null else taskId,
                                folderId = taskInputState.folderId,
                                title = taskInputState.title,
                                description = taskInputState.description,
                                due = taskInputState.due,
                                isCompleted = taskInputState.isCompleted,
                                timestamp = taskInputState.timestamp
                            )
                            taskViewModel.insertTask(taskToSave)
                            scope.launch {
                                snackBarHostState.showSnackbar("Task Saved", duration = SnackbarDuration.Short)

                            }
                            onFinish()
                        } else {
                            scope.launch {
                                snackBarHostState.showSnackbar("Title or description cannot be empty")
                            }
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
            TopAppBar(
                title = { Text(if (taskId == -1L) "Add Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (taskId != -1L) {
                        IconButton(onClick = {
                            taskState?.let { taskViewModel.deleteTask(it) }
                            onFinish()
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
            OutlinedTextField(
                value = taskInputState.title,
                onValueChange = { taskInputState = taskInputState.copy(title = it) },
                label = { Text("Title") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(folder?.name ?: "Parent")
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

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = taskInputState.due?.toMillis() ?: Instant.now().toEpochMilli()
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
}


@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}