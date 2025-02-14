package com.sinxn.mytasks.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sinxn.mytasks.ui.screens.AddEditNoteScreen
import com.sinxn.mytasks.ui.screens.NoteListScreen
import com.sinxn.mytasks.ui.screens.viewmodel.NoteViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "note_list",
        modifier = modifier
    ) {
        composable("note_list") {
            NoteListScreen(
                notes = noteViewModel.notes.collectAsState().value,
                onAddNoteClick = { navController.navigate("add_edit_note/-1L") },
                onNoteClick = { noteId ->
                    navController.navigate("add_edit_note/$noteId")
                }
            )
        }
        composable(
            route = "add_edit_note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            AddEditNoteScreen(
                noteId = noteId,
                onSaveNote = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                noteViewModel = noteViewModel,
                modifier = Modifier
            )
        }
    }
}
