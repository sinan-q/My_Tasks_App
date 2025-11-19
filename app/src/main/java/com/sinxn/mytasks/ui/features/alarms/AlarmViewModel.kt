package com.sinxn.mytasks.ui.features.alarms

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.domain.models.Task
import com.sinxn.mytasks.domain.usecase.alarm.AlarmUseCases
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
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
    private val alarmUseCases: AlarmUseCases,
    private val taskUseCases: TaskUseCases,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val _uiState = MutableStateFlow<AlarmScreenUiState>(AlarmScreenUiState.Loading)
    val uiState: StateFlow<AlarmScreenUiState> = _uiState

    fun getTaskById(id: Long) {
        viewModelScope.launch {
            _uiState.value = AlarmScreenUiState.Loading
            try {
                val task = taskUseCases.getTask(id)
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
            val newAlarm = alarmUseCases.getAlarmById(alarmId).copy(
                time = newTime
            )
            alarmUseCases.snoozeAlarm(newAlarm)
        }
    }

    fun cancelAlarm(alarmId: Long) {
        viewModelScope.launch {
            val alarm = alarmUseCases.getAlarmById(alarmId)
            alarmUseCases.cancelAlarm(alarm)
        }
    }

    fun cancelNotification(alarmId: Long) {
        notificationManager.cancel(alarmId.toInt())
    }

    fun setAsCompleted(taskId: Long) {
        viewModelScope.launch {
            taskUseCases.updateStatusTask(taskId, true)
            alarmUseCases.cancelAlarmsByTaskId(taskId)
        }
    }
}
