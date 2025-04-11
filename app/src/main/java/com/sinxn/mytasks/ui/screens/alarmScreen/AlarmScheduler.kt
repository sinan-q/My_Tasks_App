package com.sinxn.mytasks.ui.screens.alarmScreen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getIntent(alarmId: Long): PendingIntent {
        return Intent(context, AlarmReceiver::class.java)
            .apply {
                action = "ACTION_ALARM"
                putExtra("ALARM_ID", alarmId)
            }
            .let { intent ->
            PendingIntent.getBroadcast(
                context,
                alarmId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
    fun scheduleAlarm(alarmId: Long,timeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        assert(alarmManager.canScheduleExactAlarms())
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            getIntent(alarmId)
        )
    }
    fun cancelAlarm(alarmId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(getIntent(alarmId))
    }
}