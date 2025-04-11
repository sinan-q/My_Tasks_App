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
    private fun getIntent(alarmId: Long, title: String, description: String, timeInMillis: Long): PendingIntent {
        return Intent(context, AlarmReceiver::class.java)
            .apply {
                action = "ACTION_ALARM"
                putExtra("ALARM_ID", alarmId)
                putExtra("ALARM_TITLE", title)
                putExtra("ALARM_DESCRIPTION", description)
                putExtra("ALARM_TIME", timeInMillis)
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
    fun scheduleAlarm(alarmId: Long, title: String, description: String, timeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        assert(alarmManager.canScheduleExactAlarms())
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            getIntent(alarmId, title, description, timeInMillis)
        )
    }
    fun cancelAlarm(alarmId: Long, title: String, description: String, timeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(getIntent(alarmId, title, description, timeInMillis))
    }
}