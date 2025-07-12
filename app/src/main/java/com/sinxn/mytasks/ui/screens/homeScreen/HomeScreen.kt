package com.sinxn.mytasks.ui.screens.homeScreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.MyGrid
import com.sinxn.mytasks.ui.components.MyTitle
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
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                MyTitle(modifier = Modifier.clickable {
                    onEventClick()
                }, title = "Upcoming Events")
                if (events.isEmpty()) Text(modifier = Modifier.padding(bottom = 12.dp), text = "Nothing to show here")
            }

            items(events) { event ->
                EventSmallItem(event)
            }
            item {
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
                        onDelete = { homeViewModel.deleteFolder(folder) },
                        onLock = { homeViewModel.lockFolder(folder) })
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

