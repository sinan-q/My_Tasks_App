package com.sinxn.mytasks.ui.features.alarms.broadcastReceivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sinxn.mytasks.domain.models.Alarm
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private fun getIntent(alarm: Alarm): PendingIntent {
        return Intent(context, AlarmReceiver::class.java)
            .apply {
                action = "ACTION_ALARM"
                putExtra("ALARM_ID", alarm.alarmId)
                putExtra("ALARM_TASK_ID", alarm.taskId)
                putExtra("ALARM_TIME", alarm.time)
            }
            .let { intent ->
            PendingIntent.getBroadcast(
                context,
                alarm.alarmId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
    fun scheduleAlarm(alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        assert(alarmManager.canScheduleExactAlarms())
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.time,
            getIntent(alarm)
        )
    }
    fun cancelAlarm(alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(getIntent(alarm))
    }
}