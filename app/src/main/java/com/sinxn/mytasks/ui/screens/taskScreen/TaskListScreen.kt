package com.sinxn.mytasks.ui.screens.taskScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinxn.mytasks.R
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.ListTopAppBar
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.components.ShowActionsFAB
import com.sinxn.mytasks.ui.viewModels.TaskViewModel

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onAddTaskClick: (Long?) -> Unit,
    onTaskClick: (Long?) -> Unit,
) {
    val tasks = viewModel.tasks.collectAsState().value
    val selectionAction by viewModel.selectedAction.collectAsState()
    val selectedTasks by viewModel.selectedTasks.collectAsState()
    val selectionCount = viewModel.selectionCount.collectAsState()
    val toast = viewModel.toastMessage.collectAsState(null)
    val context = LocalContext.current

    var hideLocked by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(toast) {
        Toast.makeText(context, toast.value, Toast.LENGTH_SHORT).show()
    }
    Scaffold(
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
                RectangleFAB(onClick = { onAddTaskClick(0L) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        },
        topBar = {
            ListTopAppBar(
                expanded = expanded,
                setExpanded = { expanded = it},
                hideLocked = hideLocked,
                setHideLocked = { hideLocked = it }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier
        ) {
            items(tasks) { task ->
                val path = viewModel.getPath(task.folderId, hideLocked)
                if (path != null) TaskItem(
                    task = task,
                    path = path,
                    onClick = { onTaskClick(task.id) },
                    onUpdate = { task.id?.let { it1 -> viewModel.updateStatusTask(it1, it) } },
                    onHold = { viewModel.onSelectionTask(task) },
                    selected = task in selectedTasks
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
            message = "Sure want to delete ${selectionCount.value} items?"
        )
    }
}
