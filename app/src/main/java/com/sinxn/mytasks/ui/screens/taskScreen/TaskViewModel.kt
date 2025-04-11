package com.sinxn.mytasks.ui.screens.taskScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.repository.FolderRepository
import com.sinxn.mytasks.data.repository.TaskRepository
import com.sinxn.mytasks.ui.screens.alarmScreen.AlarmScheduler
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val folderRepository: FolderRepository,
    private val alarmScheduler: AlarmScheduler
    ) : ViewModel() {

    val tasks = repository.getAllTasksSorted().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    private val _task = MutableStateFlow(Task())
    val task: StateFlow<Task> = _task

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
            val fetchedTask = repository.getTaskById(taskId)!!
            val fetchedFolder = folderRepository.getFolderById(fetchedTask.folderId)
            _task.value = fetchedTask
            _folder.value = fetchedFolder
        }
    }

    fun insertTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        var taskId = task.id ?: 0L
        if (taskId != 0L) {
            repository.updateTask(task)
            alarmScheduler.cancelAlarm(taskId, task.title, task.description, task.due?.toMillis() ?: 0)
        } else taskId = repository.insertTask(task)
        if (taskId != -1L)
            task.due?.let { due -> alarmScheduler.scheduleAlarm(taskId, task.title, task.description, due.toMillis()   ) }
    }

    fun deleteTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        if (task.id == null || task.due == null) {
            Log.d("TaskViewModel","Task ID: ${task.id}")
        }
        else {
            alarmScheduler.cancelAlarm(task.id, task.title, task.description, task.due.toMillis())
            repository.deleteTask(task)
        }

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
