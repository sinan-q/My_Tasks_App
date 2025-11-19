package com.sinxn.mytasks.ui.features.alarms

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.domain.models.Task
import com.sinxn.mytasks.ui.features.alarms.broadcastReceivers.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AlarmScreenUiState {
    object Loading : AlarmScreenUiState()
    data class Success(val task: Task) : AlarmScreenUiState()
    data class Error(val message: String) : AlarmScreenUiState()
}

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val alarmScheduler: AlarmScheduler,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val _uiState = MutableStateFlow<AlarmScreenUiState>(AlarmScreenUiState.Loading)
    val uiState: StateFlow<AlarmScreenUiState> = _uiState

    fun getTaskById(id: Long) {
        viewModelScope.launch {
            _uiState.value = AlarmScreenUiState.Loading
            try {
                val task = taskRepository.getTaskById(id)
                if (task != null) {
                    _uiState.value = AlarmScreenUiState.Success(task)
                } else {
                    _uiState.value = AlarmScreenUiState.Error("Task not found.")
                }
            } catch (e: Exception) {
                _uiState.value = AlarmScreenUiState.Error(e.message ?: "An error occurred")
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

    fun cancelAlarm(alarmId: Long) {
        viewModelScope.launch {
            val alarm = alarmRepository.getAlarmById(alarmId)
            alarmScheduler.cancelAlarm(alarm)
            alarmRepository.deleteAlarm(alarmId)
        }
    }

    fun cancelNotification(alarmId: Long) {
        notificationManager.cancel(alarmId.toInt())
    }

    fun setAsCompleted(taskId: Long) {
        viewModelScope.launch {
            taskRepository.updateStatusTask(taskId, true)
            alarmRepository.cancelAlarmsByTaskId(taskId)

        }
    }
}
