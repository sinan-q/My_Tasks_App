package com.sinxn.mytasks.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.EventSmallItem
import com.sinxn.mytasks.ui.components.FolderItem
import com.sinxn.mytasks.ui.components.FolderItemEdit
import com.sinxn.mytasks.ui.components.MyGrid
import com.sinxn.mytasks.ui.components.MyTitle
import com.sinxn.mytasks.ui.components.NoteItem
import com.sinxn.mytasks.ui.components.ShowOptionsFAB
import com.sinxn.mytasks.ui.components.TaskItem
import com.sinxn.mytasks.ui.screens.viewmodel.HomeViewModel
import com.sinxn.mytasks.ui.screens.viewmodel.TaskViewModel

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
) {
    val folders by homeViewModel.folders.collectAsState(initial = emptyList())
    val events by homeViewModel.events.collectAsState(initial = emptyList())

    val tasks by homeViewModel.tasks.collectAsState(initial = emptyList())
    val notes by homeViewModel.notes.collectAsState(initial = emptyList())
    var folderEditToggle by remember { mutableStateOf(false) }


    Scaffold(
        floatingActionButton = {
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
        },

        topBar = {

        },
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                MyTitle(modifier = Modifier.clickable {
                    onEventClick()
                }, title = "Upcoming Events")
            }
            items(events) { event ->
                EventSmallItem(event)
            }
            item {
                MyTitle(title = "Root")
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
                MyGrid(
                    list = folders,
                    columns = 2
                ) { folder ->
                    FolderItem(
                        modifier = Modifier.weight(1f),
                        folder = folder,
                        onClick = { onFolderClick(folder.folderId) },
                        onDelete = { homeViewModel.deleteFolder(folder) })
                }
            }
            items(tasks) { task ->
                TaskItem(
                    task = task, onClick = { onTaskClick(task.id) },
                    onUpdate = { status -> taskViewModel.updateStatusTask(task.id!!, status) },
                    path = null,
                )
            }
            item {
                MyGrid(
                    list = notes,
                    columns = 2
                ) { note ->
                    NoteItem(
                        modifier = Modifier.weight(1f),
                        note = note,
                        onClick = { onNoteClick(note.id) }
                    )
                }
            }
        }
    }
}

