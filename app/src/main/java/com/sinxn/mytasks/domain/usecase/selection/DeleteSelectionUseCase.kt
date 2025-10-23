package com.sinxn.mytasks.domain.usecase.selection

import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.domain.usecase.folder.DeleteFolderAndItsContentsUseCase
import javax.inject.Inject

class DeleteSelectionUseCase @Inject constructor(
    private val taskRepository: TaskRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val deleteFolderAndItsContentsUseCase: DeleteFolderAndItsContentsUseCase
) {
    suspend operator fun invoke(
        selectedTasks: Set<Task>,
        selectedNotes: Set<Note>,
        selectedFolders: Set<Folder>
    ) {
        taskRepository.deleteTasks(selectedTasks.toList())
        noteRepository.deleteNotes(selectedNotes.toList())
        selectedFolders.forEach {
            deleteFolderAndItsContentsUseCase(it)
        }
    }
}
