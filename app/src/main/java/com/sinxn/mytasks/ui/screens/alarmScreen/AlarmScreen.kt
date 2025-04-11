package com.sinxn.mytasks.ui.screens.alarmScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val id = intent.getLongExtra("ALARM_ID", 0)
        val title = intent.getStringExtra("ALARM_TITLE") ?: ""
        val description = intent.getStringExtra("ALARM_DESCRIPTION") ?: ""
        val time = intent.getStringExtra("ALARM_TIME") ?: ""

        setContent {
            FullScreenAlertScreen(id, title, description, time) { finish() }
        }
    }
}

@Composable
fun FullScreenAlertScreen(
    id: Long,
    title: String,
    description: String,
    time: String,
    onFinish: () -> Unit
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