// com.sinxn.mytasks.data.respository.FakeAlarmRepository.kt
package com.sinxn.mytasks.data.respository

import com.sinxn.mytasks.data.interfaces.AlarmRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Alarm
import com.sinxn.mytasks.utils.toMillis
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

class FakeAlarmRepository : AlarmRepositoryInterface {

    val alarms = mutableListOf<Alarm>()
    private val idGenerator = AtomicLong(1)

    override suspend fun getAlarms(): List<Alarm> = alarms.toList()

    override suspend fun insertAlarm(alarm: Alarm) {
        val id = idGenerator.getAndIncrement()
        alarms.add(alarm.copy(alarmId = id))
    }

    override suspend fun snoozeAlarm(alarm: Alarm) {
        alarms.replaceAll {
            if (it.alarmId == alarm.alarmId) alarm else it
        }
    }

    override suspend fun deleteAlarm(id: Long) {
        alarms.removeIf { it.alarmId == id }
    }

    override suspend fun getAlarmById(alarmId: Long): Alarm {
        return alarms.first { it.alarmId == alarmId }
    }

    override suspend fun getAlarmsByTaskId(taskId: Long): List<Alarm> {
        return alarms.filter { it.taskId == taskId }
    }

    override suspend fun cancelAlarmsByTaskId(taskId: Long) {
        alarms.removeIf { it.taskId == taskId }
    }

    override suspend fun getUpcomingAlarms(): List<Alarm> {
        return alarms.filter { it.time >= LocalDateTime.now().toMillis() }
    }
}
