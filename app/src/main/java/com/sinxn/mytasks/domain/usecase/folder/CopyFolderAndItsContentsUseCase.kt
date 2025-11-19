package com.sinxn.mytasks.domain.usecase.folder

import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CopyFolderAndItsContentsUseCase @Inject constructor(
    private val folderRepository: FolderRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface
) {
    suspend operator fun invoke(folder: Folder, parentId: Long? = 0L) {
        val newFolder = folderRepository.insertFolder(folder.copy(folderId = 0L, parentFolderId = parentId))

        val noteList = noteRepository.getNotesByFolderId(folder.folderId).first()
        noteList.forEach { note -> noteRepository.insertNote(note.copy(id = null, folderId = newFolder)) }

        val tasksInFolder = taskRepository.getTasksByFolderId(folder.folderId).first()
        tasksInFolder.forEach { task -> taskRepository.insertTask(task.copy(id = null, folderId = newFolder)) }

        val subfolders = folderRepository.getSubFolders(folder.folderId).first()
        subfolders.forEach { subfolder -> invoke(subfolder, newFolder) }
    }
}