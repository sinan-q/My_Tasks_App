package com.sinxn.mytasks.data.interfaces

import com.sinxn.mytasks.data.local.entities.Alarm
import java.time.LocalDateTime

interface AlarmRepositoryInterface {
    suspend fun getAlarms(): List<Alarm>
    suspend fun insertAlarm(alarm: Alarm)
    suspend fun snoozeAlarm(alarm: Alarm)
    suspend fun deleteAlarm(id: Long)
    suspend fun getAlarmById(alarmId: Long): Alarm
    suspend fun getAlarmsByTaskId(taskId: Long): List<Alarm>
    suspend fun cancelAlarmsByTaskId(taskId: Long)
    suspend fun getUpcomingAlarms(): List<Alarm>
}
