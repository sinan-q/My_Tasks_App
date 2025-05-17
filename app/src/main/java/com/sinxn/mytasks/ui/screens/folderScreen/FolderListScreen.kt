package com.sinxn.mytasks.ui.screens.folderScreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.AddEditTopAppBar
import com.sinxn.mytasks.ui.components.MyGrid
import com.sinxn.mytasks.ui.components.ShowOptionsFAB
import com.sinxn.mytasks.ui.screens.noteScreen.NoteItem
import com.sinxn.mytasks.ui.screens.taskScreen.TaskItem
import com.sinxn.mytasks.ui.viewModels.FolderViewModel
import kotlinx.coroutines.flow.collectLatest

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
    val context = LocalContext.current
    LaunchedEffect(folderId) {
        folderViewModel.getSubFolders(folderId)
    }
    fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    LaunchedEffect(key1 = Unit) { // key1 = Unit makes it run once on composition
        folderViewModel.toastMessage.collectLatest { message -> // or .collect {
            showToast(message)
        }
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

                AddEditTopAppBar(
                    title = folder.name,
                    onNavigateUp = { if (folder.parentFolderId == 0L) onBack() else folderViewModel.onBack(folder) },
                    showDeleteAction = true,
                    onDelete = {
                        folderViewModel.deleteFolder(folder)
                        folderViewModel.onBack(folder)
                    }
                )

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