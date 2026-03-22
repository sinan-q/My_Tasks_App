package com.sinxn.mytasks.ui.features.tasks.list

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.components.ShowActionsFAB
import com.sinxn.mytasks.ui.navigation.NavRouteHelpers
import kotlinx.coroutines.flow.collectLatest
import showBiometricsAuthentication
import com.sinxn.mytasks.ui.components.taskListItems
import com.sinxn.mytasks.ui.components.SelectionDeleteDialog

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectionAction by viewModel.selectedAction.collectAsState()
    val selectedTasks by viewModel.selectedTasks.collectAsState()
    val selectionCount = viewModel.selectionCount.collectAsState()
    val context = LocalContext.current

    val hideLocked by viewModel.hideLocked.collectAsState()
    val paths by viewModel.paths.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    fun authenticate(function: () -> Unit) {
        showBiometricsAuthentication(
            context,
            onSuccess = function,
            onError = { errString ->
                // Authentication error
                Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }
        )
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (selectionCount.value != 0) {
                    ShowActionsFAB(
                        pasteDisabled = true,
                        action = selectionAction,
                        onAction = {
                            viewModel.onAction(TaskListAction.OnSelectionAction(it))
                        },

                    )
                }
                RectangleFAB(onClick = {
                    navController.navigate(
                        NavRouteHelpers.routeFor(
                            NavRouteHelpers.TaskArgs(taskId = -1L, folderId = 0L)
                        )
                    )
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        },
        topBar = { MyTasksTopAppBar(
            title = { Text("Tasks") },
            actions = {
                IconButton(onClick = { expanded = true }) {
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
                                if (hideLocked) {
                                    authenticate { viewModel.setHideLocked(false) }
                                } else {
                                    viewModel.setHideLocked(true)
                                }
                            },
                        text = (if (hideLocked) "Show" else "Hide") + " Locked Tasks"
                    )
                }
            }
        )},
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        if (uiState.isLoading) {
            Text("Loading...")
        } else if (uiState.errorMessage != null) {
            Text(uiState.errorMessage!!)
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                taskListItems(
                    tasks = uiState.tasks,
                    onTaskClick = {
                        navController.navigate(
                            NavRouteHelpers.routeFor(
                                NavRouteHelpers.TaskArgs(taskId = it, folderId = 0L)
                            )
                        )
                    },
                    onTaskUpdate = { id, status -> viewModel.onAction(TaskListAction.UpdateStatusTask(id, status)) },
                    onTaskHold = { viewModel.onSelectionTask(it) },
                    pathProvider = { paths[it] }
                )
            }
            SelectionDeleteDialog(
                selectionAction = selectionAction,
                selectionCount = selectionCount.value,
                onDismiss = { viewModel.onAction(TaskListAction.OnSelectionAction(SelectionAction.None)) },
                onConfirm = { viewModel.onAction(TaskListAction.OnSelectionAction(SelectionAction.DeleteConfirm(true))) }
            )
        }
    }
}
