package com.sinxn.mytasks.ui.screens.alarmScreen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val fullScreenIntent = Intent(context, AlarmScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ALARM_ID", intent.getLongExtra("ALARM_ID", 0))
            putExtra("ALARM_TITLE", intent.getStringExtra("ALARM_TITLE"))
            putExtra("ALARM_DESCRIPTION", intent.getStringExtra("ALARM_DESCRIPTION"))
            putExtra("ALARM_TIME", intent.getStringExtra("ALARM_TIME"))
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
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
            ).apply {
                description = "Channel for event reminders"
            }
            notificationManager.createNotificationChannel(channel)


        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.btn_dropdown)
            .setContentTitle(
                intent.getStringExtra("ALARM_TITLE")
            )
            .setContentText(
                intent.getStringExtra("ALARM_DESCRIPTION")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        notificationManager.notify(1, notification)
    }
}