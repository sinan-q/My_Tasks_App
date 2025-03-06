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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.navigation.NavGraph
import com.sinxn.mytasks.ui.screens.eventScreen.EventViewModel
import com.sinxn.mytasks.ui.screens.folderScreen.FolderViewModel
import com.sinxn.mytasks.ui.screens.homeScreen.HomeViewModel
import com.sinxn.mytasks.ui.screens.noteScreen.NoteViewModel
import com.sinxn.mytasks.ui.screens.taskScreen.TaskViewModel
import com.sinxn.mytasks.ui.theme.MyTasksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val noteViewModel: NoteViewModel by viewModels()
    private val taskViewModel: TaskViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val eventViewModel: EventViewModel by viewModels()
    private val folderViewModel: FolderViewModel by viewModels()

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
                        noteViewModel = noteViewModel,
                        taskViewModel = taskViewModel,
                        homeViewModel = homeViewModel,
                        eventViewModel = eventViewModel,
                        modifier = Modifier.padding(paddingValues),
                        folderViewModel = folderViewModel
                    )
                }
            }
        }
    }
}

