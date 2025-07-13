package com.sinxn.mytasks.ui.screens.homeScreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.R
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.store.SelectionActions
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
import com.sinxn.mytasks.ui.viewModels.TaskViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    taskViewModel: TaskViewModel,
    onAddNoteClick: (Long?) -> Unit,
    onNoteClick: (Long?) -> Unit,
    onAddTaskClick: (Long?) -> Unit,
    onTaskClick: (Long?) -> Unit,
    onAddEventClick: (Long?) -> Unit,
    onEventClick: () -> Unit,
    onFolderClick: (Long) -> Unit,
    onBackup: () -> Unit
) {
    val context = LocalContext.current
    val folders by homeViewModel.mainFolders.collectAsState(initial = emptyList())
    val events by homeViewModel.events.collectAsState(initial = emptyList())

    val tasks by homeViewModel.tasks.collectAsState(initial = emptyList())
    val notes by homeViewModel.notes.collectAsState(initial = emptyList())
    var folderEditToggle by remember { mutableStateOf(false) }

    val selectedTasks by homeViewModel.selectedTasks.collectAsState()
    val selectionAction by homeViewModel.selectedAction.collectAsState()


    var expanded by remember { mutableStateOf(false) }
    fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    LaunchedEffect(key1 = Unit) {
        homeViewModel.toastMessage.collectLatest { message ->
            showToast(message)
        }
    }

    Scaffold(
        floatingActionButton = {
            Column {
                if (selectedTasks.isNotEmpty()) {
                    ShowActionsFAB(
                        onPaste = {
                            homeViewModel.pasteSelection()
                        },
                        action = selectionAction,
                        setActions = {
                            homeViewModel.setSelectionAction(it)
                        },
                        onClearSelection = {
                            homeViewModel.clearSelection()
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
            modifier = Modifier.padding(padding),
            columns = StaggeredGridCells.Fixed(2) //TODO Adaptive
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MyTitle(onClick = { onEventClick() }, text = "Upcoming Events")
                    if (events.isEmpty()) Text(text = "Nothing to show here")
                }
            }

            items(events) { event ->
                EventSmallItem(event)
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MyTitle(text = "Home")
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
                    ) { homeViewModel.addFolder(it) }
                }
            }
            items(folders) { folder ->
                FolderItem(
                    folder = folder,
                    onClick = { onFolderClick(folder.folderId) },
                    onDelete = { homeViewModel.deleteFolder(folder) },
                    onLock = { homeViewModel.lockFolder(folder) })
            }
            items (
                key = { it.id!! },
                span = { StaggeredGridItemSpan.FullLine },
                items = tasks,
            ) { task ->
                TaskItem(
                    task = task, onClick = { if (selectedTasks.isEmpty()) onTaskClick(task.id) else if (selectionAction != SelectionActions.COPY && selectionAction != SelectionActions.CUT) homeViewModel.onSelectionTask(task) },
                    onUpdate = { status -> taskViewModel.updateStatusTask(task.id!!, status) },
                    onHold = {
                        if (selectionAction != SelectionActions.COPY && selectionAction != SelectionActions.CUT) homeViewModel.onSelectionTask(task)
                    },
                    path = null,
                    selected = task in selectedTasks
                )
            }
            items(notes) { note ->
                    NoteItem(
                        note = note,
                        onClick = { onNoteClick(note.id) }
                    )

            }
        }
        ConfirmationDialog(
            showDialog = selectionAction == SelectionActions.DELETE,
            onDismiss = { homeViewModel.clearSelection() },
            onConfirm = {
                homeViewModel.deleteTasks()
            },
            title = stringResource(R.string.delete_confirmation_title),
            message = "" //TODO
        )
    }
}

