package com.sinxn.mytasks.ui.features.tasks.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.core.SelectionActionHandler
import com.sinxn.mytasks.core.SelectionStateHolder
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
    private val getPathUseCase: GetPathUseCase,
    private val selectionActionHandler: SelectionActionHandler,
    private val selectionStateHolder: SelectionStateHolder
) : ViewModel() {

    val selectedTasks = selectionStateHolder.selectedState
    val selectedAction = selectionStateHolder.action
    val selectionCount = selectionStateHolder.selectionCount

    private val _uiState = MutableStateFlow(TasksListUiState())
    val uiState = _uiState.asStateFlow()

    // Pre-computed paths map: taskId -> path string
    private val _paths = MutableStateFlow<Map<Long, String?>>(emptyMap())
    val paths = _paths.asStateFlow()

    private val _hideLocked = MutableStateFlow(true)
    val hideLocked = _hideLocked.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            taskUseCases.getTasks().map { tasks -> tasks.map { it.toListItemUiModel() } }.collectLatest { tasks ->
                _uiState.value = TasksListUiState(tasks = tasks)
                // Pre-compute paths for all tasks
                computePaths(tasks, _hideLocked.value)
            }
        }
    }

    private suspend fun computePaths(tasks: List<TaskListItemUiModel>, hideLocked: Boolean) {
        val pathsMap = mutableMapOf<Long, String?>()
        for (task in tasks) {
            pathsMap[task.id] = getPathUseCase(task.folderId, hideLocked)
        }
        _paths.value = pathsMap
    }

    fun setHideLocked(hide: Boolean) {
        _hideLocked.value = hide
        viewModelScope.launch {
            computePaths(_uiState.value.tasks, hide)
        }
    }

    fun onAction(action: TaskListAction) {
        when (action) {
            is TaskListAction.UpdateStatusTask -> updateStatusTask(action.taskId, action.status)
            is TaskListAction.OnSelectionAction -> onSelectionAction(action.action)
        }
    }

    fun onSelectionTask(id: Long) = viewModelScope.launch {
        taskUseCases.getTask(id)?.let { selectionStateHolder.toggleTask(it) }
    }

    fun onSelectionAction(action: SelectionAction) = viewModelScope.launch {
        selectionActionHandler.onAction(action)
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