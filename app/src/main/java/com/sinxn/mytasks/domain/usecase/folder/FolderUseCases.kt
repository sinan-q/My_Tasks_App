package com.sinxn.mytasks.domain.usecase.folder

import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface

data class FolderUseCases(
    val getFolders: GetFolders,
    val getArchivedFolders: GetArchivedFolders,
    val deleteFolder: DeleteFolder,
    val addFolder: AddFolder,
    val getFolder: GetFolder,
    val toggleArchive: ToggleFolderArchive,
    val toggleArchives: ToggleFoldersArchive,
)

class GetFolders(private val repository: FolderRepositoryInterface) {
    operator fun invoke() = repository.getAllFolders()
}

class GetArchivedFolders(private val repository: FolderRepositoryInterface) {
    operator fun invoke() = repository.getArchivedFolders()
}

class DeleteFolder(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(folder: Folder) = repository.deleteFolder(folder)
}

class AddFolder(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(folder: Folder) = repository.insertFolder(folder)
}

class GetFolder(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.getFolderById(id)
}

class ToggleFolderArchive(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(id: Long, archive: Boolean) {
        if (archive) repository.archiveFolder(id) else repository.unarchiveFolder(id)
    }
}

class ToggleFoldersArchive(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>, archive: Boolean) {
        if (ids.isEmpty()) return
        if (archive) repository.archiveFolders(ids) else repository.unarchiveFolders(ids)
    }
}
