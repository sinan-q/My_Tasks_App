package com.sinxn.mytasks

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.sinxn.mytasks.ui.navigation.NavGraph
import com.sinxn.mytasks.ui.theme.MyTasksTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            MyTasksTheme {
                Surface  {
                    val context = LocalContext.current
                    var showPermissionDialog by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        val notificationManager = context.getSystemService(NotificationManager::class.java)
                        if (!notificationManager.canUseFullScreenIntent()) {
                            showPermissionDialog = true
                        }
                    }

                    if (showPermissionDialog) {
                        AlertDialog(
                            onDismissRequest = { showPermissionDialog = false },
                            title = { Text("Permission Required") },
                            text = { Text("To ensure alarms ring in full screen even when the device is locked, please grant the 'Full Screen Intent' permission.") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showPermissionDialog = false
                                        val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
                                        intent.data = "package:${context.packageName}".toUri()
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Text("Go to Settings")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showPermissionDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    NavGraph(
                        navController = navController,
                    )
                }
            }
        }
    }
}

