package com.sinxn.mytasks.ui.features.folders

import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.data.local.entities.Folder

sealed class FolderAction {
    data class AddFolder(val folder: Folder) : FolderAction()
    data class DeleteFolder(val folder: Folder) : FolderAction()
    data class LockFolder(val folder: Folder) : FolderAction()
    data class UpdateFolderName(val folderId: Long, val newName: String) : FolderAction()
    data class GetSubFolders(val folderId: Long) : FolderAction()
    data class UpdateTaskStatus(val taskId: Long, val status: Boolean) : FolderAction()
    data class OnSelectionAction(val action: SelectionAction) : FolderAction()
}
