package com.sinxn.mytasks.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.ui.components.RectangleCard
import com.sinxn.mytasks.ui.screens.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    modifier: Modifier = Modifier,
    noteId: Long = -1L,
    folderId: Long = 0,
    noteViewModel: NoteViewModel,
    onFinish: () -> Unit,
) {

    var noteInputState by remember { mutableStateOf(Note()) }
    var isEditing by remember { mutableStateOf(noteId == -1L) }
    val noteState by noteViewModel.note.collectAsState()
    val folder by noteViewModel.folder.collectAsState()
    val folders by noteViewModel.folders.collectAsState(initial = emptyList())
    var folderChangeExpanded by remember { mutableStateOf(false) }
    // Load existing note if noteId is valid
    LaunchedEffect(noteId, folderId) {
        if (noteId != -1L) {
            noteViewModel.fetchNoteById(noteId)
        } else {
            noteViewModel.fetchFolderById(folderId)
        }
    }
    LaunchedEffect(noteState) {
        noteState?.let { note ->
            noteInputState = noteInputState.copy(
                title = note.title,
                folderId = note.folderId,
                content = note.content,
                timestamp = note.timestamp
            )
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEditing) {
                        if (noteInputState.title.isNotEmpty() || noteInputState.content.isNotEmpty()) {
                            noteViewModel.addNote(
                                Note(
                                    id = if (noteId == -1L) null else noteId,
                                    folderId = noteInputState.folderId,
                                    title = noteInputState.title,
                                    content = noteInputState.content,
                                    timestamp = noteInputState.timestamp
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
            Text(folder?.name?:"Parent", modifier = Modifier.clickable(enabled = isEditing) { folderChangeExpanded = true })
            DropdownMenu(
                expanded = folderChangeExpanded,
                onDismissRequest = { folderChangeExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("...") },
                    onClick = { noteViewModel.fetchFolderById(folder?.parentFolderId?:0) }
                )
                folders.forEach { folder ->
                    DropdownMenuItem(
                        text = { Text(folder.name) },
                        onClick = { noteViewModel.fetchFolderById(folder.folderId)}
                    )
                }

            }
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
