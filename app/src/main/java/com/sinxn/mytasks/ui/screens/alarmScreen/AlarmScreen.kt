package com.sinxn.mytasks.ui.screens.alarmScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinxn.mytasks.ui.components.RectangleButton
import com.sinxn.mytasks.ui.components.ScrollablePicker
import com.sinxn.mytasks.ui.viewModels.AlarmViewModel
import com.sinxn.mytasks.utils.ReminderTypes
import com.sinxn.mytasks.utils.formatDate
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class AlarmScreen : ComponentActivity() {
    private val alarmViewModel: AlarmViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val alarmId = intent.getLongExtra("ALARM_ID", 0)
        val taskId = intent.getLongExtra("ALARM_TASK_ID", 0L)
        //val time = intent.getStringExtra("ALARM_TIME") ?: "Not Available"

        setContent {
            FullScreenAlertScreen(
                taskId = taskId,
                alarmId = alarmId,
                alarmViewModel = alarmViewModel,
                onFinish = { finish() }
            )
        }
    }
}

@Composable
fun FullScreenAlertScreen(
    onFinish: () -> Unit,
    alarmViewModel: AlarmViewModel,
    alarmId: Long,
    taskId: Long
) {
    LaunchedEffect(taskId) {
        alarmViewModel.getTaskById(taskId)
    }
    val task by alarmViewModel.task.collectAsState()

    var reminder by remember { mutableStateOf("0") }
    var reminderType by remember { mutableStateOf(ReminderTypes.MINUTE) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = task.id.toString(),
                fontSize = 24.sp
            )
            Text(
                text = task.title,
                fontSize = 24.sp
            )
            Text(
                text = task.description,
                fontSize = 24.sp
            )
            Text(
                text = task.due?.formatDate()?:"NULL",
                fontSize = 24.sp
            )
            Text(text = "Remind me again in")
            Row  {
                val itemHeight = 50.dp

                ScrollablePicker (
                    values = (0..60).toList(),
                    defaultValue = 0,
                    height = itemHeight,
                    modifier = Modifier
                        .width(70.dp)
                        .height(itemHeight)
                ) {
                    reminder = it.toString()
                }
                ScrollablePicker (
                    values = ReminderTypes.entries,
                    defaultValue = ReminderTypes.MINUTE,
                    height = itemHeight,
                    modifier = Modifier
                        .width(100.dp)
                        .height(itemHeight)
                ) {
                    reminderType = it
                }
                RectangleButton(
                    modifier = Modifier.height(itemHeight),
                    onClick = {
                        alarmViewModel.snoozeAlarm(alarmId, LocalDateTime.now().plus(reminder.toLong(), reminderType.unit).toMillis())
                       // Toast.makeText(LocalContext.current, "Alarm Snoozed", Toast.LENGTH_SHORT).show() //TODO
                        onFinish()
                    }
                ) {
                    Text("SET")
                }

            }
            Row {
                RectangleButton(onClick = {
                    alarmViewModel.setAsCompleted(task.id!!)
                    alarmViewModel.cancelNotification(alarmId)
                    onFinish()

                }) {
                    Text(text = "Set as completed")
                }
                RectangleButton(onClick = {
                    alarmViewModel.cancelAlarm(alarmId)
                    alarmViewModel.cancelNotification(alarmId)
                    onFinish()

                }) {
                    Text(text = "Close")
                }
            }
        }

    }
}