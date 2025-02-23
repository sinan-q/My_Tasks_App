package com.sinxn.mytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.navigation.NavGraph
import com.sinxn.mytasks.ui.screens.viewmodel.EventViewModel
import com.sinxn.mytasks.ui.screens.viewmodel.HomeViewModel
import com.sinxn.mytasks.ui.screens.viewmodel.NoteViewModel
import com.sinxn.mytasks.ui.theme.MyTasksTheme
import com.sinxn.mytasks.ui.screens.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val noteViewModel: NoteViewModel by viewModels()
    private val taskViewModel: TaskViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTasksTheme {
                MainScreen(noteViewModel = noteViewModel,
                    taskViewModel = taskViewModel,
                    homeViewModel = homeViewModel,
                    eventViewModel = eventViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(noteViewModel: NoteViewModel, taskViewModel: TaskViewModel, homeViewModel: HomeViewModel, eventViewModel: EventViewModel) {
    val navController = rememberNavController()
    Scaffold(
        contentWindowInsets = WindowInsets.safeContent,
        bottomBar = { BottomBar(navController = navController) }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            noteViewModel = noteViewModel,
            taskViewModel = taskViewModel,
            homeViewModel = homeViewModel,
            eventViewModel = eventViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

