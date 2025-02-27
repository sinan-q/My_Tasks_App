package com.sinxn.mytasks.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.sinxn.mytasks.ui.components.MyTitle
import com.sinxn.mytasks.ui.components.NoteItem
import com.sinxn.mytasks.ui.components.ShowOptionsFAB
import com.sinxn.mytasks.ui.components.TaskItem
import com.sinxn.mytasks.ui.screens.viewmodel.HomeViewModel
import com.sinxn.mytasks.ui.screens.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderListScreen(
    homeViewModel: HomeViewModel,
    taskViewModel: TaskViewModel,
    onAddNoteClick: (Long?) -> Unit,
    onNoteClick: (Long?) -> Unit,
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
                 TopAppBar(
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

            MyTitle(title = "Root")
            AnimatedVisibility(
                visible = folderEditToggle
            ) {
                FolderItemEdit(
                    folder = Folder(
                        name = "New Folder",
                        parentFolderId = currentFolder?.folderId
                    ), onDismiss = { folderEditToggle = false }) { homeViewModel.addFolder(it) }

            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
            ) {
                items(folders) { folder ->
                    FolderItem(
                        folder = folder,
                        onClick = { homeViewModel.getSubFolders(folder) },
                        onDelete = { homeViewModel.deleteFolder(folder) })
                }
            }
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task, onClick = { onTaskClick(task.id) },
                        onUpdate = { status -> taskViewModel.updateStatusTask(task.id!!, status) },
                        path = null,
                    )
                }
            }
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(notes) { note ->
                    NoteItem(note = note, onClick = { onNoteClick(note.id) })
                }
            }
        }

    }
}