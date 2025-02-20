package com.sinxn.mytasks.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sinxn.mytasks.ui.components.FolderItem
import com.sinxn.mytasks.ui.components.NoteItem
import com.sinxn.mytasks.ui.components.TaskItem
import com.sinxn.mytasks.ui.screens.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel
) {
    val folders by homeViewModel.folders.collectAsState(initial = emptyList())
    val tasks by homeViewModel.tasks.collectAsState(initial = emptyList())
    val notes by homeViewModel.notes.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Handle new folder creation */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Folder")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(folders) { folder ->
                FolderItem(folder = folder, onClick = { /* Navigate to folder contents */ })
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
