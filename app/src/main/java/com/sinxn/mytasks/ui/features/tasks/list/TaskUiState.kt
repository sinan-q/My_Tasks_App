package com.sinxn.mytasks.ui.features.tasks.list

import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.ui.features.tasks.addedit.ReminderModel
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class TaskUiState(
    val id: Long? = null,
    val folderId: Long = 1,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val due: LocalDateTime? = null,
    val recurrenceRule: String? = null
)

data class TasksListUiState(
    val tasks: List<TaskListItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class TaskScreenUiState(
    val task: TaskUiState = TaskUiState(),
    val reminders: List<ReminderModel> = emptyList(),
    val folder: Folder? = null, // Will be replaced with FolderUiState
    val folders: List<Folder> = emptyList(), // Will be replaced with FolderUiState
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
