package com.sinxn.mytasks.ui.screens.noteScreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.ui.components.RectangleFAB
import showBiometricsAuthentication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    noteViewModel: NoteViewModel,
    onAddNoteClick: (folder: Long?) -> Unit,
    onNoteClick: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    fun authenticate(function: () -> Unit) {

        showBiometricsAuthentication(
            context,
            onSuccess = function,
            onError = { errString ->
                // Authentication error
                Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }
        )
    }
    var hideLocked by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            RectangleFAB(onClick = { onAddNoteClick(0) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(
                        onClick = { expanded = true }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More Options",
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).clickable {
                                expanded = false
                                if (hideLocked) authenticate({ hideLocked = false })
                                else hideLocked = true

                            },
                            text = (if (hideLocked) "Show" else "Hide") + " Locked Notes"
                        )
                    }
                },
                title = { Text("My Tasks") }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyVerticalStaggeredGrid (
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = paddingValues,
            modifier = Modifier
        ) {
            items(notes) { note ->
                val path = noteViewModel.getPath(note.folderId, hideLocked)
                if (path != null) {
                    NoteItem(
                        note = note,
                        path = path,
                        onClick = { onNoteClick(note.id) }
                    )
                }
            }
        }
    }
}