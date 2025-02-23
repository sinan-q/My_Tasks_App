package com.sinxn.mytasks.ui.navigation

import AddEditTaskScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sinxn.mytasks.ui.screens.AddEditNoteScreen
import com.sinxn.mytasks.ui.screens.HomeScreen
import com.sinxn.mytasks.ui.screens.NoteListScreen
import com.sinxn.mytasks.ui.screens.TaskListScreen
import com.sinxn.mytasks.ui.screens.viewmodel.HomeViewModel
import com.sinxn.mytasks.ui.screens.viewmodel.NoteViewModel
import com.sinxn.mytasks.ui.screens.viewmodel.TaskViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    taskViewModel: TaskViewModel,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                homeViewModel = homeViewModel,
                onAddNoteClick = { folderId -> navController.navigate("add_edit_note/-1L/$folderId") },
                onNoteClick = { noteId ->
                    navController.navigate("add_edit_note/$noteId/0")
                },
                onAddTaskClick = { folderId->
                    navController.navigate("add_edit_task/-1L/$folderId")

                },
                onTaskClick = { taskId ->
                    navController.navigate("add_edit_task/$taskId/0"){
                        popUpTo(navController.currentBackStackEntry!!.destination.id){
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("note_list") {
            NoteListScreen(
                notes = noteViewModel.notes.collectAsState().value,
                onAddNoteClick = { navController.navigate("add_edit_note/-1L/0") },
                onNoteClick = { noteId ->
                    navController.navigate("add_edit_note/$noteId/0")
                }
            )
        }
        composable(
            route = "add_edit_note/{noteId}/{folderId}",
            arguments = listOf(
                navArgument("noteId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("folderId") { type = NavType.LongType; defaultValue = 0 },

                )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0
            AddEditNoteScreen(
                noteId = noteId,
                folderId = folderId,
                onFinish = { navController.popBackStack() },
                noteViewModel = noteViewModel,
                modifier = Modifier
            )
        }

        composable("tasks") {
            TaskListScreen(
                tasks = taskViewModel.tasks.collectAsState().value,
                onAddTaskClick = { parentId->
                    navController.navigate("add_edit_task/-1L/$parentId")
                },
                onTaskClick = { taskId ->
                    navController.navigate("add_edit_task/$taskId/0")
                },
                taskViewModel = taskViewModel
            )
        }
        composable(
            route = "add_edit_task/{taskId}/{folderId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("folderId") { type = NavType.LongType; defaultValue = 0 },
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0
            AddEditTaskScreen(
                taskId = taskId,
                folderId = folderId,
                taskViewModel = taskViewModel,
                onFinish = { navController.popBackStack() },
            )
        }
    }
}
