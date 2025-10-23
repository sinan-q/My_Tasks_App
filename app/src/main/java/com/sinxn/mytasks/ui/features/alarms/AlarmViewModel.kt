package com.sinxn.mytasks.ui.features.alarms

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.features.alarms.broadcastReceivers.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val alarmScheduler: AlarmScheduler,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

