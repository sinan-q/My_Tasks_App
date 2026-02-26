package com.sinxn.mytasks.ui.features.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.mytasks.R
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.MyTitle
import com.sinxn.mytasks.ui.components.RectangleCard
import com.sinxn.mytasks.ui.components.ShowActionsFAB
import com.sinxn.mytasks.ui.components.ShowOptionsFAB
import com.sinxn.mytasks.ui.features.events.EventListItemUiModel
import com.sinxn.mytasks.ui.features.events.EventSmallItem
import com.sinxn.mytasks.ui.features.folders.FolderItem
import com.sinxn.mytasks.ui.features.folders.FolderItemEdit
import com.sinxn.mytasks.ui.features.folders.FolderListItemUiModel
import com.sinxn.mytasks.ui.features.notes.list.NoteItem
import com.sinxn.mytasks.ui.features.notes.list.NoteListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.TaskItem
import com.sinxn.mytasks.ui.features.tasks.list.TaskListItemUiModel
import com.sinxn.mytasks.ui.navigation.NavRouteHelpers
import com.sinxn.mytasks.ui.navigation.Routes
import com.sinxn.mytasks.ui.navigation.Routes.Backup
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
    var showAllPendingTasks by remember { mutableStateOf(false) }

    val selectionAction by viewModel.selectedAction.collectAsState()
    val selectionCount by viewModel.selectionCount.collectAsState()

    // Observe search state from ViewModel (MVVM pattern)
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    fun showToast(message: String) {
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
            Column(horizontalAlignment = Alignment.End) {
                if (selectionCount != 0) {
                    ShowActionsFAB(
                        folderId = 0L,
                        action = selectionAction,
                        onAction = {
                            viewModel.onAction(it)
                        },

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
                showSearch = true,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
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
                // Search logic is handled in ViewModel (MVVM pattern)
                val isSearching = searchQuery.isNotBlank()

                val folders = state.homeUiModel.folders
                val upcomingEvents = state.homeUiModel.upcomingEvents
                val pendingTasks = state.homeUiModel.pendingTasks
                val tasks = state.homeUiModel.tasks
                val notes = state.homeUiModel.notes
                val pinnedItems = state.homeUiModel.pinnedItems

                LazyVerticalStaggeredGrid(
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = padding,
                    columns = StaggeredGridCells.Fixed(2), //TODO Adaptive
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    // Search Results Section
                    if (isSearching) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                MyTitle(onClick = {}, text = "Search Results (${searchResults.size})")
                                HorizontalDivider()
                                if (searchResults.isEmpty()) {
                                    Text(
                                        text = "No results found for \"$searchQuery\"",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = LocalContentColor.current.copy(alpha =0.4f),
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                        }
                        
                        items(searchResults) { item ->
                            when (item) {
                                is NoteListItemUiModel -> NoteItem(
                                    note = item,
                                    onClick = {
                                        navController.navigate(
                                            NavRouteHelpers.routeFor(
                                                NavRouteHelpers.NoteArgs(noteId = item.id, folderId = 0L)
                                            )
                                        )
                                    },
                                    onHold = { viewModel.onSelectionNote(item.id) },
                                    selected = item.isSelected,
                                    modifier = Modifier.animateItem()
                                )

                                is TaskListItemUiModel -> TaskItem(
                                    task = item,
                                    onClick = {
                                        navController.navigate(
                                            NavRouteHelpers.routeFor(
                                                NavRouteHelpers.TaskArgs(taskId = item.id, folderId = 0L)
                                            )
                                        )
                                    },
                                    onUpdate = { status -> viewModel.updateStatusTask(item.id, status) },
                                    onHold = { viewModel.onSelectionTask(item.id) },
                                    path = null,
                                    selected = item.isSelected,
                                    modifier = Modifier.animateItem()
                                )

                                is EventListItemUiModel -> EventSmallItem(
                                    item,
                                    modifier = Modifier.animateItem()
                                ) {
                                    navController.navigate(
                                        NavRouteHelpers.routeFor(
                                            NavRouteHelpers.EventArgs(eventId = item.id, folderId = 0L, date = -1L)
                                        )
                                    )
                                }

                                is FolderListItemUiModel -> FolderItem(
                                    folder = item,
                                    onClick = {
                                        navController.navigate(
                                            NavRouteHelpers.routeFor(
                                                NavRouteHelpers.FolderArgs(folderId = item.id)
                                            )
                                        )
                                    },
                                    onDelete = { viewModel.deleteFolder(Folder(folderId = item.id, name = item.name, isLocked = item.isLocked)) },
                                    onLock = { viewModel.lockFolder(Folder(folderId = item.id, name = item.name, isLocked = item.isLocked)) },
                                    onHold = { viewModel.onSelectionFolder(item.id) },
                                    selected = item.isSelected,
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                    
                    // Regular sections - only show when NOT searching
                    if (!isSearching) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            MyTitle(onClick = { navController.navigate(Routes.Event.route) }, text = "Upcoming Events")
                            HorizontalDivider()
                            if (upcomingEvents.isEmpty()) Text(
                                text = "Nothing to show here",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = LocalContentColor.current.copy(alpha = 0.4f),
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }

                    items(upcomingEvents, key = { "event_${it.id}" }) { event ->
                        EventSmallItem(event, modifier = Modifier.animateItem()) {
                            navController.navigate(
                                NavRouteHelpers.routeFor(
                                    NavRouteHelpers.EventArgs(eventId = event.id, folderId = 0L, date = -1L)
                                )
                            )
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            MyTitle(onClick = { //TODO
                            }, text = "Pending Tasks")
                            HorizontalDivider()
                            if (pendingTasks.isEmpty()) Text(
                                text = "Nothing to show here",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = LocalContentColor.current.copy(alpha = 0.4f),
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }

                    // Show first 4 pending tasks always
                    items(
                        items = pendingTasks.take(4),
                        key = { task -> "pendingTask_${task.id}" },
                        span = { StaggeredGridItemSpan.FullLine },
                        contentType = { "pendingTask" }
                    ) { task ->
                        TaskItem(
                            task = task,
                            onClick = {
                                navController.navigate(
                                    NavRouteHelpers.routeFor(
                                        NavRouteHelpers.TaskArgs(taskId = task.id, folderId = 0L)
                                    )
                                )
                            },
                            onUpdate = { status -> viewModel.updateStatusTask(task.id, status) },
                            onHold = { viewModel.onSelectionTask(task.id) },
                            path = null,
                            selected = task.isSelected,
                            modifier = Modifier.animateItem()
                        )
                    }

                    // Remaining pending tasks shown with animation
                    if (pendingTasks.size > 4) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                AnimatedVisibility(
                                    visible = showAllPendingTasks,
                                    enter = expandVertically(),
                                    exit = shrinkVertically()
                                ) {
                                    Column {
                                        pendingTasks.drop(4).forEach { task ->
                                            TaskItem(
                                                task = task,
                                                onClick = {
                                                    navController.navigate(
                                                        NavRouteHelpers.routeFor(
                                                            NavRouteHelpers.TaskArgs(taskId = task.id, folderId = 0L)
                                                        )
                                                    )
                                                },
                                                onUpdate = { status -> viewModel.updateStatusTask(task.id, status) },
                                                onHold = { viewModel.onSelectionTask(task.id) },
                                                path = null,
                                                selected = task.isSelected,
                                                modifier = Modifier
                                            )
                                        }
                                    }
                                }
                                RectangleCard(
                                    onClick = { showAllPendingTasks = !showAllPendingTasks },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = if (showAllPendingTasks) "Show Less" else "Show All (${pendingTasks.size})",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            MyTitle(onClick = { //TODO
                            }, text = "Pinned")
                            HorizontalDivider()
                            if (pinnedItems.isEmpty()) {
                                Text(
                                    text = "Nothing to show here",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = LocalContentColor.current.copy(alpha = 0.4f),
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                    items(pinnedItems) { item ->
                        when (item) {
                            is NoteListItemUiModel -> NoteItem(
                                note = item,
                                onClick = {
                                    navController.navigate(
                                        NavRouteHelpers.routeFor(
                                            NavRouteHelpers.NoteArgs(noteId = item.id, folderId = 0L)
                                        )
                                    )
                                },
                                onHold = { viewModel.onSelectionNote(item.id) },
                                selected = item.isSelected,
                                modifier = Modifier.animateItem()
                            )

                            is TaskListItemUiModel -> TaskItem(
                                task = item,
                                onClick = {
                                    navController.navigate(
                                        NavRouteHelpers.routeFor(
                                            NavRouteHelpers.TaskArgs(taskId = item.id, folderId = 0L)
                                        )
                                    )
                                },
                                onUpdate = { status -> viewModel.updateStatusTask(item.id, status) },
                                onHold = { viewModel.onSelectionTask(item.id) },
                                path = null,
                                selected = item.isSelected,
                                modifier = Modifier.animateItem()
                            )

                            is EventListItemUiModel -> EventSmallItem(
                                item,
                                modifier = Modifier.animateItem()
                            ) {
                                navController.navigate(
                                    NavRouteHelpers.routeFor(
                                        NavRouteHelpers.EventArgs(eventId = item.id, folderId = 0L, date = -1L)
                                    )
                                )
                            }

                            is FolderListItemUiModel -> FolderItem(
                                folder = item,
                                onClick = {
                                    navController.navigate(
                                        NavRouteHelpers.routeFor(
                                            NavRouteHelpers.FolderArgs(folderId = item.id)
                                        )
                                    )
                                },
                                onDelete = { viewModel.deleteFolder(Folder(folderId = item.id, name = item.name, isLocked = item.isLocked)) },
                                onLock = { viewModel.lockFolder(Folder(folderId = item.id, name = item.name, isLocked = item.isLocked)) },
                                onHold = { viewModel.onSelectionFolder(item.id) },
                                selected = item.isSelected,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }

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
                    items(folders, key = { "folder_${it.id}" }) { folder ->
                        FolderItem(
                            folder = folder,
                            onClick = {
                                navController.navigate(
                                    NavRouteHelpers.routeFor(
                                        NavRouteHelpers.FolderArgs(folderId = folder.id)
                                    )
                                )
                            },
                            onDelete = { viewModel.deleteFolder(Folder(folderId = folder.id, name = folder.name, isLocked = folder.isLocked)) },
                            onLock = { viewModel.lockFolder(Folder(folderId = folder.id, name = folder.name, isLocked = folder.isLocked)) },
                            onHold = { viewModel.onSelectionFolder(folder.id) },
                            selected = folder.isSelected,
                            modifier = Modifier.animateItem()
                        )
                    }
                    items(
                        key = { "task_${it.id}" },
                        span = { StaggeredGridItemSpan.FullLine },
                        items = tasks,
                    ) { task ->
                        TaskItem(
                            task = task,
                            onClick = {
                                navController.navigate(
                                    NavRouteHelpers.routeFor(
                                        NavRouteHelpers.TaskArgs(taskId = task.id, folderId = 0L)
                                    )
                                )
                            },
                            onUpdate = { status -> viewModel.updateStatusTask(task.id, status) },
                            onHold = { viewModel.onSelectionTask(task.id) },
                            path = null,
                            selected = task.isSelected,
                            modifier = Modifier.animateItem()
                        )
                    }
                    items(notes, key = { "note_${it.id}" }) { note ->
                        NoteItem(
                            note = note,
                            onClick = {
                                navController.navigate(
                                    NavRouteHelpers.routeFor(
                                        NavRouteHelpers.NoteArgs(noteId = note.id, folderId = 0L)
                                    )
                                )
                            },
                            onHold = { viewModel.onSelectionNote(note.id) },
                            selected = note.isSelected,
                            modifier = Modifier.animateItem()
                        )

                    }
                    } // End of if (!isSearching)
                }
                ConfirmationDialog(
                    showDialog = selectionAction == SelectionAction.Delete,
                    onDismiss = {
                        viewModel.onAction(SelectionAction.None)
                    },
                    onConfirm = {
                        viewModel.onAction(SelectionAction.DeleteConfirm(true))
                    },
                    title = stringResource(R.string.delete_confirmation_title),
                    message = "Sure want to delete $selectionCount items?"
                )
            }
        }
    }
}
