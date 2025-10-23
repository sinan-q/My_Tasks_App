package com.sinxn.mytasks.domain.usecase.selection

import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
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
        action: SelectionActions,
        selectedTasks: Set<Task>,
        selectedNotes: Set<Note>,
        selectedFolders: Set<Folder>,
        destinationFolderId: Long
    ) {
        if (action == SelectionActions.COPY) {
            selectedFolders.forEach {
                copyFolderAndItsContentsUseCase(it, parentId = destinationFolderId)
            }
            selectedTasks.forEach {
                taskRepository.insertTask(it.copy(id = null, folderId = destinationFolderId))
            }
            selectedNotes.forEach {
                noteRepository.insertNote(it.copy(id = null, folderId = destinationFolderId))
            }
        } else if (action == SelectionActions.CUT) {
            selectedTasks.forEach {
                taskRepository.updateTask(it.copy(folderId = destinationFolderId))
            }
            selectedNotes.forEach {
                noteRepository.updateNote(it.copy(folderId = destinationFolderId))
            }
            selectedFolders.forEach {
                folderRepository.updateFolder(it.copy(parentFolderId = destinationFolderId))
            }
        }
    }
}
