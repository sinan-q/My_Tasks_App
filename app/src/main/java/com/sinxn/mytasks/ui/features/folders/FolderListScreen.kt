package com.sinxn.mytasks.ui.features.folders

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.mytasks.R
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.MyTextField
import com.sinxn.mytasks.ui.components.ShowActionsFAB
import com.sinxn.mytasks.ui.components.ShowOptionsFAB
import com.sinxn.mytasks.ui.features.notes.NoteItem
import com.sinxn.mytasks.ui.features.tasks.TaskItem
import com.sinxn.mytasks.ui.navigation.Routes
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderListScreen(
    folderViewModel: FolderViewModel = hiltViewModel(),
    folderId: Long = 0L,
    navController: NavController,
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for dialog
    var isFolderNameEdit by remember { mutableStateOf(false) }

    val selectedTasks by folderViewModel.selectedTasks.collectAsState()
    val selectedFolders by folderViewModel.selectedFolders.collectAsState()
    val selectedNotes by folderViewModel.selectedNotes.collectAsState()
    val selectionAction by folderViewModel.selectedAction.collectAsState()
    val selectionCount = folderViewModel.selectionCount.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(folderId) {
        folderViewModel.onAction(FolderAction.GetSubFolders(folderId))
    }

    LaunchedEffect(key1 = Unit) {
        folderViewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val uiState by folderViewModel.uiState.collectAsState()

    var folderEditToggle by remember { mutableStateOf(false) }


    when (val state = uiState) {
        is FolderScreenUiState.Loading -> {
            Scaffold(
                bottomBar = { BottomBar(navController = navController) },
                topBar = {
                    MyTasksTopAppBar(
                        title = { Text("Loading...") },
                        onNavigateUp = { navController.popBackStack() },
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        is FolderScreenUiState.Error -> {
            Text(state.message)
        }
        is FolderScreenUiState.Success -> {
            val folders = state.folders
            val currentFolder = state.folder
            val tasks = state.tasks
            val notes = state.notes
            var folderName by remember(currentFolder?.name) { mutableStateOf(currentFolder?.name ?: "") } // Add this line
            BackHandler(
                enabled = currentFolder?.parentFolderId != 0L
            ) {
                if (currentFolder != null) {
                    folderViewModel.onAction(FolderAction.GetSubFolders(currentFolder.parentFolderId ?: 0L))
                }
            }
            Scaffold(
                bottomBar = { BottomBar(navController = navController) },
                floatingActionButton = {
                    Column(horizontalAlignment = Alignment.End) {
                        if (selectionCount.value != 0) {
                            ShowActionsFAB(
                                onPaste = {
                                    folderViewModel.onAction(FolderAction.PasteSelection)
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
                                navController = navController,
                                onAddFolderClick = { folderEditToggle = true },
                                currentFolder = it
                            )
                        }
                    }

                },

                topBar = {
                    currentFolder?.let { folder ->
                        MyTasksTopAppBar(
                            title = {
                                MyTextField(
                                    value = folderName,
                                    onValueChange = { folderName = it },
                                    singleLine = true,
                                    textStyle = TextStyle.Default.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    ),
                                    readOnly = !isFolderNameEdit,
                                    placeholder = "Folder Name",
                                )
                            },
                            onNavigateUp = { if (folder.parentFolderId == 0L) navController.popBackStack() else folderViewModel.onAction(FolderAction.GetSubFolders(folder.parentFolderId ?: 0L)) },
                            actions = {
                                if (isFolderNameEdit) {
                                    IconButton(onClick = {
                                        isFolderNameEdit = false
                                        folderViewModel.onAction(FolderAction.UpdateFolderName(folder.folderId, folderName))
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Save new folder Name"
                                        )
                                    }
                                    IconButton(onClick = {
                                        folderName = folder.name
                                        isFolderNameEdit = false
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Cancel Folder Name change"
                                        )
                                    }
                                } else {
                                    IconButton(onClick = {
                                        folderName = folder.name
                                        isFolderNameEdit = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Folder Name"
                                        )
                                    }
                                    IconButton(onClick = {
                                        showDeleteConfirmationDialog = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete"
                                        )
                                    }
                                }



                            }
                        )

                    }
                },
            ) { padding ->
                LazyVerticalStaggeredGrid(
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = padding,
                    columns = StaggeredGridCells.Fixed(2), //TODO Adaptive
                    modifier = Modifier.padding(horizontal = 16.dp),
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
                            ) { folderViewModel.onAction(FolderAction.AddFolder(it)) }
                        }
                    }
                    items(folders, key = { "folder_${it.folderId}" }) { folder ->
                        FolderItem(
                            folder = folder,
                            onClick = { folderViewModel.onAction(FolderAction.GetSubFolders(folder.folderId)) },
                            onDelete = { folderViewModel.onAction(FolderAction.DeleteFolder(folder)) },
                            onLock = { folderViewModel.onAction(FolderAction.LockFolder(folder)) },
                            onHold = { folderViewModel.onSelectionFolder(folder) },
                            selected = folder in selectedFolders,
                            modifier = Modifier.animateItem()
                        )
                    }
                    items(
                        items = tasks,
                        key = { "task_${it.id!!}" },
                        span = { StaggeredGridItemSpan.FullLine },
                    ) { task ->
                        TaskItem(
                            task = task,
                            onClick = { navController.navigate(Routes.Task.get(task.id)) },
                            onHold = { folderViewModel.onSelectionTask(task) },
                            onUpdate = { status -> folderViewModel.onAction(FolderAction.UpdateTaskStatus(task.id!!, status)) },
                            path = null,
                            selected = task in selectedTasks,
                            modifier = Modifier.animateItem()
                        )
                    }
                    items(notes, key = { "note_${it.id!!}" }) { note ->
                        NoteItem(
                            note = note,
                            onClick = { navController.navigate(Routes.Note.get(note.id)) },
                            onHold = { folderViewModel.onSelectionNote(note) },
                            selected = note in selectedNotes,
                            modifier = Modifier.animateItem()
                        )
                    }
                }

            }
            currentFolder?.let {
                ConfirmationDialog(
                    showDialog = showDeleteConfirmationDialog,
                    onDismiss = { showDeleteConfirmationDialog = false },
                    onConfirm = {
                        folderViewModel.onAction(FolderAction.DeleteFolder(it))
                        folderViewModel.onAction(FolderAction.GetSubFolders(it.parentFolderId ?: 0L))
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
    }
}
