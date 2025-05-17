package com.sinxn.mytasks.ui.screens.noteScreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.R
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.ui.components.AddEditTopAppBar
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.components.rememberPressBackTwiceState
import com.sinxn.mytasks.ui.screens.folderScreen.FolderDropDown
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditNoteScreen(
    modifier: Modifier = Modifier,
    noteId: Long = -1L,
    folderId: Long = 0,
    noteViewModel: NoteViewModel,
    onFinish: () -> Unit,
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for dialog

    val context = LocalContext.current
    var noteInputState by remember { mutableStateOf(Note()) }
    var isEditing by remember { mutableStateOf(noteId == -1L) }
    val noteState by noteViewModel.note.collectAsState()
    val folder by noteViewModel.folder.collectAsState()
    val subFolders by noteViewModel.subFolders.collectAsState()

    val handleBackPressAttempt = rememberPressBackTwiceState(
        enabled = isEditing, // Only require double press if currently editing
        onExit = onFinish,
        message = "Press Back Again to cancel changes"
    )
    BackHandler(onBack = handleBackPressAttempt)


    LaunchedEffect(noteId, folderId) {
        if (noteId != -1L) {
            noteViewModel.fetchNoteById(noteId)
        } else {
            noteViewModel.newNoteByFolder(folderId)
        }
    }
    LaunchedEffect(noteState) {
        noteState?.let { note ->
            noteInputState = note.copy()
        }
    }

    fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    LaunchedEffect(key1 = Unit) { // key1 = Unit makes it run once on composition
        noteViewModel.toastMessage.collectLatest { message -> // or .collect {
            showToast(message)
        }
    }

    Scaffold(
        floatingActionButton = {
            RectangleFAB(
                onClick = {
                    if (isEditing) {
                        if (noteInputState.title.isNotEmpty() || noteInputState.content.isNotEmpty()) {
                            noteViewModel.addNote(
                                noteInputState.copy(
                                    id = if (noteId == -1L) null else noteId
                                )
                            )
                            onFinish()
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
            AddEditTopAppBar(
                title = if (noteId == -1L) "Add Note" else "Edit Note",
                onNavigateUp = handleBackPressAttempt,
                showDeleteAction = noteId != -1L,
                onDelete = {
                    showDeleteConfirmationDialog = true
                }
            )
        },

    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = noteInputState.title,
                onValueChange = { noteInputState = noteInputState.copy(title = it) },
                label = { Text("Title") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            FolderDropDown(
                onClick = { folderId ->
                    noteViewModel.fetchFolderById(folderId)
                },
                isEditing = isEditing,
                folder = folder,
                folders = subFolders
            )
            OutlinedTextField(
                value = noteInputState.content,
                onValueChange = {noteInputState = noteInputState.copy( content = it )},
                label = { Text("Description") },
                readOnly = !isEditing,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    ConfirmationDialog(
        showDialog = showDeleteConfirmationDialog,
        onDismiss = { showDeleteConfirmationDialog = false },
        onConfirm = {
            noteState?.let { noteViewModel.deleteNote(it) }
            showDeleteConfirmationDialog = false
            onFinish()
        },
        title = stringResource(R.string.delete_confirmation_title),
        message = stringResource(R.string.delete_item_message)
    )
}
