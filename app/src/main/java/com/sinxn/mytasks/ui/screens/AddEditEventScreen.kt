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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.ui.components.FolderDropDown
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.screens.viewmodel.EventViewModel
import com.sinxn.mytasks.utils.formatDate
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import java.time.Instant
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    modifier: Modifier = Modifier,
    eventId: Long = -1L,
    folderId: Long = 0,
    date: Long = -1L,
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
    val folders by eventViewModel.folders.collectAsState()

    LaunchedEffect(eventId, folderId, date) {
        if (eventId != -1L) {
            eventViewModel.fetchEventById(eventId)
        } else if (folderId != 0L) {
            eventViewModel.fetchFolderById(folderId)
        }
        val initialDate = if (date != -1L) fromMillis(date) else LocalDateTime.now()
        eventInputState = eventInputState.copy(
                start = initialDate.withHour(10).withMinute(0),
                end = initialDate.withHour(11).withMinute(0),
            )
    }
    // Update the input state when the task state changes
    LaunchedEffect(eventState) {
        eventState?.let { event ->
            eventInputState = event.copy() // Use copy to avoid shared mutable state
        }
    }

    Scaffold(
        floatingActionButton = {
            RectangleFAB(
                onClick = {
                    if (isEditing) {
                        val isInputValid = validateEventInput(eventInputState)

                        if (isInputValid) {
                            val eventToSave = eventInputState.copy(
                                id = if (eventId == -1L) null else eventId,
                            )
                            eventViewModel.insertEvent(eventToSave)
                            isEditing = false
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
            FolderDropDown(
                onClick = { folderId ->
                    eventViewModel.fetchFolderById(folderId)
                },
                isEditing = isEditing,
                folder = folder,
                folders = folders
            )
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
                        showTimePicker = false
                        isDatePickerForStart = null
                    },
                    onConfirm = {
                        if (isDatePickerForStart == true) {
                            eventInputState = eventInputState.copy(
                                start = eventInputState.start?.addTimerPickerState(timePickerState),
                            )
                            if (eventInputState.end!!.isBefore(eventInputState.start))
                                eventInputState = eventInputState.copy(
                                    end = eventInputState.start?.plusHours(1),
                                )
                        } else if (isDatePickerForStart == false)
                            eventInputState = eventInputState.copy(
                                end = eventInputState.end?.addTimerPickerState(timePickerState)
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

fun validateEventInput(eventInputState: Event): Boolean {
    return !(eventInputState.title.isEmpty() && eventInputState.description.isEmpty()) &&
            eventInputState.start != null &&
            eventInputState.end != null &&
            !eventInputState.start.isAfter(eventInputState.end)
}

@OptIn(ExperimentalMaterial3Api::class)
fun LocalDateTime.addTimerPickerState(timePickerState: TimePickerState): LocalDateTime {
    return this.withHour(timePickerState.hour).withMinute(timePickerState.minute)
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
