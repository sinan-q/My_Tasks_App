package com.sinxn.mytasks.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.FolderItem
import com.sinxn.mytasks.ui.components.FolderItemEdit
import com.sinxn.mytasks.ui.components.NoteItem
import com.sinxn.mytasks.ui.components.TaskItem
import com.sinxn.mytasks.ui.screens.viewmodel.HomeViewModel
import com.sinxn.mytasks.ui.screens.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    taskViewModel: TaskViewModel,
    onAddNoteClick: (Long?) -> Unit,
    onNoteClick: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    onAddTaskClick: (Long?) -> Unit,
    onTaskClick: (Long?) -> Unit,
) {
    val folders by homeViewModel.folders.collectAsState(initial = emptyList())
    val currentFolder by homeViewModel.folder.collectAsState(
        initial = Folder(
            name = "Root",
            folderId = 0L
        )
    )
    val tasks by homeViewModel.tasks.collectAsState(initial = emptyList())
    val notes by homeViewModel.notes.collectAsState(initial = emptyList())
    var folderEditToggle by remember { mutableStateOf(false) }

    BackHandler(
        enabled = currentFolder?.folderId != 0L
    ) {
        homeViewModel.onBack(currentFolder!!)
    }
    Scaffold(
        floatingActionButton = {
            ShowOptionsFAB(
                onAddTaskClick = onAddTaskClick,
                onAddNoteClick = onAddNoteClick,
                onAddFolderClick = { folderEditToggle = true },
                currentFolder = currentFolder
            )
        },

        topBar = {
            currentFolder?.let { folder ->
                if (folder.folderId != 0L) TopAppBar(
                    title = { Text(folder.name) },
                    navigationIcon = {
                        IconButton(onClick = { homeViewModel.onBack(folder) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            homeViewModel.onBack(folder)
                            homeViewModel.deleteFolder(folder)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }

                    })
            }
        },
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            if (folderEditToggle) {
                FolderItemEdit(
                    folder = Folder(
                        name = "New Folder",
                        parentFolderId = currentFolder?.folderId
                    ), onDismiss = { folderEditToggle = false }) { homeViewModel.addFolder(it) }
            }
            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = GridCells.Fixed(2),
            ) {
                items(folders) { folder ->
                    FolderItem(
                        folder = folder,
                        onClick = { homeViewModel.getSubFolders(folder) },
                        onDelete = { homeViewModel.deleteFolder(folder) })
                }
            }
            androidx.compose.foundation.lazy.LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task, onClick = { onTaskClick(task.id) },
                        onUpdate = { status -> taskViewModel.updateStatusTask(task.id!!, status) },
                        path = null,
                    )
                }
            }
            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(notes) { note ->
                    NoteItem(note = note, onClick = { onNoteClick(note.id) })
                }
            }
        }

    }
}

@Composable
fun ShowOptionsFAB(
    onAddTaskClick: (Long?) -> Unit = {},
    onAddNoteClick: (Long?) -> Unit = {},
    onAddFolderClick: () -> Unit = {},
    currentFolder: Folder? = null,
) {
    var showOptions by remember { mutableStateOf(false) }
    val isExtended by remember {
        derivedStateOf {
            showOptions
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.End) {
        if (showOptions) {
            ExtendedFloatingActionButton(
                onClick = {
                    showOptions = false
                    onAddTaskClick(currentFolder?.folderId)
                }, icon = {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Add Task"
                    )
                }, text = { Text(text = "Add Task") })
            ExtendedFloatingActionButton(
                onClick = {
                    showOptions = false
                    onAddFolderClick()
                }, icon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Folder"
                    )
                }, text = { Text(text = "Add Folder") })
            ExtendedFloatingActionButton(
                onClick = {
                    showOptions = false
                    onAddNoteClick(currentFolder?.folderId)
                }, icon = {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Add Note"
                    )
                }, text = { Text(text = "Add Note") })
        }
        FloatingActionButton(onClick = { showOptions = !showOptions }) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add"
            )
        }
                    }
}
