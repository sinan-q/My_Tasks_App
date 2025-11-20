package com.sinxn.mytasks.ui.features.tasks.addedit


import com.sinxn.mytasks.ui.features.tasks.addedit.ReminderModel
import com.sinxn.mytasks.ui.features.tasks.list.TaskUiState
import java.time.temporal.ChronoUnit

sealed class AddEditTaskAction {
    data class UpdateTask(val task: TaskUiState) : AddEditTaskAction()
    data class InsertTask(val task: TaskUiState, val reminders: List<ReminderModel>) : AddEditTaskAction()
    data class DeleteTask(val task: TaskUiState) : AddEditTaskAction()
    data class FetchTaskById(val taskId: Long) : AddEditTaskAction()
    data class FetchFolderById(val folderId: Long) : AddEditTaskAction()
    data class AddReminder(val reminder: ReminderModel) : AddEditTaskAction()
    data class RemoveReminder(val reminder: ReminderModel) : AddEditTaskAction()


}