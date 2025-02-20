package com.sinxn.mytasks.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.FolderItem
import com.sinxn.mytasks.ui.components.FolderItemEdit
import com.sinxn.mytasks.ui.components.NoteItem
import com.sinxn.mytasks.ui.components.TaskItem
import com.sinxn.mytasks.ui.screens.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel
) {
    val folders by homeViewModel.folders.collectAsState(initial = emptyList())
    val currentFolder by homeViewModel.folder.collectAsState(initial = Folder(name = "Root", folderId = 0L))
    val tasks by homeViewModel.tasks.collectAsState(initial = emptyList())
    val notes by homeViewModel.notes.collectAsState(initial = emptyList())
    var folderEditToggle by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { folderEditToggle = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Folder")
            }
        },

        topBar = {
            currentFolder?.let { folder ->
            if(folder.folderId != 0L) TopAppBar(
                title = { Text(folder.name?:"") },
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
        LazyColumn(contentPadding = padding) {
            if (folderEditToggle) item {
                FolderItemEdit(folder = Folder(name = "New Folder", parentFolderId = currentFolder?.folderId), onDismiss = { folderEditToggle = false }) { homeViewModel.addFolder(it) }
            }
            items(folders) { folder ->
                FolderItem(folder = folder, onClick = { homeViewModel.getSubFolders(folder) })
            }
            items(tasks) { task ->
                TaskItem(
                    task = task, onClick = { /* Navigate to folder contents */ },
                    onUpdate = {}
                )
            }
            items(notes) { note ->
                NoteItem(note = note, onClick = { /* Navigate to folder contents */ })
            }
        }
    }
}
