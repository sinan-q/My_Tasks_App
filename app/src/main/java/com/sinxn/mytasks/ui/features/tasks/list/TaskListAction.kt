package com.sinxn.mytasks.ui.features.tasks.list

import com.sinxn.mytasks.core.SelectionAction

sealed class TaskListAction {
    data class UpdateStatusTask(val taskId: Long, val status: Boolean) : TaskListAction()
    data class OnSelectionAction(val action: SelectionAction) : TaskListAction()
}
