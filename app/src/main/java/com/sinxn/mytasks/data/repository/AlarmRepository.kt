package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.AlarmDao
import com.sinxn.mytasks.data.mapper.toDomain
import com.sinxn.mytasks.data.mapper.toEntity
import com.sinxn.mytasks.domain.models.Alarm
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.ui.features.alarms.broadcastReceivers.AlarmScheduler
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmScheduler: AlarmScheduler,

    ): AlarmRepositoryInterface {
    override suspend fun getAlarms(): List<Alarm> = alarmDao.getAlarms().map { it.toDomain() }

    override suspend fun insertAlarm(alarm: Alarm) {
        val alarmId = alarmDao.insertAlarm(alarm.toEntity())
        if (alarmId != -1L) {
            alarmScheduler.scheduleAlarm(
                alarm.copy(
                    alarmId = alarmId
                )
            )
        }
    }
    override suspend fun insertAlarms(alarms: List<Alarm>) {
        alarmDao.insertAlarms(alarms.map { it.toEntity() })
    }
    override suspend fun clearAllAlarms() {
        alarmDao.clearAllAlarms()
    }

    override suspend fun snoozeAlarm(alarm: Alarm) {
        alarmScheduler.scheduleAlarm(alarm)
        alarmDao.updateAlarm(alarm.alarmId, alarm.time)
    }

    override suspend fun deleteAlarm(id: Long) {
        val alarm = alarmDao.getAlarmById(id)
        alarmScheduler.cancelAlarm(alarm.toDomain())
        alarmDao.deleteAlarm(alarm)
    }

    override suspend fun getAlarmById(alarmId: Long): Alarm {
        return alarmDao.getAlarmById(alarmId).toDomain()
    }

    override suspend fun getAlarmsByTaskId(taskId: Long): List<Alarm> {
        return alarmDao.getAlarmsByTaskId(taskId).map { it.toDomain() }
    }

    override suspend fun cancelAlarmsByTaskId(taskId: Long) {
        getAlarmsByTaskId(taskId).forEach { alarm ->
            alarmScheduler.cancelAlarm(alarm)
            deleteAlarm(alarm.alarmId)
        }
    }

    override suspend fun getUpcomingAlarms(): List<Alarm> {
        return alarmDao.getUpcomingAlarms(LocalDateTime.now()).map { it.toDomain() }

    }
}