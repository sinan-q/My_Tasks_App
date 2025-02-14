package com.sinxn.mytasks.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.ui.screens.viewmodel.NoteViewModel

@Composable
fun AddEditNoteScreen(
    modifier: Modifier = Modifier,
    noteId: Long = -1L,
    noteViewModel: NoteViewModel,
    onSaveNote: () -> Unit,
    onCancel: () -> Unit,
) {
    val noteState by noteViewModel.note.collectAsState()

    var title by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }

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
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BasicTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom =  8.dp),
        singleLine = true,
        decorationBox = { innerTextField ->
            if (title.text.isEmpty()) {
                Text(text = "Title", color = Color.Gray)
            }
            innerTextField()
        }
        )
        BasicTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            decorationBox = { innerTextField ->
                if (content.text.isEmpty()) {
                    Text(text = "Content", color = Color.Gray)
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onCancel) {
                Text(text = "Cancel")
            }
            Button(
                onClick = {
                    if (title.text.isNotEmpty() && content.text.isNotEmpty()) {
                        noteViewModel.addNote(
                            Note(
                                id = if (noteId == -1L) null else noteId,
                                title = title.text,
                                content = content.text
                            )
                        )
                        onSaveNote()
                    } else {
                        // Handle empty fields, e.g., show a Toast or Snackbar
                    }
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}
