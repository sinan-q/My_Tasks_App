package com.sinxn.mytasks.ui.screens.alarmScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.sinxn.mytasks.data.local.entities.Alarm
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.repository.AlarmRepository
import com.sinxn.mytasks.ui.screens.backupScreen.BackupViewModel
import com.sinxn.mytasks.utils.formatDate
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmScreen : ComponentActivity() {
    private val alarmViewModel: AlarmViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val id = intent.getLongExtra("ALARM_ID", 0)
        val taskId = intent.getLongExtra("ALARM_TASK_ID", 0L)
        val time = intent.getStringExtra("ALARM_TIME") ?: "Not Available"

        setContent {
            val task by alarmViewModel.task.collectAsState()

            LaunchedEffect(taskId) {
                alarmViewModel.getTaskById(taskId)
            }
            FullScreenAlertScreen(
                task = task,
                onFinish = { finish() },
                onSnoozeAlarm = { newTimeInMillis ->
                    alarmViewModel.snoozeAlarm(id, newTimeInMillis)
                }
            )
        }
    }
}

@Composable
fun FullScreenAlertScreen(
    task: Task,
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
                text = task.id.toString(),
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = task.title,
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = task.description,
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = task.due?.formatDate()?:"NULL",
                color = Color.White,
                fontSize = 24.sp
            )
            Row {
                Button(onClick = {
                    onSnoozeAlarm(LocalDateTime.now().plusMinutes(1).toMillis()) //TODO
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