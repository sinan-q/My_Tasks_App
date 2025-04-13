package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.AlarmDao
import com.sinxn.mytasks.data.local.dao.TaskDao
import com.sinxn.mytasks.data.local.entities.Alarm
import com.sinxn.mytasks.ui.screens.alarmScreen.AlarmScheduler
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao,
    private val taskDao: TaskDao,
    private val alarmScheduler: AlarmScheduler,

    ) {
    suspend fun getAlarms(): List<Alarm> = alarmDao.getAlarms()

    suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insertAlarm(alarm)
        alarmScheduler.scheduleAlarm(alarm)
    }
//    suspend fun rescheduleAlarms() { //TODO
//        alarmDao.insertAlarm(alarm)
//        alarmScheduler.scheduleAlarm(alarm)
//    }
    suspend fun snoozeAlarm(alarm: Alarm) {
        alarmScheduler.scheduleAlarm(alarm)
        alarmDao.updateAlarm(alarm.alarmId, alarm.time)
    }

    suspend fun deleteAlarm(id: Long) {
        val alarm = alarmDao.getAlarmById(id)
        alarmScheduler.cancelAlarm(alarm)
        alarmDao.deleteAlarm(alarm)
    }

    suspend fun getAlarmById(alarmId: Long): Alarm {
        return alarmDao.getAlarmById(alarmId)
    }

    suspend fun getAlarmsByTaskId(taskId: Long): List<Alarm> {
        return alarmDao.getAlarmsByTaskId(taskId)
    }

    suspend fun cancelAlarmsByTaskId(taskId: Long) {
        getAlarmsByTaskId(taskId).forEach { alarm ->
            alarmScheduler.cancelAlarm(alarm)
            deleteAlarm(alarm.alarmId)
        }
    }

    suspend fun getUpcomingAlarms(): List<Alarm> {
        return alarmDao.getUpcomingAlarms(LocalDateTime.now())

    }
}