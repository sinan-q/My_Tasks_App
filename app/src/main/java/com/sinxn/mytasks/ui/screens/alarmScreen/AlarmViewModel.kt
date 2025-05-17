package com.sinxn.mytasks.ui.screens.alarmScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.interfaces.AlarmRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
) : ViewModel() {

    private val _task = MutableStateFlow(Task())
    val task: StateFlow<Task> = _task

    fun getTaskById(id: Long) {
        viewModelScope.launch {
            taskRepository.getTaskById(id)?.let {
                _task.value = it
            }
        }
    }

    fun snoozeAlarm(alarmId: Long, newTime: Long) {
        viewModelScope.launch {
            val newAlarm = alarmRepository.getAlarmById(alarmId).copy(
                time = newTime
            )
            alarmRepository.snoozeAlarm(newAlarm)
        }
    }
}

