package com.sinxn.mytasks.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.ui.screens.viewmodel.NoteViewModel
import java.util.Date

@Composable
fun AddEditNoteScreen(
    modifier: Modifier = Modifier,
    noteId: Long = -1L,
    noteViewModel: NoteViewModel,
    onFinish: () -> Unit,
) {
    val noteState by noteViewModel.note.collectAsState()

    var title by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }
    var timestamp by remember { mutableStateOf<Date?>(null) }

    // Load existing note if noteId is valid
    LaunchedEffect(noteId) {
        if (noteId != -1L) {
            noteViewModel.fetchNoteById(noteId)
        }
    }

    LaunchedEffect(noteState) {
        noteState?.let { note ->
            title = TextFieldValue(note.title)
            content = TextFieldValue(note.content)
            timestamp = note.timestamp
        }
    }
    Scaffold(
        floatingActionButton = {
        FloatingActionButton(
            onClick = {
                if (title.text.isNotEmpty() && content.text.isNotEmpty()) {
                    noteViewModel.addNote(
                        Note(
                            id = if (noteId == -1L) null else noteId,
                            title = title.text,
                            content = content.text,
                            timestamp = timestamp?: Date()
                        )
                    )
                    onFinish()
                } else {
                    // Handle empty fields, e.g., show a Toast or Snackbar
                }
            }
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
        }
    }){
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (noteState != null) {
                    noteViewModel.deleteNote(noteState!!)
                    onFinish()
                }
            }) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    }





}
