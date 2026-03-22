package com.sinxn.mytasks.ui.features.alarms.broadcastReceivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.sinxn.mytasks.R
import com.sinxn.mytasks.data.repository.TaskRepository
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.domain.repository.AlarmSchedulerInterface
import com.sinxn.mytasks.ui.features.alarms.AlarmScreen
import com.sinxn.mytasks.utils.formatDate
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dmfs.rfc5545.recur.RecurrenceRule
import java.util.TimeZone
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var taskRepository: TaskRepository
    
    @Inject
    lateinit var alarmRepository: AlarmRepositoryInterface

    @Inject
    lateinit var alarmScheduler: AlarmSchedulerInterface

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("ALARM_ID", 0)
        val taskId = intent.getLongExtra("ALARM_TASK_ID", 0L)

        val fullScreenIntent = Intent(context, AlarmScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ALARM_ID", alarmId)
            putExtra("ALARM_TASK_ID", taskId)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "event_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Event Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        CoroutineScope(Dispatchers.IO).launch {
            taskRepository.getTaskById(taskId)?.let { task ->
                if (task.isCompleted) return@launch

                val builder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.task_ic)
                    .setContentTitle(task.title)
                    .setContentText(task.description.plus("\ndue on ").plus(task.due?.formatDate()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setOngoing(true)

                if (notificationManager.canUseFullScreenIntent()) {
                    builder.setFullScreenIntent(fullScreenPendingIntent, true)
                }

                val notification = builder.build()
                notificationManager.notify(alarmId.toInt(), notification)

                if (task.recurrenceRule != null && task.due != null) {
                    val originalAlarm = alarmRepository.getAlarmById(alarmId)
                    if (originalAlarm != null && originalAlarm.trigger == com.sinxn.mytasks.utils.ReminderTrigger.FROM_END) {
                        try {
                            val rule = RecurrenceRule(task.recurrenceRule)
                            val startMillis = task.due.toMillis()
                            val offsetMillis = startMillis - originalAlarm.time

                            val iterator = rule.iterator(startMillis, TimeZone.getDefault())
                            val nowMillis = System.currentTimeMillis()
                            var nextAlarmTime = 0L

                            while (iterator.hasNext()) {
                                val occurrenceMillis = iterator.nextMillis()
                                val potentialAlarmTime = occurrenceMillis - offsetMillis
                                if (potentialAlarmTime > nowMillis) {
                                    nextAlarmTime = potentialAlarmTime
                                    break
                                }
                            }

                            if (nextAlarmTime > 0L) {
                                val nextAlarm = originalAlarm.copy(time = nextAlarmTime)
                                alarmScheduler.scheduleAlarm(nextAlarm)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}