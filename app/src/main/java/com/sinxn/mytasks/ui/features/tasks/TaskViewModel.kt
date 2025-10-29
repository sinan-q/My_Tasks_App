package com.sinxn.mytasks.ui.features.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.data.local.entities.Alarm
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.domain.usecase.folder.GetPathUseCase
import com.sinxn.mytasks.utils.Constants
import com.sinxn.mytasks.utils.differenceSeconds
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepositoryInterface,
    private val alarmRepository: AlarmRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
    private val selectionStore: SelectionStore,
    private val getPathUseCase: GetPathUseCase
) : ViewModel() {

    val selectedTasks = selectionStore.selectedTasks
    val selectedAction = selectionStore.action
    val selectionCount = selectionStore.selectionCount

    private val _uiState = MutableStateFlow(TasksListUiState())
    val uiState = _uiState.asStateFlow()

    private val _taskScreenUiState = MutableStateFlow(TaskScreenUiState())
    val taskScreenUiState = _taskScreenUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllTasksSorted().map { tasks -> tasks.map { it.toListItemUiModel() } }.collectLatest { tasks ->
                _uiState.value = TasksListUiState(tasks = tasks)
            }
        }
    }

    fun onAction(action: AddEditTaskAction) {
        when (action) {
            is AddEditTaskAction.UpdateTask -> onTaskUpdate(action.task)
            is AddEditTaskAction.InsertTask -> insertTask(action.task, action.reminders)
            is AddEditTaskAction.DeleteTask -> deleteTask(action.task)
            is AddEditTaskAction.FetchTaskById -> fetchTaskById(action.taskId)
            is AddEditTaskAction.FetchFolderById -> fetchFolderById(action.folderId)
            is AddEditTaskAction.AddReminder -> addReminder(action.reminder)
            is AddEditTaskAction.RemoveReminder -> removeReminder(action.reminder)
            is AddEditTaskAction.UpdateStatusTask -> updateStatusTask(action.taskId, action.status)
        }
    }

    suspend fun getPath(folderId: Long, hideLocked: Boolean): String? {
        return getPathUseCase(folderId, hideLocked)
    }

    fun onSelectionTask(id: Long) = viewModelScope.launch {
        repository.getTaskById(id)?.let { selectionStore.toggleTask(it) }
    }

    fun setSelectionAction(action: SelectionActions) = selectionStore.setAction(action)

    fun clearSelection() {
        selectionStore.clearSelection()
    }

    fun deleteSelection() {
        viewModelScope.launch {
            selectionStore.deleteSelection()
        }
    }
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    private fun onTaskUpdate(task: TaskUiState) {
        _taskScreenUiState.value = _taskScreenUiState.value.copy(task = task)
    }

    private fun fetchTaskById(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _taskScreenUiState.value = _taskScreenUiState.value.copy(isLoading = true)
            try {
                val fetchedTask = repository.getTaskById(taskId)
                if (fetchedTask == null) {
                    showToast("Task not found")
                    _taskScreenUiState.value = _taskScreenUiState.value.copy(isLoading = false)
                    return@launch
                }

                val alarms = mutableListOf<Pair<Int, ChronoUnit>>().apply {
                    alarmRepository.getAlarmsByTaskId(taskId).forEach { alarm ->
                        fetchedTask.due?.differenceSeconds(fromMillis(alarm.time))?.let { it2 ->
                            val duration = Duration.ofSeconds(it2)
                            val pair = if (duration.toDaysPart() > 0) Pair(duration.toDaysPart().toInt(), ChronoUnit.DAYS) else if(duration.toHours() > 0) Pair(duration.toHoursPart(), ChronoUnit.HOURS) else if(duration.toMinutesPart() > 0) Pair(duration.toMinutesPart(), ChronoUnit.MINUTES) else Pair(duration.toSecondsPart(), ChronoUnit.SECONDS)
                            add(pair)
                        }
                    }
                }

                val fetchedFolder = folderRepository.getFolderById(fetchedTask.folderId)
                val subFolders = folderRepository.getSubFolders(fetchedTask.folderId).first()

                _taskScreenUiState.value = TaskScreenUiState(
                    task = fetchedTask.toUiState(),
                    reminders = alarms,
                    folder = fetchedFolder,
                    folders = subFolders,
                    isLoading = false
                )

            } catch (e: Exception) {
                showToast(e.message ?: "An error occurred")
                _taskScreenUiState.value = _taskScreenUiState.value.copy(isLoading = false)
            }
        }
    }

    private fun insertTask(task: TaskUiState, reminders: List<Pair<Int, ChronoUnit>> ) {
        if (task.title.isEmpty() && task.description.isEmpty()) {
            showToast(Constants.SAVE_FAILED_EMPTY)
            return
        }
        task.due?.let { due ->
            if (reminders.isNotEmpty() && reminders.map {
                validateReminder(due, it)
            }.contains(false) ) {
                showToast(Constants.NOTE_SAVE_FAILED_REMINDER_IN_PAST)
                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            var taskId = task.id
            if (taskId != null) {
                repository.updateTask(task.toDomain())
                alarmRepository.cancelAlarmsByTaskId(taskId)
            } else taskId = repository.insertTask(task.toDomain())
            task.due?.let { due ->
                reminders.forEach { pair ->
                    val time = due.minus(pair.first.toLong(), pair.second).toMillis()
                    alarmRepository.insertAlarm(
                        Alarm(
                        taskId = taskId,
                        isTask = true, //TODO Event
                        time = time
                    ))
                }
            }
            showToast(Constants.SAVE_SUCCESS)
        }
    }

    private fun deleteTask(task: TaskUiState) = viewModelScope.launch(Dispatchers.IO) {
        if (task.id == null) {
            showToast(Constants.DELETE_FAILED)
            return@launch
        }
        if(task.due != null) {
            alarmRepository.cancelAlarmsByTaskId(task.id)
        }
        val deleted = repository.deleteTask(task.toDomain())
        if (deleted == 0) {
            showToast(Constants.DELETE_FAILED)
            return@launch
        }
        showToast(Constants.DELETE_SUCCESS)

    }

    private fun addReminder(pair: Pair<Int, ChronoUnit>) {
        if (_taskScreenUiState.value.task.due == null || !validateReminder(_taskScreenUiState.value.task.due!!, pair)) {
            showToast(Constants.NOTE_SAVE_FAILED_REMINDER_IN_PAST)
            return
        }
        if (_taskScreenUiState.value.reminders.contains(pair)) {
            showToast(Constants.TASK_REMINDER_ALREADY_EXISTS)
            return
        }
        _taskScreenUiState.value = _taskScreenUiState.value.copy(reminders = _taskScreenUiState.value.reminders.plus(pair))
    }

    private fun validateReminder(dueDae: LocalDateTime, pair: Pair<Int, ChronoUnit>): Boolean = dueDae.minus(pair.first.toLong(),pair.second).isAfter(LocalDateTime.now())

    private fun removeReminder(pair: Pair<Int, ChronoUnit>) {
        _taskScreenUiState.value = _taskScreenUiState.value.copy(reminders = _taskScreenUiState.value.reminders.minus(pair))
    }

    private fun updateStatusTask(taskId: Long, status: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStatusTask(taskId, status)
        }
    }

    private fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            _taskScreenUiState.value = _taskScreenUiState.value.copy(
                folder = fetchedFolder,
                folders = subFolders,
                task = _taskScreenUiState.value.task.copy(folderId = folderId)
            )
        }
    }
}

private fun Task.toUiState(): TaskUiState {
    return TaskUiState(
        id = id,
        folderId = folderId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        due = due,
        recurrenceRule = recurrenceRule
    )
}

private fun TaskUiState.toDomain(): Task {
    return Task(
        id = id,
        folderId = folderId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        due = due,
        recurrenceRule = recurrenceRule
    )
}
