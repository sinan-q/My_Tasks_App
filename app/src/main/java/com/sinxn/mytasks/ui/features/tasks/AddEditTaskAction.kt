package com.sinxn.mytasks.ui.features.tasks

import java.time.temporal.ChronoUnit

sealed class AddEditTaskAction {
    data class UpdateTask(val task: TaskUiState) : AddEditTaskAction()
    data class InsertTask(val task: TaskUiState, val reminders: List<Pair<Int, ChronoUnit>>) : AddEditTaskAction()
    data class DeleteTask(val task: TaskUiState) : AddEditTaskAction()
    data class FetchTaskById(val taskId: Long) : AddEditTaskAction()
    data class FetchFolderById(val folderId: Long) : AddEditTaskAction()
    data class AddReminder(val reminder: Pair<Int, ChronoUnit>) : AddEditTaskAction()
    data class RemoveReminder(val reminder: Pair<Int, ChronoUnit>) : AddEditTaskAction()
    data class UpdateStatusTask(val taskId: Long, val status: Boolean) : AddEditTaskAction()
}
