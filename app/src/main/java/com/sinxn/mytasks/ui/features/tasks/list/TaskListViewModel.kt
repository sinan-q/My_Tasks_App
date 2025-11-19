package com.sinxn.mytasks.ui.features.tasks.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.domain.usecase.folder.GetPathUseCase
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val selectionStore: SelectionStore,
    private val getPathUseCase: GetPathUseCase
) : ViewModel() {

    val selectedTasks = selectionStore.selectedTasks
    val selectedAction = selectionStore.action
    val selectionCount = selectionStore.selectionCount

    private val _uiState = MutableStateFlow(TasksListUiState())
    val uiState = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            taskUseCases.getTasks().map { tasks -> tasks.map { it.toListItemUiModel() } }.collectLatest { tasks ->
                _uiState.value = TasksListUiState(tasks = tasks)
            }
        }
    }

    fun onAction(action: TaskListAction) {
        when (action) {
            is TaskListAction.UpdateStatusTask -> updateStatusTask(action.taskId, action.status)
            is TaskListAction.OnSelectionAction -> onSelectionAction(action.action)
        }
    }

    suspend fun getPath(folderId: Long, hideLocked: Boolean): String? {
        return getPathUseCase(folderId, hideLocked)
    }

    fun onSelectionTask(id: Long) = viewModelScope.launch {
        taskUseCases.getTask(id)?.let { selectionStore.toggleTask(it) }
    }

    fun onSelectionAction(action: SelectionAction) = viewModelScope.launch {
        selectionStore.onAction(action)
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    private fun updateStatusTask(taskId: Long, status: Boolean) {
        viewModelScope.launch {
            taskUseCases.updateStatusTask(taskId, status)
        }
    }
}