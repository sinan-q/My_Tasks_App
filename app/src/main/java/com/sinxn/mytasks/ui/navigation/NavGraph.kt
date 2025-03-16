package com.sinxn.mytasks.ui.navigation

import com.sinxn.mytasks.ui.screens.taskScreen.AddEditTaskScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sinxn.mytasks.ui.screens.backupScreen.BackupScreen
import com.sinxn.mytasks.ui.screens.backupScreen.BackupViewModel
import com.sinxn.mytasks.ui.screens.eventScreen.AddEditEventScreen
import com.sinxn.mytasks.ui.screens.noteScreen.AddEditNoteScreen
import com.sinxn.mytasks.ui.screens.eventScreen.EventListScreen
import com.sinxn.mytasks.ui.screens.folderScreen.FolderListScreen
import com.sinxn.mytasks.ui.screens.homeScreen.HomeScreen
import com.sinxn.mytasks.ui.screens.noteScreen.NoteListScreen
import com.sinxn.mytasks.ui.screens.taskScreen.TaskListScreen
import com.sinxn.mytasks.ui.screens.eventScreen.EventViewModel
import com.sinxn.mytasks.ui.screens.folderScreen.FolderViewModel
import com.sinxn.mytasks.ui.screens.homeScreen.HomeViewModel
import com.sinxn.mytasks.ui.screens.noteScreen.NoteViewModel
import com.sinxn.mytasks.ui.screens.taskScreen.TaskViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    taskViewModel: TaskViewModel,
    homeViewModel: HomeViewModel,
    eventViewModel: EventViewModel,
    folderViewModel: FolderViewModel,
    backupViewModel: BackupViewModel,
    modifier: Modifier = Modifier
) {
    val onAddNoteClick: (folderId: Long?) -> Unit = { folderId -> navController.navigate("add_edit_note/-1L/$folderId") }
    val onNoteClick: (noteId: Long?) -> Unit = { noteId ->
        navController.navigate("add_edit_note/$noteId/0")
    }
    val onAddTaskClick: (folderId: Long?) -> Unit = { folderId->
        navController.navigate("add_edit_task/-1L/$folderId")
    }
    val onTaskClick: (taskId: Long?) -> Unit = { taskId ->
        navController.navigate("add_edit_task/$taskId/0"){
            popUpTo(navController.currentBackStackEntry!!.destination.id){
                inclusive = true
            }
        }
    }

    val onAddEventClick: (folderId: Long?) -> Unit = { folderId ->
        navController.navigate("add_edit_event/-1L/$folderId/-1L")
    }

    val onEventClick: () -> Unit = {
        navController.navigate("event_list")
    }
    val onFolderClick: (folderId: Long) -> Unit = { folderId ->
        navController.navigate("folder_list/$folderId")
    }

    val onBack: () -> Unit = {
        navController.popBackStack()
    }

    val onBackup: () -> Unit = {
        navController.navigate("backup")
    }

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                homeViewModel = homeViewModel,
                taskViewModel = taskViewModel,
                onAddNoteClick = onAddNoteClick,
                onNoteClick = onNoteClick,
                onAddTaskClick = onAddTaskClick,
                onTaskClick = onTaskClick,
                onAddEventClick = onAddEventClick,
                onEventClick = onEventClick,
                onFolderClick = onFolderClick,
                onBackup = onBackup
            )
        }

        composable("note_list") {
            NoteListScreen(
                notes = noteViewModel.notes.collectAsState().value,
                onAddNoteClick = onAddNoteClick,
                onNoteClick = onNoteClick
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
                onAddTaskClick = onAddTaskClick,
                onTaskClick = onTaskClick,
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
        composable("event_list") {
            EventListScreen(
                eventViewModel = eventViewModel,
                onAddEventClick = onAddEventClick,
                onEventClick = onAddEventClick,
                onDayClick = { epochDay ->
                    navController.navigate("add_edit_event/-1L/0/$epochDay")
                }
            )
        }

        composable(
            route = "add_edit_event/{eventId}/{folderId}/{date}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("folderId") { type = NavType.LongType; defaultValue = 0 },
                navArgument("date") { type = NavType.LongType; defaultValue = -1L }
            )
        ){ backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: -1L
            val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0
            val date = backStackEntry.arguments?.getLong("date") ?: -1L
            AddEditEventScreen(
                eventId = eventId,
                folderId = folderId,
                date = date,
                eventViewModel = eventViewModel,
                onFinish = onBack
            )


        }

        composable(
            route = "folder_list/{folderId}",
            arguments = listOf(
                navArgument("folderId") {type = NavType.LongType; defaultValue = 0},
            )
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0
            FolderListScreen(
                folderId = folderId,
                folderViewModel = folderViewModel,
                onAddNoteClick = onAddNoteClick,
                onNoteClick = onNoteClick,
                onAddTaskClick = onAddTaskClick,
                onTaskClick = onTaskClick,
                onBack = onBack

            )
        }

        composable(
            route = "backup",

        ) {
            BackupScreen(viewModel = backupViewModel)
        }
    }
}
