package com.sinxn.mytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.navigation.NavGraph
import com.sinxn.mytasks.ui.screens.viewmodel.NoteViewModel
import com.sinxn.mytasks.ui.theme.MyTasksTheme
import com.sinxn.mytasks.ui.screens.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val noteViewModel: NoteViewModel by viewModels()
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTasksTheme {
                MainScreen(noteViewModel = noteViewModel, taskViewModel = taskViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(noteViewModel: NoteViewModel, taskViewModel: TaskViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            noteViewModel = noteViewModel,
            taskViewModel = taskViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

