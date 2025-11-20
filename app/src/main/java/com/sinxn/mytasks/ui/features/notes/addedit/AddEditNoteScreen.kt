package com.sinxn.mytasks.ui.features.notes.addedit

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
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
import com.sinxn.mytasks.ui.components.rememberPressBackTwiceState
import com.sinxn.mytasks.ui.features.folders.FolderDropDown
import com.sinxn.mytasks.ui.components.ParentSelectionDialog
import com.sinxn.mytasks.ui.components.ParentItemOption
import androidx.compose.foundation.clickable
import com.sinxn.mytasks.domain.models.RelationItemType
import com.sinxn.mytasks.ui.features.notes.list.NoteScreenUiState
import com.sinxn.mytasks.utils.Constants
import com.sinxn.mytasks.utils.formatDate
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditNoteScreen(
    noteId: Long = -1L,
    folderId: Long = 0,
    noteViewModel: AddEditNoteViewModel = hiltViewModel(),
    onFinish: () -> Unit,
    onNavigateToItem: (Long, RelationItemType) -> Unit = { _, _ -> }
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for dialog

    val context = LocalContext.current
    val uiState by noteViewModel.uiState.collectAsState()
    var isEditing by remember { mutableStateOf(noteId == -1L) }
    var showParentSelectionDialog by remember { mutableStateOf(false) }

    val allTasks by noteViewModel.allTasks.collectAsState(initial = emptyList())
    val allEvents by noteViewModel.allEvents.collectAsState(initial = emptyList())
    val allNotes by noteViewModel.allNotes.collectAsState(initial = emptyList())

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val handleBackPressAttempt = rememberPressBackTwiceState(
        enabled = isEditing, // Only require double press if currently editing
        onExit = onFinish,
        message = "Press Back Again to cancel changes"
    )
    BackHandler(onBack = handleBackPressAttempt)


    LaunchedEffect(noteId, folderId) {
        if (noteId != -1L) {
            noteViewModel.onAction(AddEditNoteAction.FetchNoteById(noteId))
        } else {
            noteViewModel.onAction(AddEditNoteAction.NewNoteByFolder(folderId))
        }
    }

    fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        if (message in listOf(Constants.SAVE_SUCCESS, Constants.DELETE_SUCCESS, Constants.NOT_FOUND)) onFinish()
    }
    LaunchedEffect(key1 = Unit) { // key1 = Unit makes it run once on composition
        noteViewModel.toastMessage.collectLatest { message -> // or .collect {
            showToast(message)
        }
    }

    when (val state = uiState) {
        is NoteScreenUiState.Loading -> {
            Text("Loading...")
        }
        is NoteScreenUiState.Error -> {
            Text(state.message)
        }
        is NoteScreenUiState.Success -> {
            val noteInputState = state.note
            val folder = state.folder
            val subFolders = state.folders

            LaunchedEffect(Unit) {
                if (noteId == -1L) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            }

            Scaffold(
                floatingActionButton = {
                    RectangleFAB(
                        onClick = {
                            if (isEditing) {
                                if (noteInputState.title.isNotEmpty() || noteInputState.content.isNotEmpty()) {
                                    noteViewModel.onAction(AddEditNoteAction.InsertNote(noteInputState))
                                    isEditing = false
                                } else {
                                    showToast("Note cannot be empty")
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
                    MyTasksTopAppBar(
                        onNavigateUp = handleBackPressAttempt,
                        actions = {
                            if (noteId != -1L) {
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
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    MyTextField(
                        value = noteInputState.title,
                        onValueChange = { noteViewModel.onAction(AddEditNoteAction.UpdateNote(noteInputState.copy(title = it))) },
                        readOnly = !isEditing,
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                        placeholder = "Title",
                        textStyle = TextStyle.Default.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    )
                    HorizontalDivider()
                    Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Icon(painterResource(R.drawable.clock_ic), contentDescription = "Clock Icon", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(noteInputState.timestamp.formatDate(), fontSize = 12.sp)
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        FolderDropDown(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            onClick = { folderId ->
                                noteViewModel.onAction(AddEditNoteAction.FetchFolderById(folderId))
                            },
                            isEditing = isEditing,
                            folder = folder,
                            folders = subFolders
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Parent Item Section
                    Text(
                        text = "Parent Item",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                    
                    if (state.parentItem != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToItem(state.parentItem!!.id, state.parentItem!!.type) }
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "[${state.parentItem!!.type.name}]",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = state.parentItem!!.title,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            if (isEditing) {
                                IconButton(onClick = { noteViewModel.onAction(AddEditNoteAction.RemoveParent) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Parent")
                                }
                            }
                        }
                    } else if (isEditing) {
                        androidx.compose.material3.TextButton(
                            onClick = { showParentSelectionDialog = true },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            Text("Add Parent Item")
                        }
                    }

                    // Related Items Section
                    if (state.relatedItems.isNotEmpty()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "Related Items",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                        state.relatedItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToItem(item.id, item.type) }
                                    .padding(horizontal = 20.dp, vertical = 4.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "[${item.type.name}]",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    HorizontalDivider()
                    if (isEditing) {
                        MyTextField(
                            value = noteInputState.content,
                            onValueChange = { noteViewModel.onAction(AddEditNoteAction.UpdateNote(noteInputState.copy(content = it))) },
                            modifier = Modifier.fillMaxSize(),
                            placeholder = "Content",
                            textStyle = TextStyle.Default.copy(
                                fontSize = 16.sp
                            )
                        )
                    } else {
                        MarkdownText(
                            markdown = noteInputState.content,
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            style = TextStyle.Default.copy(
                                fontSize = 16.sp
                            )
                        )
                    }
                }
                }
                
                if (showParentSelectionDialog) {
                    ParentSelectionDialog(
                        onDismiss = { showParentSelectionDialog = false },
                        onSelect = { 
                            noteViewModel.onAction(AddEditNoteAction.SetParent(it))
                            showParentSelectionDialog = false
                        },
                        tasks = allTasks,
                        events = allEvents,
                        notes = allNotes,
                        currentId = noteId,
                        currentType = RelationItemType.NOTE
                    )
                }

            ConfirmationDialog(
                showDialog = showDeleteConfirmationDialog,
                onDismiss = { showDeleteConfirmationDialog = false },
                onConfirm = {
                    noteViewModel.onAction(AddEditNoteAction.DeleteNote(noteInputState))
                    showDeleteConfirmationDialog = false
                },
                title = stringResource(R.string.delete_confirmation_title),
                message = stringResource(R.string.delete_item_message)
            )
        }
    }
}
