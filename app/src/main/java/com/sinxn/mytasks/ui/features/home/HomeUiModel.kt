package com.sinxn.mytasks.ui.features.home

import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.ui.features.events.EventListItemUiModel
import com.sinxn.mytasks.ui.features.folders.FolderListItemUiModel
import com.sinxn.mytasks.ui.features.notes.list.NoteListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.TaskListItemUiModel

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
    val tasks: List<TaskListItemUiModel>,
    val pinnedItems: List<Any>
)
