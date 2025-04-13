package com.sinxn.mytasks.ui.screens.alarmScreen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.local.database.AppDatabase
import com.sinxn.mytasks.data.local.entities.Alarm
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.repository.AlarmRepository
import com.sinxn.mytasks.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val taskRepository: TaskRepository,
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

