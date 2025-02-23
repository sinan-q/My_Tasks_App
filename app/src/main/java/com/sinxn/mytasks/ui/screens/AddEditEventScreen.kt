package com.sinxn.mytasks.ui.screens

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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
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
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.ui.screens.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    modifier: Modifier = Modifier,
    eventId: Long = -1L,
    folderId: Long = 0,
    date: Long? = null,
    eventViewModel: EventViewModel,
    onFinish: () -> Unit,
) {


    var eventInputState by remember { mutableStateOf(Event()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isDatePickerForStart by remember { mutableStateOf<Boolean?>(null) }
    var isEditing by remember { mutableStateOf(eventId == -1L) }

    val eventState by eventViewModel.event.collectAsState()
    val folder by eventViewModel.folder.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Use a single LaunchedEffect for fetching data
    LaunchedEffect(eventId, folderId, date) {
        if (eventId != -1L) {
            eventViewModel.fetchEventById(eventId)
        } else if (folderId != 0L) {
            eventViewModel.fetchFolderById(folderId)
        } else if (date != null){
            eventInputState = eventInputState.copy(
                start = fromMillis(date)

            )
        }
    }
    // Update the input state when the task state changes
    LaunchedEffect(eventState) {
        eventState?.let { event ->
            eventInputState = eventInputState.copy(
                id = event.id,
                title = event.title,
                folderId = event.folderId,
                description = (event.description),
                start = event.start,
                end = event.end,
                timestamp = event.timestamp,
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEditing) {
                        if (eventInputState.title.isNotEmpty() || eventInputState.description.isNotEmpty() || eventInputState.start != null) {
                            val eventToSave = Event(
                                id = if (eventId == -1L) null else eventId,
                                folderId = eventInputState.folderId,
                                title = eventInputState.title,
                                description = eventInputState.description,
                                start = eventInputState.start,
                                end = eventInputState.end,
                                timestamp = eventInputState.timestamp
                            )
                            eventViewModel.insertEvent(eventToSave)
                            onFinish()
                        } else {
                            //TODO show error
                        }
                    } else {
                        isEditing = true
                    }
                }
            ) {
                Icon(
                    if (!isEditing) Icons.Default.Edit else Icons.Default.Check,
                    contentDescription = if (!isEditing) "Edit Event" else "Save Event"
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(if (eventId == -1L) "Add Event" else "Edit Event") },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (eventId != -1L) {
                        IconButton(onClick = {
                            eventState?.let { eventViewModel.deleteEvent(it) }
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
                value = eventInputState.title,
                onValueChange = { eventInputState = eventInputState.copy(title = it) },
                label = { Text("Title") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(folder?.name ?: "Parent")
            OutlinedTextField(
                value = eventInputState.description,
                onValueChange = { eventInputState = eventInputState.copy(description = it) },
                label = { Text("Description") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = eventInputState.start?.formatDate() ?: "No Start Date",
                onValueChange = {},
                label = { Text("Start Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        isDatePickerForStart = true
                        showDatePicker = isEditing
                    }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Start Date"
                        )
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = eventInputState.end?.formatDate() ?: "No End Date",
                onValueChange = {},
                label = { Text("End Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        isDatePickerForStart = false
                        showDatePicker = isEditing
                    }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select End Date"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = eventInputState.start?.toMillis()?:eventInputState.end?.toMillis()?: Instant.now().toEpochMilli()
                )

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (isDatePickerForStart == true) {
                                    eventInputState = eventInputState.copy(
                                        start = datePickerState.selectedDateMillis?.let { fromMillis(it) }
                                    )
                                }
                                else if (isDatePickerForStart == false){
                                    eventInputState = eventInputState.copy(
                                        end = datePickerState.selectedDateMillis?.let { fromMillis(it) }
                                    )
                                }
                                showDatePicker = false
                                showTimePicker = true
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            isDatePickerForStart = null
                        }) {
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
                    onDismiss = {
                        showTimePicker = false;
                        isDatePickerForStart = null
                    },
                    onConfirm = {
                        if (isDatePickerForStart == true)
                            eventInputState = eventInputState.copy(
                                start = mergeDateAndTime(eventInputState.start!! , timePickerState)
                            )
                        else if (isDatePickerForStart == false)
                            eventInputState = eventInputState.copy(
                                end = mergeDateAndTime(eventInputState.end!! , timePickerState)
                            )
                        isDatePickerForStart = null
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

@OptIn(ExperimentalMaterial3Api::class)
fun mergeDateAndTime(date: LocalDateTime, timePickerState: TimePickerState): LocalDateTime {
    return date.withHour(timePickerState.hour).withMinute(timePickerState.minute)
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

// Extension function for formatting Date
fun LocalDateTime.formatDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
    return this.format(formatter)
}

fun fromMillis(millis :Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
}

fun LocalDateTime.toMillis(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

