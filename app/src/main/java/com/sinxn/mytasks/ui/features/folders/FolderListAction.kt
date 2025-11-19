package com.sinxn.mytasks.ui.features.folders

import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.domain.models.Folder

sealed class FolderListAction {
    data class AddFolderList(val folder: Folder) : FolderListAction()
    data class DeleteFolderList(val folder: Folder) : FolderListAction()
    data class LockFolderList(val folder: Folder) : FolderListAction()
    data class UpdateFolderListName(val folderId: Long, val newName: String) : FolderListAction()
    data class GetSubFolders(val folderId: Long) : FolderListAction()
    data class UpdateTaskStatus(val taskId: Long, val status: Boolean) : FolderListAction()
    data class OnSelectionListAction(val action: SelectionAction) : FolderListAction()
}
