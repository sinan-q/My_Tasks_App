package com.sinxn.mytasks.ui.screens.folderScreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinxn.mytasks.R
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.ui.components.AddEditTopAppBar
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.ShowActionsFAB
import com.sinxn.mytasks.ui.components.ShowOptionsFAB
import com.sinxn.mytasks.ui.screens.noteScreen.NoteItem
import com.sinxn.mytasks.ui.screens.taskScreen.TaskItem
import com.sinxn.mytasks.ui.viewModels.FolderViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FolderListScreen(
    folderViewModel: FolderViewModel = hiltViewModel(),
    folderId: Long = 0L,
    onAddNoteClick: (Long?) -> Unit,
    onNoteClick: (Long?) -> Unit,
    onAddTaskClick: (Long?) -> Unit,
    onTaskClick: (Long?) -> Unit,
    onBack: () -> Unit,
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for dialog

    val selectedTasks by folderViewModel.selectedTasks.collectAsState()
    val selectedFolders by folderViewModel.selectedFolders.collectAsState()
    val selectedNotes by folderViewModel.selectedNotes.collectAsState()
    val selectionAction by folderViewModel.selectedAction.collectAsState()
    val selectionCount = folderViewModel.selectionCount.collectAsState()

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
            Column(horizontalAlignment = Alignment.End) {
                if (selectionCount.value != 0) {
                    ShowActionsFAB(
                        onPaste = {
                            folderViewModel.pasteSelection()
                        },
                        action = selectionAction,
                        setActions = {
                            folderViewModel.setSelectionAction(it)
                        },
                        onClearSelection = {
                            folderViewModel.clearSelection()
                        }
                    )
                }
                currentFolder?.let {
                    ShowOptionsFAB(
                        onAddTaskClick = onAddTaskClick,
                        onAddNoteClick = onAddNoteClick,
                        onAddFolderClick = { folderEditToggle = true },
                        currentFolder = it
                    )
                }
            }

        },

        topBar = {
            currentFolder?.let { folder ->

                AddEditTopAppBar(
                    title = folder.name,
                    onNavigateUp = { if (folder.parentFolderId == 0L) onBack() else folderViewModel.onBack(folder) },
                    showDeleteAction = true,
                    onDelete = {
                        showDeleteConfirmationDialog = true
                    }
                )

            }
        },
    ) { padding ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding(padding),
            columns = StaggeredGridCells.Fixed(2), //TODO Adaptive
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                AnimatedVisibility(
                    visible = folderEditToggle
                ) {
                    FolderItemEdit(
                        folder = Folder(
                            name = "New Folder",
                            parentFolderId = currentFolder?.folderId?: 0L
                        ),
                        onDismiss = { folderEditToggle = false }
                    ) { folderViewModel.addFolder(it) }
                }
            }
            items(folders) { folder ->
                FolderItem(
                    folder = folder,
                    onClick = { folderViewModel.getSubFolders(folder.folderId) },
                    onDelete = { folderViewModel.deleteFolder(folder) },
                    onLock = { folderViewModel.lockFolder(folder) },
                    onHold = { folderViewModel.onSelectionFolder(folder) },
                    selected = folder in selectedFolders
                )
            }
            items (
                key = { it.id!! },
                span = { StaggeredGridItemSpan.FullLine },
                items = tasks,
            ) { task ->
                TaskItem(
                    task = task,
                    onClick = { onTaskClick(task.id) },
                    onHold = { folderViewModel.onSelectionTask(task) },
                    onUpdate = { status -> folderViewModel.updateTaskStatus(task.id!!, status) },
                    path = null,
                    selected = task in selectedTasks
                )
            }
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onClick = { onNoteClick(note.id) },
                    onHold = { folderViewModel.onSelectionNote(note) },
                    selected = note in selectedNotes
                )
            }
        }

    }
    currentFolder?.let { folder ->
        ConfirmationDialog(
            showDialog = showDeleteConfirmationDialog,
            onDismiss = { showDeleteConfirmationDialog = false },
            onConfirm = {
                folderViewModel.deleteFolder(folder)
                folderViewModel.onBack(folder) 
                showDeleteConfirmationDialog = false
            },
            title = stringResource(R.string.delete_confirmation_title),
            message = stringResource(R.string.delete_folder_message)
        )
    }
    ConfirmationDialog(
        showDialog = selectionAction == SelectionActions.DELETE,
        onDismiss = {
            folderViewModel.setSelectionAction(SelectionActions.NONE)
        },
        onConfirm = {
            folderViewModel.deleteSelection()
        },
        title = stringResource(R.string.delete_confirmation_title),
        message = "Sure want to delete ${selectionCount.value} items?" //TODO
    )

}