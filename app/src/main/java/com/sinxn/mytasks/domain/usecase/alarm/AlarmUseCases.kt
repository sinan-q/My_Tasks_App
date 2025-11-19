package com.sinxn.mytasks.domain.usecase.alarm

import com.sinxn.mytasks.domain.models.Alarm
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.domain.repository.AlarmSchedulerInterface

data class AlarmUseCases(
    val getAlarms: GetAlarmsUseCase,
    val getAlarmById: GetAlarmByIdUseCase,
    val snoozeAlarm: SnoozeAlarmUseCase,
    val cancelAlarm: CancelAlarmUseCase,
    val deleteAlarm: DeleteAlarmUseCase,
    val cancelAlarmsByTaskId: CancelAlarmsByTaskIdUseCase,
    val insertAlarm: InsertAlarmUseCase,
    val insertAlarms: InsertAlarmsUseCase,
    val clearAllAlarms: ClearAllAlarmsUseCase,
    val getAlarmsByTaskId: GetAlarmsByTaskIdUseCase,
    val getUpcomingAlarms: GetUpcomingAlarmsUseCase
)

class GetAlarmsUseCase(private val repository: AlarmRepositoryInterface) {
    suspend operator fun invoke(): List<Alarm> = repository.getAlarms()
}

class GetAlarmByIdUseCase(private val repository: AlarmRepositoryInterface) {
    suspend operator fun invoke(id: Long): Alarm = repository.getAlarmById(id)
}

class SnoozeAlarmUseCase(
    private val repository: AlarmRepositoryInterface,
    private val scheduler: AlarmSchedulerInterface
) {
    suspend operator fun invoke(alarm: Alarm) {
        repository.snoozeAlarm(alarm)
        scheduler.scheduleAlarm(alarm)
    }
}

class CancelAlarmUseCase(
    private val repository: AlarmRepositoryInterface,
    private val scheduler: AlarmSchedulerInterface
) {
    suspend operator fun invoke(alarm: Alarm) {
        scheduler.cancelAlarm(alarm)
        repository.deleteAlarm(alarm.alarmId)
    }
}

class DeleteAlarmUseCase(private val repository: AlarmRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.deleteAlarm(id)
}

class CancelAlarmsByTaskIdUseCase(private val repository: AlarmRepositoryInterface) {
    suspend operator fun invoke(taskId: Long) = repository.cancelAlarmsByTaskId(taskId)
}

class InsertAlarmUseCase(private val repository: AlarmRepositoryInterface) {
    suspend operator fun invoke(alarm: Alarm) = repository.insertAlarm(alarm)
}

class InsertAlarmsUseCase(private val repository: AlarmRepositoryInterface) {
    suspend operator fun invoke(alarms: List<Alarm>) = repository.insertAlarms(alarms)
}

class ClearAllAlarmsUseCase(private val repository: AlarmRepositoryInterface) {
    suspend operator fun invoke() = repository.clearAllAlarms()
}

class GetAlarmsByTaskIdUseCase(private val repository: AlarmRepositoryInterface) {
    suspend operator fun invoke(taskId: Long): List<Alarm> = repository.getAlarmsByTaskId(taskId)
}

class GetUpcomingAlarmsUseCase(private val repository: AlarmRepositoryInterface) {
    suspend operator fun invoke(): List<Alarm> = repository.getUpcomingAlarms()
}
