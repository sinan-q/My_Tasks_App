package com.sinxn.mytasks.ui.screens.noteScreen

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.ui.screens.folderScreen.FolderDropDown
import com.sinxn.mytasks.ui.components.RectangleFAB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    modifier: Modifier = Modifier,
    noteId: Long = -1L,
    folderId: Long = 0,
    noteViewModel: NoteViewModel,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current
    var noteInputState by remember { mutableStateOf(Note()) }
    var isEditing by remember { mutableStateOf(noteId == -1L) }
    val noteState by noteViewModel.note.collectAsState()
    val folder by noteViewModel.folder.collectAsState()
    val subFolders by noteViewModel.subFolders.collectAsState()
    val toastMessage by noteViewModel.toastMessage.collectAsState()

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
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context,message,Toast.LENGTH_LONG).show()
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
                            noteViewModel.toast("Note cannot be empty")
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
                title = { Text(if (noteId == -1L) "Add Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (noteId != -1L) IconButton(onClick = {
                        noteState?.let { noteViewModel.deleteNote(it) }
                        onFinish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }

                })
        },
        modifier = Modifier.imePadding()
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
}
