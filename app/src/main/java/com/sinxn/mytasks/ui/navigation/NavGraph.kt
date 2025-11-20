package com.sinxn.mytasks.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.sinxn.mytasks.ui.navigation.Routes.Backup
import com.sinxn.mytasks.ui.navigation.Routes.Event
import com.sinxn.mytasks.ui.navigation.Routes.Folder
import com.sinxn.mytasks.ui.navigation.Routes.Home
import com.sinxn.mytasks.ui.navigation.Routes.Note
import com.sinxn.mytasks.ui.navigation.Routes.Task
import com.sinxn.mytasks.ui.features.backup.BackupScreen
import com.sinxn.mytasks.ui.features.events.addedit.AddEditEventScreen
import com.sinxn.mytasks.ui.features.events.list.EventListScreen
import com.sinxn.mytasks.ui.features.folders.FolderListScreen
import com.sinxn.mytasks.ui.features.home.HomeScreen
import com.sinxn.mytasks.ui.features.notes.addedit.AddEditNoteScreen
import com.sinxn.mytasks.ui.features.notes.list.NoteListScreen
import com.sinxn.mytasks.ui.features.tasks.addedit.AddEditTaskScreen
import com.sinxn.mytasks.ui.features.tasks.list.TaskListScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(
            route = Home.route,
            deepLinks = listOf(navDeepLink { uriPattern = Home.deepLink })
        ) { HomeScreen(navController = navController) }

        composable(Note.route) { NoteListScreen(navController = navController) }

        composable(
            route = Note.Add.route,
            deepLinks = listOf(navDeepLink { uriPattern = Note.Add.deepLink }),
            arguments = listOf(
                navArgument(Note.noteIdArg) { type = NavType.LongType; defaultValue = -1L },
                navArgument(Note.folderIdArg) { type = NavType.LongType; defaultValue = 0 },
                )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong(Note.noteIdArg) ?: -1L
            val folderId = backStackEntry.arguments?.getLong(Note.folderIdArg) ?: 0
            AddEditNoteScreen(
                noteId = noteId,
                folderId = folderId,
                onFinish = { navController.popBackStack() },
                onNavigateToItem = { id, type ->
                    when (type) {
                        com.sinxn.mytasks.domain.models.RelationItemType.TASK -> navController.navigate(Task.get(id))
                        com.sinxn.mytasks.domain.models.RelationItemType.EVENT -> navController.navigate(Event.get(id))
                        com.sinxn.mytasks.domain.models.RelationItemType.NOTE -> navController.navigate(Note.get(id))
                    }
                }
            )
        }

        composable(Task.route) { TaskListScreen(navController = navController) }
        composable(
            route = Task.Add.route,
            deepLinks = listOf(navDeepLink { uriPattern = Task.Add.deepLink }),
            arguments = listOf(
                navArgument(Task.taskIdArg) { type = NavType.LongType; defaultValue = -1L },
                navArgument(Task.folderIdArg) { type = NavType.LongType; defaultValue = 0 },
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong(Task.taskIdArg) ?: -1L
            val folderId = backStackEntry.arguments?.getLong(Task.folderIdArg) ?: 0
            AddEditTaskScreen(
                taskId = taskId,
                folderId = folderId,
                onFinish = {
                    navController.popBackStack()
                },
                onNavigateToItem = { id, type ->
                    when (type) {
                        com.sinxn.mytasks.domain.models.RelationItemType.TASK -> navController.navigate(Task.get(id))
                        com.sinxn.mytasks.domain.models.RelationItemType.EVENT -> navController.navigate(Event.get(id))
                        com.sinxn.mytasks.domain.models.RelationItemType.NOTE -> navController.navigate(Note.get(id))
                    }
                }
            )
        }
        composable(Event.route) { EventListScreen(navController = navController) }

        composable(
            route = Event.Add.route,
            deepLinks = listOf(navDeepLink { uriPattern = Event.Add.deepLink }),
            arguments = listOf(
                navArgument(Event.eventIdArg) { type = NavType.LongType; defaultValue = -1L },
                navArgument(Event.folderIdArg) { type = NavType.LongType; defaultValue = 0 },
                navArgument(Event.dateArg) { type = NavType.LongType; defaultValue = -1L }
            )
        ){ backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong(Event.eventIdArg) ?: -1L
            val folderId = backStackEntry.arguments?.getLong(Event.folderIdArg) ?: 0
            val date = backStackEntry.arguments?.getLong(Event.dateArg) ?: -1L
            AddEditEventScreen(
                eventId = eventId,
                folderId = folderId,
                date = date,
                onFinish = {
                    navController.popBackStack()
                },
                onNavigateToItem = { id, type ->
                    when (type) {
                        com.sinxn.mytasks.domain.models.RelationItemType.TASK -> navController.navigate(Task.get(id))
                        com.sinxn.mytasks.domain.models.RelationItemType.EVENT -> navController.navigate(Event.get(id))
                        com.sinxn.mytasks.domain.models.RelationItemType.NOTE -> navController.navigate(Note.get(id))
                    }
                }
            )
        }

        composable(
            route = Folder.route,
            arguments = listOf(
                navArgument(Folder.folerIdArg) {type = NavType.LongType; defaultValue = 0},
            )
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getLong(Folder.folerIdArg) ?: 0
            FolderListScreen(
                folderId = folderId,
                navController = navController,
            )
        }

        composable(route = Backup.route) { BackupScreen() }
    }
}
