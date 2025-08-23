package com.sinxn.mytasks.ui.screens.taskScreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.mytasks.R
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.components.ShowActionsFAB
import com.sinxn.mytasks.ui.navigation.Routes
import com.sinxn.mytasks.ui.viewModels.TaskViewModel
import showBiometricsAuthentication

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    navController: NavController,
) {
    val tasks = viewModel.tasks.collectAsState().value
    val selectionAction by viewModel.selectedAction.collectAsState()
    val selectedTasks by viewModel.selectedTasks.collectAsState()
    val selectionCount = viewModel.selectionCount.collectAsState()
    val toast = viewModel.toastMessage.collectAsState(null)
    val context = LocalContext.current

    var hideLocked by remember { mutableStateOf(true) }
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

    LaunchedEffect(toast) {
        if (toast.value != null) Toast.makeText(context, toast.value, Toast.LENGTH_SHORT).show()
    }
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (selectionCount.value != 0) {
                    ShowActionsFAB(
                        onPaste = { viewModel.showToast("cannot paste here") },
                        action = selectionAction,
                        setActions = {
                            viewModel.setSelectionAction(it)
                        },
                        onClearSelection = {
                            viewModel.clearSelection()
                        }
                    )
                }
                RectangleFAB(onClick = { navController.navigate(Routes.Task.Add.byFolder(0L)) }) {
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
                                    authenticate { hideLocked = false }
                                } else {
                                    hideLocked = true
                                }
                            },
                        text = (if (hideLocked) "Show" else "Hide") + " Locked Notes"
                    )
                }
            }
        )},
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(tasks) { task ->
                var path by remember { mutableStateOf<String?>(null) } // Start with null or a loading state
                var isLoadingPath by remember { mutableStateOf(true) }

                // Launch a coroutine for each item to get its path
                LaunchedEffect(key1 = task.folderId, key2 = hideLocked) {
                    isLoadingPath = true
                    path = viewModel.getPath(task.folderId, hideLocked)
                    isLoadingPath = false
                }

                if (!isLoadingPath) {
                    if (path != null)
                        TaskItem(
                            task = task,
                            path = path,
                            onClick = { navController.navigate(Routes.Task.get(task.id)) },
                            onUpdate = { task.id?.let { it1 -> viewModel.updateStatusTask(it1, it) } },
                            onHold = { viewModel.onSelectionTask(task) },
                            selected = task in selectedTasks
                    )
                }
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
            message = "Sure want to delete ${selectionCount.value} items?"
        )
    }
}
