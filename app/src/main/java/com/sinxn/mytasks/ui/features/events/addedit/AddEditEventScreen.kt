package com.sinxn.mytasks.ui.features.events.addedit

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinxn.mytasks.R
import com.sinxn.mytasks.domain.models.RelationItemType
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.MyTextField
import com.sinxn.mytasks.ui.components.ParentSelectionDialog
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.components.RecurrenceComponent
import com.sinxn.mytasks.ui.components.TimePickerDialog
import com.sinxn.mytasks.ui.components.rememberPressBackTwiceState
import com.sinxn.mytasks.ui.features.folders.FolderDropDown
import com.sinxn.mytasks.utils.Constants
import com.sinxn.mytasks.utils.addTimerPickerState
import com.sinxn.mytasks.utils.formatDate
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    eventId: Long = -1L,
    folderId: Long = 0,
    date: Long = -1L,
    eventViewModel: AddEditEventViewModel = hiltViewModel(),
    onFinish: () -> Unit,
    onNavigateToItem: (Long, RelationItemType) -> Unit = { _, _ -> }
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    val uiState by eventViewModel.uiState.collectAsState()

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showParentSelectionDialog by remember { mutableStateOf(false) }

    var isEditing by remember { mutableStateOf(eventId == -1L) }

    val allTasks by eventViewModel.allTasks.collectAsState(initial = emptyList())
    val allEvents by eventViewModel.allEvents.collectAsState(initial = emptyList())
    val allNotes by eventViewModel.allNotes.collectAsState(initial = emptyList())

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        if (message in listOf(Constants.SAVE_SUCCESS, Constants.DELETE_SUCCESS, Constants.NOT_FOUND)) onFinish()
    }

    LaunchedEffect(key1 = Unit) {
        eventViewModel.toastMessage.collectLatest { message ->
            showToast(message)
        }
    }

    val handleBackPressAttempt = rememberPressBackTwiceState(
        enabled = isEditing,
        onExit = onFinish,
    )
    BackHandler(onBack = handleBackPressAttempt)

    LaunchedEffect(eventId, folderId) {
        if (eventId != -1L) {
            eventViewModel.onAction(AddEditEventAction.FetchEventById(eventId))
        } else {
            eventViewModel.onAction(AddEditEventAction.FetchFolderById(folderId))
        }
    }

    val eventInputState = uiState.event
    val folder = uiState.folder
    val folders = uiState.folders

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
                        eventViewModel.onAction(AddEditEventAction.InsertEvent(eventInputState))
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MyTextField(
                value = eventInputState.title,
                onValueChange = { eventViewModel.onAction(AddEditEventAction.UpdateEvent(eventInputState.copy(title = it))) },
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
                onClick = { folderId ->
                    eventViewModel.onAction(AddEditEventAction.FetchFolderById(folderId))
                },
                isEditing = isEditing,
                folder = folder,
                folders = folders
            )
            HorizontalDivider()
            MyTextField(
                value = eventInputState.description,
                onValueChange = { eventViewModel.onAction(AddEditEventAction.UpdateEvent(eventInputState.copy(description = it))) },
                placeholder = "Description",
                readOnly = !isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            
            // Start Date
            OutlinedTextField(
                value = eventInputState.start?.formatDate() ?: "Select Start Date",
                onValueChange = {},
                label = { Text("Start Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = isEditing }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Start Date"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 20.dp)
            )

            // End Date
            OutlinedTextField(
                value = eventInputState.end?.formatDate() ?: "Select End Date",
                onValueChange = {},
                label = { Text("End Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = isEditing }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select End Date"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 20.dp)
            )

            RecurrenceComponent(
                recurrenceRule = eventInputState.recurrenceRule,
                onRecurrenceRuleChange = {
                    eventViewModel.onAction(AddEditEventAction.UpdateEvent(eventInputState.copy(recurrenceRule = it)))
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Parent Item Section
            Text(
                text = "Parent Item",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            if (uiState.parentItem != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToItem(uiState.parentItem!!.id, uiState.parentItem!!.type) }
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "[${uiState.parentItem!!.type.name}]",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = uiState.parentItem!!.title,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (isEditing) {
                        IconButton(onClick = { eventViewModel.onAction(AddEditEventAction.RemoveParent) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Parent")
                        }
                    }
                }
            } else if (isEditing) {
                TextButton(
                    onClick = { showParentSelectionDialog = true },
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text("Add Parent Item")
                }
            }

            // Related Items Section
            if (uiState.relatedItems.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Related Items",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                uiState.relatedItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToItem(item.id, item.type) }
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "[${item.type.name}]",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Date/Time Pickers
            if (showStartDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = eventInputState.start?.toMillis() ?: LocalDateTime.now().toMillis()
                )
                DatePickerDialog(
                    onDismissRequest = { showStartDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            eventViewModel.onAction(
                                AddEditEventAction.UpdateEvent(
                                    eventInputState.copy(
                                        start = datePickerState.selectedDateMillis?.let { fromMillis(it) }
                                    )
                                )
                            )
                            showStartDatePicker = false
                            showStartTimePicker = true
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
                    }
                ) { DatePicker(state = datePickerState) }
            }

            if (showStartTimePicker) {
                val timePickerState = rememberTimePickerState()
                TimePickerDialog(
                    onDismiss = { showStartTimePicker = false },
                    onConfirm = {
                        eventViewModel.onAction(
                            AddEditEventAction.UpdateEvent(
                                eventInputState.copy(
                                    start = eventInputState.start?.addTimerPickerState(timePickerState)
                                )
                            )
                        )
                        showStartTimePicker = false
                    }
                ) { TimePicker(state = timePickerState) }
            }

            if (showEndDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = eventInputState.end?.toMillis() ?: LocalDateTime.now().plusHours(1).toMillis()
                )
                DatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            eventViewModel.onAction(
                                AddEditEventAction.UpdateEvent(
                                    eventInputState.copy(
                                        end = datePickerState.selectedDateMillis?.let { fromMillis(it) }
                                    )
                                )
                            )
                            showEndDatePicker = false
                            showEndTimePicker = true
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
                    }
                ) { DatePicker(state = datePickerState) }
            }

            if (showEndTimePicker) {
                val timePickerState = rememberTimePickerState()
                TimePickerDialog(
                    onDismiss = { showEndTimePicker = false },
                    onConfirm = {
                        eventViewModel.onAction(
                            AddEditEventAction.UpdateEvent(
                                eventInputState.copy(
                                    end = eventInputState.end?.addTimerPickerState(timePickerState)
                                )
                            )
                        )
                        showEndTimePicker = false
                    }
                ) { TimePicker(state = timePickerState) }
            }

            if (showParentSelectionDialog) {
                ParentSelectionDialog(
                    onDismiss = { showParentSelectionDialog = false },
                    onSelect = {
                        eventViewModel.onAction(AddEditEventAction.SetParent(it))
                        showParentSelectionDialog = false
                    },
                    tasks = allTasks,
                    events = allEvents,
                    notes = allNotes,
                    currentId = eventId,
                    currentType = RelationItemType.EVENT
                )
            }
        }

        ConfirmationDialog(
            showDialog = showDeleteConfirmationDialog,
            onDismiss = { showDeleteConfirmationDialog = false },
            onConfirm = {
                eventViewModel.onAction(AddEditEventAction.DeleteEvent(eventInputState))
                showDeleteConfirmationDialog = false
                onFinish()
            },
            title = stringResource(R.string.delete_confirmation_title),
            message = stringResource(R.string.delete_item_message)
        )
    }
}