package com.sinxn.mytasks.ui.screens.folderScreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.MyGrid
import com.sinxn.mytasks.ui.components.ShowOptionsFAB
import com.sinxn.mytasks.ui.screens.noteScreen.NoteItem
import com.sinxn.mytasks.ui.screens.taskScreen.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderListScreen(
    folderViewModel: FolderViewModel,
    folderId: Long = 0L,
    onAddNoteClick: (Long?) -> Unit,
    onNoteClick: (Long?) -> Unit,
    onAddTaskClick: (Long?) -> Unit,
    onTaskClick: (Long?) -> Unit,
    onBack: () -> Unit,
) {
    LaunchedEffect(folderId) {
        folderViewModel.getSubFolders(folderId)
    }
    val folders by folderViewModel.folders.collectAsState(initial = emptyList())
    val currentFolder by folderViewModel.folder.collectAsState(
        initial = Folder(
            name = "Root",
            folderId = 0L
        )
    )
    val tasks by folderViewModel.tasks.collectAsState(initial = emptyList())
    val notes by folderViewModel.notes.collectAsState(initial = emptyList())
    var folderEditToggle by remember { mutableStateOf(false) }

    BackHandler(
        enabled = currentFolder?.parentFolderId != 0L
    ) {
        folderViewModel.onBack(currentFolder!!)
    }
    Scaffold(
        floatingActionButton = {
            currentFolder?.let {
                ShowOptionsFAB(
                    onAddTaskClick = onAddTaskClick,
                    onAddNoteClick = onAddNoteClick,
                    onAddFolderClick = { folderEditToggle = true },
                    currentFolder = it
                )
            }
        },

        topBar = {
            currentFolder?.let { folder ->
                 TopAppBar(
                    title = { Text(folder.name) },
                    navigationIcon = {
                        IconButton(onClick = { if (folder.parentFolderId == 0L) onBack() else folderViewModel.onBack(folder) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            folderViewModel.onBack(folder)
                            folderViewModel.deleteFolder(folder)
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
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                AnimatedVisibility(
                    visible = folderEditToggle
                ) {
                    FolderItemEdit(
                        folder = Folder(
                            name = "New Folder",
                            parentFolderId = currentFolder?.folderId?: 0L
                        ), onDismiss = { folderEditToggle = false }) { folderViewModel.addFolder(it) }
                }
                MyGrid(
                    list = folders,
                    columns = 2
                ) { folder ->
                    FolderItem(
                        modifier = Modifier.weight(1f),
                        folder = folder,
                        onClick = { folderViewModel.getSubFolders(folder.folderId) },
                        onDelete = { folderViewModel.deleteFolder(folder) },
                        onLock = { folderViewModel.lockFolder(folder) }
                    )
                }
            }
            items(tasks) { task ->
                TaskItem(
                    task = task, onClick = { onTaskClick(task.id) },
                    onUpdate = { status -> folderViewModel.updateTaskStatus(task.id!!, status) },
                    path = null,
                )
            }
            item {
                MyGrid(
                    list = notes,
                    columns = 2
                ) { note ->
                    NoteItem(modifier = Modifier.weight(1f),note = note, onClick = { onNoteClick(note.id) })
                }
            }
        }
    }
}