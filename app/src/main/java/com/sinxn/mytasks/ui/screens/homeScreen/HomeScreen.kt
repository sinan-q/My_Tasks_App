package com.sinxn.mytasks.ui.screens.homeScreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinxn.mytasks.R
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.MyTitle
import com.sinxn.mytasks.ui.components.ShowActionsFAB
import com.sinxn.mytasks.ui.components.ShowOptionsFAB
import com.sinxn.mytasks.ui.screens.eventScreen.EventSmallItem
import com.sinxn.mytasks.ui.screens.folderScreen.FolderItem
import com.sinxn.mytasks.ui.screens.folderScreen.FolderItemEdit
import com.sinxn.mytasks.ui.screens.noteScreen.NoteItem
import com.sinxn.mytasks.ui.screens.taskScreen.TaskItem
import com.sinxn.mytasks.ui.viewModels.HomeViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddNoteClick: (Long?) -> Unit,
    onNoteClick: (Long?) -> Unit,
    onAddTaskClick: (Long?) -> Unit,
    onTaskClick: (Long?) -> Unit,
    onAddEventClick: (Long?) -> Unit, //TODO use navController
    onEventClick: () -> Unit,
    onFolderClick: (Long) -> Unit,
    onBackup: () -> Unit
) {
    val context = LocalContext.current
    val folders by viewModel.mainFolders.collectAsState(initial = emptyList())
    val events by viewModel.events.collectAsState(initial = emptyList())

    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    val notes by viewModel.notes.collectAsState(initial = emptyList())
    var folderEditToggle by remember { mutableStateOf(false) }

    val selectedTasks by viewModel.selectedTasks.collectAsState()
    val selectedNotes by viewModel.selectedNotes.collectAsState()
    val selectedFolders by viewModel.selectedFolders.collectAsState()
    val selectionAction by viewModel.selectedAction.collectAsState()
    val selectionCount = viewModel.selectionCount.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.toastMessage.collectLatest { message ->
            showToast(message)
        }
    }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End){
                if (selectionCount.value != 0) {
                    ShowActionsFAB(
                        onPaste = {
                            viewModel.pasteSelection()
                        },
                        action = selectionAction,
                        setActions = {
                            viewModel.setSelectionAction(it)
                        },
                        onClearSelection = {
                            viewModel.clearSelection()
                        }
                    )
                }
                ShowOptionsFAB(
                    onAddTaskClick = onAddTaskClick,
                    onAddNoteClick = onAddNoteClick,
                    onAddEventClick = onAddEventClick,
                    onAddFolderClick = { folderEditToggle = true },
                    currentFolder = Folder(
                        folderId = 0L,
                        name = "Root"
                    ),
                )
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
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .clickable {
                                    expanded = false
                                    onBackup()
                                },
                            text = "Backup"
                        ) 
                    }
                },
                title = { Text("My Tasks") }
            )
        },
    ) { padding ->
        LazyVerticalStaggeredGrid(
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(padding),
            columns = StaggeredGridCells.Fixed(2) //TODO Adaptive
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MyTitle(onClick = { onEventClick() }, text = "Upcoming Events")
                    HorizontalDivider()
                    if (events.isEmpty()) Text(text = "Nothing to show here", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = LocalContentColor.current.copy(alpha = 0.4f), fontStyle = FontStyle.Italic)
                }
            }

            items(events) { event ->
                EventSmallItem(event)
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MyTitle(onClick = { //TODO
                         }, text = "Favourites")
                    HorizontalDivider()
                    Text(text = "Nothing to show here", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = LocalContentColor.current.copy(alpha = 0.4f), fontStyle = FontStyle.Italic)
                }
            }
            //TODO Favourites
            item(span = StaggeredGridItemSpan.FullLine) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MyTitle(text = "Home")
                    HorizontalDivider()
                    if (folders.isEmpty() && tasks.isEmpty() && notes.isEmpty()) Text(text = "Nothing to show here")
                }
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                AnimatedVisibility(
                    visible = folderEditToggle
                ) {
                    FolderItemEdit(
                        folder = Folder(
                            name = "New Folder",
                        ),
                        onDismiss = { folderEditToggle = false }
                    ) { viewModel.addFolder(it) }
                }
            }
            items(folders) { folder ->
                FolderItem(
                    folder = folder,
                    onClick = { onFolderClick(folder.folderId)},
                    onDelete = { viewModel.deleteFolder(folder) },
                    onLock = { viewModel.lockFolder(folder) },
                    onHold = { viewModel.onSelectionFolder(folder) },
                    selected = folder in selectedFolders
                )
            }
            items (
                key = { it.id!! },
                span = { StaggeredGridItemSpan.FullLine },
                items = tasks,
            ) { task ->
                TaskItem(
                    task = task, onClick = { onTaskClick(task.id)},
                    onUpdate = { status -> viewModel.updateStatusTask(task.id!!, status) },
                    onHold = { viewModel.onSelectionTask(task) },
                    path = null,
                    selected = task in selectedTasks
                )
            }
            items(notes) { note ->
                    NoteItem(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        onHold = { viewModel.onSelectionNote(note) },
                        selected = note in selectedNotes
                    )

            }
        }
        ConfirmationDialog(
            showDialog = selectionAction == SelectionActions.DELETE,
            onDismiss = {
                viewModel.setSelectionAction(SelectionActions.NONE)
            },
            onConfirm = {
                viewModel.deleteSelection()
            },
            title = stringResource(R.string.delete_confirmation_title),
            message = "Sure want to delete ${selectionCount.value} items?"
        )
    }
}

