package com.sinxn.mytasks.ui.features.tasks.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.domain.models.Alarm
import com.sinxn.mytasks.domain.models.Task
import com.sinxn.mytasks.domain.usecase.alarm.AlarmUseCases
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.ui.features.tasks.list.TaskScreenUiState
import com.sinxn.mytasks.ui.features.tasks.list.TaskUiState
import com.sinxn.mytasks.utils.Constants
import com.sinxn.mytasks.utils.differenceSeconds
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val alarmUseCases: AlarmUseCases,
    private val folderUseCases: FolderUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun onAction(action: AddEditTaskAction) {
        when (action) {
            is AddEditTaskAction.UpdateTask -> onTaskUpdate(action.task)
            is AddEditTaskAction.InsertTask -> insertTask(action.task, action.reminders)
            is AddEditTaskAction.DeleteTask -> deleteTask(action.task)
            is AddEditTaskAction.FetchTaskById -> fetchTaskById(action.taskId)
            is AddEditTaskAction.FetchFolderById -> fetchFolderById(action.folderId)
            is AddEditTaskAction.AddReminder -> addReminder(action.reminder)
            is AddEditTaskAction.RemoveReminder -> removeReminder(action.reminder)
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    private fun onTaskUpdate(task: TaskUiState) {
        _uiState.value = _uiState.value.copy(task = task)
    }

    private fun fetchTaskById(taskId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val fetchedTask = taskUseCases.getTask(taskId)
                if (fetchedTask == null) {
                    showToast("Task not found")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@launch
                }

                val alarms = mutableListOf<Pair<Int, ChronoUnit>>().apply {
                    alarmUseCases.getAlarmsByTaskId(taskId).forEach { alarm ->
                        fetchedTask.due?.differenceSeconds(fromMillis(alarm.time))?.let { it2 ->
                            val duration = Duration.ofSeconds(it2)
                            val pair = if (duration.toDaysPart() > 0) Pair(duration.toDaysPart().toInt(), ChronoUnit.DAYS) else if(duration.toHours() > 0) Pair(duration.toHoursPart(), ChronoUnit.HOURS) else if(duration.toMinutesPart() > 0) Pair(duration.toMinutesPart(), ChronoUnit.MINUTES) else Pair(duration.toSecondsPart(), ChronoUnit.SECONDS)
                            add(pair)
                        }
                    }
                }

                val fetchedFolder = folderUseCases.getFolder(fetchedTask.folderId)
                val subFolders = folderUseCases.getSubFolders(fetchedTask.folderId).first()

                _uiState.value = TaskScreenUiState(
                    task = fetchedTask.toUiState(),
                    reminders = alarms,
                    folder = fetchedFolder,
                    folders = subFolders,
                    isLoading = false
                )

            } catch (e: Exception) {
                showToast(e.message ?: "An error occurred")
                _uiState.value = _uiState.value.copy(isLoading = false)
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
        viewModelScope.launch {
            var taskId = task.id
            if (taskId != null) {
                taskUseCases.updateTask(task.toDomain())
                alarmUseCases.cancelAlarmsByTaskId(taskId)
            } else taskId = taskUseCases.addTask(task.toDomain())
            
            task.due?.let { due ->
                reminders.forEach { pair ->
                    val time = due.minus(pair.first.toLong(), pair.second).toMillis()
                    alarmUseCases.insertAlarm(
                        Alarm(
                            alarmId = 0,
                            taskId = taskId,
                            isTask = true, //TODO Event
                            time = time
                        )
                    )
                }
            }
            showToast(Constants.SAVE_SUCCESS)
        }
    }

    private fun deleteTask(task: TaskUiState) = viewModelScope.launch {
        if (task.id == null) {
            showToast(Constants.DELETE_FAILED)
            return@launch
        }
        if(task.due != null) {
            alarmUseCases.cancelAlarmsByTaskId(task.id)
        }
        val deleted = taskUseCases.deleteTask(task.toDomain())
        if (deleted == 0) {
            showToast(Constants.DELETE_FAILED)
            return@launch
        }
        showToast(Constants.DELETE_SUCCESS)

    }

    private fun addReminder(pair: Pair<Int, ChronoUnit>) {
        if (_uiState.value.task.due == null || !validateReminder(_uiState.value.task.due!!, pair)) {
            showToast(Constants.NOTE_SAVE_FAILED_REMINDER_IN_PAST)
            return
        }
        if (_uiState.value.reminders.contains(pair)) {
            showToast(Constants.TASK_REMINDER_ALREADY_EXISTS)
            return
        }
        _uiState.value = _uiState.value.copy(reminders = _uiState.value.reminders.plus(pair))
    }

    private fun validateReminder(dueDae: LocalDateTime, pair: Pair<Int, ChronoUnit>): Boolean = dueDae.minus(pair.first.toLong(),pair.second).isAfter(LocalDateTime.now())

    private fun removeReminder(pair: Pair<Int, ChronoUnit>) {
        _uiState.value = _uiState.value.copy(reminders = _uiState.value.reminders.minus(pair))
    }

    private fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderUseCases.getFolder(folderId)
            val subFolders = folderUseCases.getSubFolders(folderId).first()
            _uiState.value = _uiState.value.copy(
                folder = fetchedFolder,
                folders = subFolders,
                task = _uiState.value.task.copy(folderId = folderId)
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
