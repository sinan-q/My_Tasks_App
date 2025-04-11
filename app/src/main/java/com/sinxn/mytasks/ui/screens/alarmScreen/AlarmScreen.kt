package com.sinxn.mytasks.ui.screens.alarmScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.sinxn.mytasks.data.repository.AlarmRepository
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmScreen : ComponentActivity() {
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var alarmRepository: AlarmRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val id = intent.getLongExtra("ALARM_ID", 0)
        val title = intent.getStringExtra("ALARM_TITLE") ?: "Not Available"
        val description = intent.getStringExtra("ALARM_DESCRIPTION") ?: "Not Available"
        val time = intent.getStringExtra("ALARM_TIME") ?: "Not Available"

        setContent {
            FullScreenAlertScreen(id, title, description, time, onFinish = { finish() }, onSnoozeAlarm = { newTimeInMillis ->
                CoroutineScope(Dispatchers.IO).launch {
                    alarmScheduler.scheduleAlarm(id, title, description, newTimeInMillis)
                    alarmRepository.updateAlarm(id, newTimeInMillis)
                }

            } )
        }
    }
}

@Composable
fun FullScreenAlertScreen(
    id: Long,
    title: String,
    description: String,
    time: String,
    onFinish: () -> Unit,
    onSnoozeAlarm: (Long) -> Unit

) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = id.toString(),
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = description,
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = time,
                color = Color.White,
                fontSize = 24.sp
            )
            Row {
                Button(onClick = {
                    onSnoozeAlarm(LocalDateTime.now().plusMinutes(1).toMillis())
                    onFinish()
                }) {
                    Text(
                        text = "Snooze 5 minute",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
                Button(onClick = onFinish) {
                    Text(
                        text = "Close",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }

    }
}