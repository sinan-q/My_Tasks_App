package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.domain.models.Alarm

interface AlarmSchedulerInterface {
    fun scheduleAlarm(alarm: Alarm)
    fun cancelAlarm(alarm: Alarm)
}
