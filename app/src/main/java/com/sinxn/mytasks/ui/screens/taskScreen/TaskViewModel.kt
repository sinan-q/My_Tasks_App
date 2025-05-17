package com.sinxn.mytasks.ui.screens.taskScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.interfaces.AlarmRepositoryInterface
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Alarm
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.utils.differenceSeconds
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
    private val alarmRepository: AlarmRepositoryInterface
    ) : ViewModel() {

    val tasks = repository.getAllTasksSorted().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    private val _task = MutableStateFlow(Task())
    val task: StateFlow<Task> = _task

    private val _reminders = MutableStateFlow(emptyList<Pair<Int, ChronoUnit>>())
    val reminders: StateFlow<List<Pair<Int, ChronoUnit>>> = _reminders

    private val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders

    init {
        viewModelScope.launch {
            folderRepository.getAllFolders().collect { folders ->
                _folders.value = folders
            }
        }
    }

    fun fetchTaskById(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val alarms = mutableListOf<Pair<Int, ChronoUnit>>()
            val fetchedTask = repository.getTaskById(taskId)!!
            val fetchedFolder = folderRepository.getFolderById(fetchedTask.folderId)
            alarmRepository.getAlarmsByTaskId(taskId).forEach { alarm ->
                fetchedTask.due?.differenceSeconds(fromMillis(alarm.time))?.let { it2 ->
                    val duration = Duration.ofSeconds(it2)
                    val pair = if (duration.toDaysPart() > 0) Pair(duration.toDaysPart().toInt(), ChronoUnit.DAYS) else if(duration.toHours() > 0) Pair(duration.toHoursPart(), ChronoUnit.HOURS) else if(duration.toMinutesPart() > 0) Pair(duration.toMinutesPart(), ChronoUnit.MINUTES) else Pair(duration.toSecondsPart(), ChronoUnit.SECONDS)
                    alarms.add(pair)
                }
            }
            _task.value = fetchedTask
            _folder.value = fetchedFolder
            _reminders.value = alarms
        }
    }

    fun insertTask(task: Task, reminders: List<Pair<Int, ChronoUnit>>) = viewModelScope.launch(Dispatchers.IO) {
        var taskId = task.id ?: 0L
        if (taskId != 0L) {
            repository.updateTask(task)
            alarmRepository.cancelAlarmsByTaskId(taskId)
        } else taskId = repository.insertTask(task)
        if (taskId != -1L)
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
    }

    fun deleteTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        if (task.id == null || task.due == null) {
            Log.d("TaskViewModel","Task ID: ${task.id}")
        }
        else {
            alarmRepository.deleteAlarm(task.id)
        }
        repository.deleteTask(task)


    }

    fun addReminder(pair: Pair<Int, ChronoUnit>) {
        _reminders.value = reminders.value.plus(pair)
    }

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
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            _folders.value = subFolders
            _folder.value = fetchedFolder
            _task.value = task.value.copy(
                folderId = fetchedFolder.folderId,
            )
        }
    }

    fun getPath(folderId: Long): String {
        val path = StringBuilder()
        var curr = folderId
        while (curr != 0L) {
            val folder = folders.value.find { it.folderId == curr }
            path.insert(0, "/")
            path.insert(0, folder?.name)
            curr = folder?.parentFolderId ?: 0L

        }

        return path.toString()
    }
}
