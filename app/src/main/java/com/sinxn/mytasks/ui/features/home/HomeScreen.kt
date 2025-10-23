package com.sinxn.mytasks.ui.features.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.mytasks.R
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.MyTitle
import com.sinxn.mytasks.ui.components.ShowActionsFAB
import com.sinxn.mytasks.ui.components.ShowOptionsFAB
import com.sinxn.mytasks.ui.features.events.EventSmallItem
import com.sinxn.mytasks.ui.features.folders.FolderItem
import com.sinxn.mytasks.ui.features.folders.FolderItemEdit
import com.sinxn.mytasks.ui.features.notes.NoteItem
import com.sinxn.mytasks.ui.features.tasks.TaskItem
import com.sinxn.mytasks.ui.navigation.Routes
import com.sinxn.mytasks.ui.navigation.Routes.Backup
import com.sinxn.mytasks.ui.navigation.Routes.Event
import com.sinxn.mytasks.ui.navigation.Routes.Note
import com.sinxn.mytasks.ui.navigation.Routes.Task
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,

) {
    val context = LocalContext.current
    var folderEditToggle by remember { mutableStateOf(false) }

    val selectedTasks by viewModel.selectedTasks.collectAsState()
    val selectedNotes by viewModel.selectedNotes.collectAsState()
    val selectedFolders by viewModel.selectedFolders.collectAsState()
    val selectionAction by viewModel.selectedAction.collectAsState()
    val selectionCount by viewModel.selectionCount.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    fun showToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.toastMessage.collectLatest { message ->
            showToast(message)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End){
                if (selectionCount != 0) {
                    ShowActionsFAB(
                        onPaste = {
                            viewModel.pasteSelection()
                        },
                        action = selectionAction,
                        setActions = {
                            viewModel.setSelectionAction(it)
                        },
                        onClearSelection = {
                            viewModel.clearSelection()
                        }
                    )
                }
                ShowOptionsFAB(
                    navController = navController,
                    onAddFolderClick = { folderEditToggle = true },
                    currentFolder = Folder(
                        folderId = 0L,
                        name = "Root"
                    ),
                )
            }

        },

        topBar = {
            MyTasksTopAppBar(
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
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .clickable {
                                    expanded = false
                                    navController.navigate(Backup.route)
                                },
                            text = "Backup"
                        )
                    }
                },
                title = { Text("My Tasks") }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        when (val state = uiState) {
            is HomeScreenUiState.Loading -> {
                Text("Loading...")
            }
            is HomeScreenUiState.Error -> {
                Text(state.message)
            }
            is HomeScreenUiState.Success -> {
                val folders = state.folders
                val upcomingEvents = state.upcomingEvents
                val pendingTasks = state.pendingTasks
                val tasks = state.tasks
                val notes = state.notes

                LazyVerticalStaggeredGrid(
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = padding,
                    columns = StaggeredGridCells.Fixed(2), //TODO Adaptive
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            MyTitle(onClick = { navController.navigate(Event.route) }, text = "Upcoming Events")
                            HorizontalDivider()
                            if (upcomingEvents.isEmpty()) Text(text = "Nothing to show here", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = LocalContentColor.current.copy(alpha = 0.4f), fontStyle = FontStyle.Italic)
                        }
                    }

                    items(upcomingEvents, key = { "event_${it.id!!}" }) { event ->
                        EventSmallItem(event, modifier = Modifier.animateItem()) {
                            navController.navigate(Event.get(event.id))
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            MyTitle(onClick = { //TODO
                            }, text = "Pending Tasks")
                            HorizontalDivider()
                            // TODO: Animated items if possible
                            if (pendingTasks.isEmpty()) Text(text = "Nothing to show here", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = LocalContentColor.current.copy(alpha = 0.4f), fontStyle = FontStyle.Italic)
                        }
                    }

                    items (
                        items = pendingTasks,
                        key = { task -> "pendingTask_${task.id!!}" },
                        span = { StaggeredGridItemSpan.FullLine },
                        contentType = { "pendingTask" }
                    ) { task ->
                        TaskItem(
                            task = task,
                            onClick = { navController.navigate(Task.get(task.id)) },
                            onUpdate = { status -> viewModel.updateStatusTask(task.id!!, status) },
                            onHold = { viewModel.onSelectionTask(task) },
                            path = null,
                            selected = task in selectedTasks,
                            modifier = Modifier.animateItem()
                        )
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            MyTitle(onClick = { //TODO
                            }, text = "Favourites")
                            HorizontalDivider()
                            Text(text = "Nothing to show here", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = LocalContentColor.current.copy(alpha = 0.4f), fontStyle = FontStyle.Italic)
                        }
                    }
                    //TODO Favourites
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            MyTitle(text = "Home")
                            HorizontalDivider()
                            if (folders.isEmpty() && tasks.isEmpty() && notes.isEmpty()) Text(text = "Nothing to show here")
                        }
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        AnimatedVisibility(
                            visible = folderEditToggle
                        ) {
                            FolderItemEdit(
                                folder = Folder(
                                    name = "New Folder",
                                ),
                                onDismiss = { folderEditToggle = false }
                            ) { viewModel.addFolder(it) }
                        }
                    }
                    items(folders, key = { "folder_${it.folderId}" }) { folder ->
                        FolderItem(
                            folder = folder,
                            onClick = { navController.navigate(Routes.Folder.get(folder.folderId)) },
                            onDelete = { viewModel.deleteFolder(folder) },
                            onLock = { viewModel.lockFolder(folder) },
                            onHold = { viewModel.onSelectionFolder(folder) },
                            selected = folder in selectedFolders,
                            modifier = Modifier.animateItem()
                        )
                    }
                    items (
                        key = { "task_${it.id!!}" },
                        span = { StaggeredGridItemSpan.FullLine },
                        items = tasks,
                    ) { task ->
                        TaskItem(
                            task = task,
                            onClick = { navController.navigate(Task.get(task.id))},
                            onUpdate = { status -> viewModel.updateStatusTask(task.id!!, status) },
                            onHold = { viewModel.onSelectionTask(task) },
                            path = null,
                            selected = task in selectedTasks,
                            modifier = Modifier.animateItem()
                        )
                    }
                    items(notes, key = { "note_${it.id!!}" }) { note ->
                        NoteItem(
                            note = note,
                            onClick = { navController.navigate(Note.get(note.id)) },
                            onHold = { viewModel.onSelectionNote(note) },
                            selected = note in selectedNotes,
                            modifier = Modifier.animateItem()
                        )

                    }
                }
                ConfirmationDialog(
                    showDialog = selectionAction == SelectionActions.DELETE,
                    onDismiss = {
                        viewModel.setSelectionAction(SelectionActions.NONE)
                    },
                    onConfirm = {
                        viewModel.deleteSelection()
                    },
                    title = stringResource(R.string.delete_confirmation_title),
                    message = "Sure want to delete $selectionCount items?"
                )
            }
        }
    }
}
