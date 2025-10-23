package com.sinxn.mytasks.ui.features.home

import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.features.events.EventListItemUiModel
import com.sinxn.mytasks.ui.features.events.toListItemUiModel
import com.sinxn.mytasks.ui.features.folders.FolderListItemUiModel
import com.sinxn.mytasks.ui.features.folders.toListItemUiModel
import com.sinxn.mytasks.ui.features.notes.NoteListItemUiModel
import com.sinxn.mytasks.ui.features.notes.toListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.TaskListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.toListItemUiModel

/**
 * A UI-specific model representing the home screen.
 * This class is tailored for the UI and contains only the data needed for rendering the screen.
 */
data class HomeUiModel(
    val parentFolder: Folder?,
    val folders: List<FolderListItemUiModel>,
    val upcomingEvents: List<EventListItemUiModel>,
    val pendingTasks: List<TaskListItemUiModel>,
    val notes: List<NoteListItemUiModel>,
    val tasks: List<TaskListItemUiModel>
)
