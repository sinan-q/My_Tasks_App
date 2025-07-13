package com.sinxn.mytasks

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.navigation.NavGraph
import com.sinxn.mytasks.ui.theme.MyTasksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            MyTasksTheme {
                Scaffold(
                    contentWindowInsets = WindowInsets.safeContent,
                    bottomBar = { BottomBar(navController = navController) }
                ) { paddingValues ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues),
                    )
                }
            }
        }
    }
}

