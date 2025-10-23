package com.sinxn.mytasks.data.usecase.folder

import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteFolderAndItsContentsUseCase @Inject constructor(
    private val folderRepository: FolderRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface
) {
    suspend operator fun invoke(folder: Folder) {
        // Recursive deletion logic...
        val subfolders = folderRepository.getSubFolders(folder.folderId)
            .first() // Use .first() if you expect the flow to emit once and complete or you need the current list
        subfolders.forEach { subfolder -> invoke(subfolder) }

        val noteList = noteRepository.getNotesByFolderId(folder.folderId).first()
        noteList.forEach { note -> noteRepository.deleteNote(note) }

        val tasksInFolder = taskRepository.getTasksByFolderId(folder.folderId).first()
        tasksInFolder.forEach { task -> taskRepository.deleteTask(task) }

        folderRepository.deleteFolder(folder)
    }
}