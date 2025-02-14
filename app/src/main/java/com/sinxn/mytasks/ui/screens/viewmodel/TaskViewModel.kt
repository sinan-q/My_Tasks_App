package com.sinxn.mytasks.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    val tasks = repository.getAllTasks().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task

    fun fetchTaskById(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedTask = repository.getTaskById(taskId)
            _task.value = fetchedTask
        }
    }

    fun insertTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTask(task)
    }
}
