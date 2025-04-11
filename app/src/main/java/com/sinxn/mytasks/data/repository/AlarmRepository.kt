package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.AlarmDao
import com.sinxn.mytasks.data.local.dao.TaskDao
import com.sinxn.mytasks.data.local.entities.Alarm
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao,
    private val taskDao: TaskDao
) {
    suspend fun getAlarms(): List<Alarm> = alarmDao.getAlarms()

    suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insertAlarm(alarm)
    }

    suspend fun updateAlarm(id: Long, time: Long) {
        alarmDao.updateAlarm(id, time)
    }

    suspend fun deleteAlarm(id: Long) {
        alarmDao.deleteAlarm(id)
    }

    suspend fun getAlarmById(alarmId: Long): Alarm {
        return alarmDao.getAlarmById(alarmId)
    }

    suspend fun getUpcomingAlarms(): List<Alarm> {
        return alarmDao.getUpcomingAlarms(LocalDateTime.now())

    }
}