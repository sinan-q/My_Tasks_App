package com.sinxn.mytasks.ui.features.events

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinxn.mytasks.R
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.MyTextField
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.components.TimePickerDialog
import com.sinxn.mytasks.ui.components.rememberPressBackTwiceState
import com.sinxn.mytasks.ui.features.folders.FolderDropDown
import com.sinxn.mytasks.utils.Constants
import com.sinxn.mytasks.utils.addTimerPickerState
import com.sinxn.mytasks.utils.formatDate
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    eventId: Long = -1L,
    folderId: Long = 0,
    date: Long = -1L,
    eventViewModel: EventViewModel = hiltViewModel(),
    onFinish: () -> Unit,
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for dialog

    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isDatePickerForStart by remember { mutableStateOf<Boolean?>(null) }
    var isEditing by remember { mutableStateOf(eventId == -1L) }

    val uiState by eventViewModel.uiState.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val handleBackPressAttempt = rememberPressBackTwiceState(
        enabled = isEditing,
        onExit = onFinish,
    )
    BackHandler(onBack = handleBackPressAttempt)

    fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        if (message in listOf(Constants.NOT_FOUND, Constants.SAVE_SUCCESS, Constants.DELETE_SUCCESS)) onFinish()
    }
    LaunchedEffect(key1 = Unit) {
        eventViewModel.toastMessage.collectLatest { message ->
            showToast(message)
        }
    }
    LaunchedEffect(eventId, folderId, date) {
        if (eventId != -1L) {
            eventViewModel.fetchEventById(eventId)
        } else if (folderId != 0L) {
            eventViewModel.fetchFolderById(folderId)
        }
        val initialDate = if (date != -1L) fromMillis(date) else LocalDateTime.now().plusDays(1)
        (uiState as? EventScreenUiState.Success)?.let {
            eventViewModel.onUpdateEvent(it.event.copy(
                start = initialDate.withHour(10).withMinute(0),
                end = initialDate.withHour(11).withMinute(0),
            ))
        }
    }

    when (val state = uiState) {
        is EventScreenUiState.Loading -> {
            Text(text = "Loading...")
        }
        is EventScreenUiState.Error -> {
            Text(text = state.message)
        }
        is EventScreenUiState.Success -> {
            val eventInputState = state.event
            val folder = state.folder
            val folders = state.folders

            LaunchedEffect(Unit) {
                if (eventId == -1L) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            }

            Scaffold(
                floatingActionButton = {
                    RectangleFAB(
                        onClick = {
                            if (isEditing) {
                                eventViewModel.insertEvent(eventInputState)
                                isEditing = false

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
                    MyTasksTopAppBar(
                        onNavigateUp = handleBackPressAttempt,
                        actions = {
                            if (eventId != -1L) {
                                IconButton(onClick = { showDeleteConfirmationDialog = true }) {
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    MyTextField(
                        value = eventInputState.title,
                        onValueChange = { eventViewModel.onUpdateEvent(eventInputState.copy(title = it)) },
                        placeholder = "Title",
                        readOnly = !isEditing,
                        textStyle = TextStyle.Default.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    FolderDropDown(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        onClick = { folderId ->
                            eventViewModel.fetchFolderById(folderId)
                        },
                        isEditing = isEditing,
                        folder = folder,
                        folders = folders
                    )
                    HorizontalDivider()
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 20.dp)
                    )
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 20.dp)
                    )
                    HorizontalDivider()
                    MyTextField(
                        value = eventInputState.description,
                        onValueChange = { eventViewModel.onUpdateEvent(eventInputState.copy(description = it)) },
                        placeholder = "Description",
                        readOnly = !isEditing,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
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
                                            eventViewModel.onUpdateEvent(eventInputState.copy(
                                                start = datePickerState.selectedDateMillis?.let { fromMillis(it) }
                                            ))
                                        }
                                        else if (isDatePickerForStart == false){
                                            eventViewModel.onUpdateEvent(eventInputState.copy(
                                                end = datePickerState.selectedDateMillis?.let { fromMillis(it) }
                                            ))
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
                                    eventViewModel.onUpdateEvent(eventInputState.copy(
                                        start = eventInputState.start?.addTimerPickerState(timePickerState),
                                    ))
                                    if (eventInputState.end!!.isBefore(eventInputState.start))
                                        eventViewModel.onUpdateEvent(eventInputState.copy(
                                            end = eventInputState.start?.plusHours(1),
                                        ))
                                } else if (isDatePickerForStart == false)
                                    eventViewModel.onUpdateEvent(eventInputState.copy(
                                        end = eventInputState.end?.addTimerPickerState(timePickerState)
                                    ))
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
            ConfirmationDialog(
                showDialog = showDeleteConfirmationDialog,
                onDismiss = { showDeleteConfirmationDialog = false },
                onConfirm = {
                    eventViewModel.deleteEvent(eventInputState)
                    showDeleteConfirmationDialog = false },
                title = stringResource(R.string.delete_confirmation_title),
                message = stringResource(R.string.delete_item_message)
            )
        }
    }
}