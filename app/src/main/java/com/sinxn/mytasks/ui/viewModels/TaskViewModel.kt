package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.FolderStore
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.data.interfaces.AlarmRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Alarm
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.utils.differenceSeconds
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepositoryInterface,
    private val alarmRepository: AlarmRepositoryInterface,
    private val selectionStore: SelectionStore,
    private val folderStore: FolderStore
    ) : ViewModel() {

    val selectedTasks = selectionStore.selectedTasks
    val selectedAction = selectionStore.action
    val selectionCount = selectionStore.selectionCount

    val folders = folderStore.folders
    val folder = folderStore.parentFolder

    suspend fun getPath(folderId: Long, hideLocked: Boolean): String? {
         return folderStore.getPath(folderId, hideLocked)
    }

    fun onSelectionTask(task: Task) = selectionStore.toggleTask(task)

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


    val tasks = repository.getAllTasksSorted().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    private val _task = MutableStateFlow(Task())
    val task: StateFlow<Task> = _task

    private val _reminders = MutableStateFlow(emptyList<Pair<Int, ChronoUnit>>())
    val reminders: StateFlow<List<Pair<Int, ChronoUnit>>> = _reminders


    fun fetchTaskById(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val alarms = mutableListOf<Pair<Int, ChronoUnit>>()
            val fetchedTask = repository.getTaskById(taskId)!!
            folderStore.fetchFolderById(fetchedTask.folderId)
            alarmRepository.getAlarmsByTaskId(taskId).forEach { alarm ->
                fetchedTask.due?.differenceSeconds(fromMillis(alarm.time))?.let { it2 ->
                    val duration = Duration.ofSeconds(it2)
                    val pair = if (duration.toDaysPart() > 0) Pair(duration.toDaysPart().toInt(), ChronoUnit.DAYS) else if(duration.toHours() > 0) Pair(duration.toHoursPart(), ChronoUnit.HOURS) else if(duration.toMinutesPart() > 0) Pair(duration.toMinutesPart(), ChronoUnit.MINUTES) else Pair(duration.toSecondsPart(), ChronoUnit.SECONDS)
                    alarms.add(pair)
                }
            }
            _task.value = fetchedTask
            _reminders.value = alarms
        }
    }

    fun insertTask(
        task: Task,
        reminders:
        List<Pair<Int, ChronoUnit>>,
        onFinish: () -> Unit
        ) {
        if (task.title.isEmpty() && task.description.isEmpty()) { showToast("Title or Description cannot be empty");return }
        task.due?.let { due ->
            if (reminders.isNotEmpty() && reminders.map { validateReminder(due, it) }.contains(false) ) { showToast("Reminder should be in a future time");return }
        }
        viewModelScope.launch(Dispatchers.IO) {
            var taskId = task.id
            if (taskId != null) {
                repository.updateTask(task)
                alarmRepository.cancelAlarmsByTaskId(taskId)
            } else taskId = repository.insertTask(task)
            task.due?.let { due ->
                reminders.forEach { pair ->
                    val time = due.minus(pair.first.toLong(), pair.second).toMillis()
                    alarmRepository.insertAlarm(Alarm(
                        taskId = taskId,
                        isTask = true, //TODO Event
                        time = time
                    ))
                }
            }
            showToast("Task Added")
            onFinish()
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        if (task.id == null || task.due == null) {
            showToast("Task ID: ${task.id}")
        }
        else {
            alarmRepository.cancelAlarmsByTaskId(task.id)
        }
        repository.deleteTask(task)
        showToast("Task Deleted")

    }

    fun addReminder(pair: Pair<Int, ChronoUnit>) {
        if (reminders.value.contains(pair)) {
            showToast("Reminder already added")
            return
        }
        _reminders.value = reminders.value.plus(pair)
    }

    fun validateReminder(dueDae: LocalDateTime, pair: Pair<Int, ChronoUnit>): Boolean = dueDae.minus(pair.first.toLong(),pair.second).isAfter(LocalDateTime.now())

    fun removeReminder(pair: Pair<Int, ChronoUnit>) {
        _reminders.value = reminders.value.minus(pair)
    }

    fun updateStatusTask(taskId: Long, status: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
           repository.updateStatusTask(taskId, status)
        }
    }

    fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            folderStore.fetchFolderById(folderId)
            _task.value = task.value.copy(
                folderId = folderId
            )
        }

    }
}
