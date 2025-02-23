package com.sinxn.mytasks.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.FolderItem
import com.sinxn.mytasks.ui.components.FolderItemEdit
import com.sinxn.mytasks.ui.components.NoteItem
import com.sinxn.mytasks.ui.components.TaskItem
import com.sinxn.mytasks.ui.screens.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onAddNoteClick: (Long?) -> Unit,
    onNoteClick: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    onAddTaskClick: (Long?) -> Unit,
    onTaskClick: (Long?) -> Unit,
) {
    val folders by homeViewModel.folders.collectAsState(initial = emptyList())
    val currentFolder by homeViewModel.folder.collectAsState(initial = Folder(name = "Root", folderId = 0L))
    val tasks by homeViewModel.tasks.collectAsState(initial = emptyList())
    val notes by homeViewModel.notes.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var folderEditToggle by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (showOptions) {
                Surface {
                    LazyColumn {
                        item {
                            ListItem(headlineContent = { Text(text = "Add Task") }, leadingContent = {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "Localized description"
                                )
                            }, modifier = Modifier.clickable { scope.launch {
                                onAddTaskClick(currentFolder?.folderId);
                                showOptions = false; } })
                        }
                        item {
                            ListItem(headlineContent = { Text(text = "Add Folder") }, leadingContent = {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Localized description"
                                )
                            }, modifier = Modifier.clickable { scope.launch { folderEditToggle = true;showOptions = false } })
                        }
                        item {
                            ListItem(headlineContent = { Text(text = "Add Note") }, leadingContent = { Icon(Icons.Filled.Check, contentDescription = "Localized description") },
                                modifier = Modifier.clickable { scope.launch {
                                    showOptions = false;
                                    onAddNoteClick(currentFolder?.folderId)
                                } })
                        }
                    }
                }
            } else FloatingActionButton(onClick = { showOptions = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },

        topBar = {
            currentFolder?.let { folder ->
            if(folder.folderId != 0L) TopAppBar(
                title = { Text(folder.name) },
                navigationIcon = {
                    IconButton(onClick = {  homeViewModel.onBack(folder) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
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
        Column (modifier = Modifier.padding(padding)) {
            if (folderEditToggle) {
                FolderItemEdit(folder = Folder(name = "New Folder", parentFolderId = currentFolder?.folderId), onDismiss = { folderEditToggle = false }) { homeViewModel.addFolder(it) }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
            ) {
                items(folders) { folder ->
                    FolderItem(folder = folder, onClick = { homeViewModel.getSubFolders(folder) }, onDelete = {homeViewModel.deleteFolder(folder)})
                }
            }
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task, onClick = { onTaskClick(task.id) },
                        onUpdate = {}
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
