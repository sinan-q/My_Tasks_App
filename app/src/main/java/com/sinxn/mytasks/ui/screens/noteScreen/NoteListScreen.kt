package com.sinxn.mytasks.ui.screens.noteScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sinxn.mytasks.R
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.store.SelectionActions
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.ListTopAppBar
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.components.ShowActionsFAB
import com.sinxn.mytasks.ui.viewModels.NoteViewModel

@Composable
fun NoteListScreen(
    notes: List<Note>,
    noteViewModel: NoteViewModel,
    onAddNoteClick: (folder: Long?) -> Unit,
    onNoteClick: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {

    val selectionAction by noteViewModel.selectedAction.collectAsState()
    val selectedNotes by noteViewModel.selectedNotes.collectAsState()
    val selectionCount = noteViewModel.selectionCount.collectAsState()

    var hideLocked by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (selectionCount.value != 0) {
                    ShowActionsFAB(
                        onPaste = {},
                        action = selectionAction,
                        setActions = {
                            noteViewModel.setSelectionAction(it)
                        },
                        onClearSelection = {
                            noteViewModel.clearSelection()
                        }
                    )
                }
                RectangleFAB(onClick = { onAddNoteClick(0) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        },
        topBar = {
            ListTopAppBar(
                expanded = expanded,
                setExpanded = { expanded = it},
                hideLocked = hideLocked,
                setHideLocked = { hideLocked = it }
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
                        onClick = { onNoteClick(note.id) },
                        onHold = { noteViewModel.onSelectionNote(note) },
                        selected = note in selectedNotes,
                    )
                }
            }
        }
        ConfirmationDialog(
            showDialog = selectionAction == SelectionActions.DELETE,
            onDismiss = {
                noteViewModel.setSelectionAction(SelectionActions.NONE)
            },
            onConfirm = {
                noteViewModel.deleteSelection()
            },
            title = stringResource(R.string.delete_confirmation_title),
            message = "Sure want to delete ${selectionCount.value} items?"
        )
    }
}