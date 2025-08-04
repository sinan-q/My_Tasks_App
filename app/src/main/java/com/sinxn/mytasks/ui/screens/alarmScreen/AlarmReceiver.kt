package com.sinxn.mytasks.ui.screens.alarmScreen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.sinxn.mytasks.data.repository.TaskRepository
import com.sinxn.mytasks.utils.formatDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("ALARM_ID", 0)
        val taskId = intent.getLongExtra("ALARM_TASK_ID", 0L)
        //val time = intent.getStringExtra("ALARM_TIME") ?: "Not Available"
        val fullScreenIntent = Intent(context, AlarmScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ALARM_ID", alarmId)
            putExtra("ALARM_TASK_ID", taskId)
            //putExtra("ALARM_TIME", time)
        }


        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "event_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel if necessary
        val channel = NotificationChannel(
            channelId,
            "Event Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        CoroutineScope(Dispatchers.IO).launch {
            taskRepository.getTaskById(taskId)?.let { task ->
                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.btn_dropdown)
                    .setContentTitle(task.title)
                    .setContentText(task.description.plus("\ndue on ").plus(task.due?.formatDate()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(fullScreenPendingIntent, true)
                    .setOngoing(true)
                    .build()

                notificationManager.notify(alarmId.toInt(), notification)
            }

        }
    }
}