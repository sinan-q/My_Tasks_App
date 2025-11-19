package com.sinxn.mytasks.domain.usecase.selection

import com.sinxn.mytasks.core.SelectedItems
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.domain.usecase.folder.CopyFolderAndItsContentsUseCase
import javax.inject.Inject

class PasteSelectionUseCase @Inject constructor(
    private val taskRepository: TaskRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
    private val copyFolderAndItsContentsUseCase: CopyFolderAndItsContentsUseCase
) {
    suspend operator fun invoke(
        action: SelectionAction,
        selectedItems: SelectedItems,
        destinationFolderId: Long
    ) {
        if (action == SelectionAction.Copy) {
            selectedItems.folders.forEach {
                copyFolderAndItsContentsUseCase(it, parentId = destinationFolderId)
            }
            selectedItems.tasks.forEach {
                taskRepository.insertTask(it.copy(id = null, folderId = destinationFolderId))
            }
            selectedItems.notes.forEach {
                noteRepository.insertNote(it.copy(id = null, folderId = destinationFolderId))
            }
        } else if (action == SelectionAction.Cut) {
            selectedItems.tasks.forEach {
                taskRepository.updateTask(it.copy(folderId = destinationFolderId))
            }
            selectedItems.notes.forEach {
                noteRepository.updateNote(it.copy(folderId = destinationFolderId))
            }
            selectedItems.folders.forEach {
                folderRepository.updateFolder(it.copy(parentFolderId = destinationFolderId))
            }
        }
    }
}
