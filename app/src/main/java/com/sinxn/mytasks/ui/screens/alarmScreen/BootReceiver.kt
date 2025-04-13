package com.sinxn.mytasks.ui.screens.alarmScreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sinxn.mytasks.data.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var alarmRepository: AlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                alarmRepository.getAlarms().forEach { alarm ->
                        alarmScheduler.scheduleAlarm(alarm)
                }
            }
        }
    }
}