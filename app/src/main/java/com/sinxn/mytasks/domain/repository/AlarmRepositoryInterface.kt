package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.data.local.entities.Alarm

interface AlarmRepositoryInterface {
    suspend fun getAlarms(): List<Alarm>
    suspend fun insertAlarm(alarm: Alarm)
    suspend fun insertAlarms(alarms: List<Alarm>)
    suspend fun clearAllAlarms()
    suspend fun snoozeAlarm(alarm: Alarm)
    suspend fun deleteAlarm(id: Long)
    suspend fun getAlarmById(alarmId: Long): Alarm
    suspend fun getAlarmsByTaskId(taskId: Long): List<Alarm>
    suspend fun cancelAlarmsByTaskId(taskId: Long)
    suspend fun getUpcomingAlarms(): List<Alarm>
}
