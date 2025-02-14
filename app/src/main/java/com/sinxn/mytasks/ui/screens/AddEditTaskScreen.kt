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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import com.sinxn.mytasks.utils.formatDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    modifier: Modifier = Modifier,
    taskId: Long = -1L,
    taskViewModel: TaskViewModel,
    onFinish: () -> Unit,
) {
    val taskState by taskViewModel.task.collectAsState()
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var timestamp by remember { mutableStateOf<Date?>(null) }
    var isCompleted by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(taskId == -1L) }

    LaunchedEffect(taskId) {
        if (taskId != -1L) {
            taskViewModel.fetchTaskById(taskId)
        }
    }
    LaunchedEffect(taskState) {
        taskState?.let { task ->
            dueDate = task.due
            title = TextFieldValue(task.title)
            description = TextFieldValue(task.description)
            timestamp = task.timestamp
            isCompleted = task.isCompleted
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEditing) {
                        if (title.text.isNotEmpty() || description.text.isNotEmpty()) {
                            taskViewModel.insertTask(
                                Task(
                                    id = if (taskId == -1L) null else taskId,
                                    title = title.text,
                                    description = description.text,
                                    due = dueDate,
                                    isCompleted = isCompleted,
                                    timestamp = timestamp ?: Date()
                                )
                            )
                            onFinish()
                        } else {
                            // Handle empty fields, e.g., show a Toast or Snackbar
                        }
                    } else {
                        isEditing = true
                    }
                }
            ) {
                Icon(
                    if (!isEditing) Icons.Default.Edit else Icons.Default.Check,
                    contentDescription = null
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
                })
        },
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = dueDate?.let { formatDate(it) } ?: "No Due",
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
                    initialSelectedDateMillis = dueDate?.time ?: Date().time
                )

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                dueDate = datePickerState.selectedDateMillis?.let { Date(it) }!!
                                showDatePicker = false
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
        }
    }
}