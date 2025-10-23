package com.sinxn.mytasks.ui.features.alarms.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var alarmRepository: AlarmRepositoryInterface

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