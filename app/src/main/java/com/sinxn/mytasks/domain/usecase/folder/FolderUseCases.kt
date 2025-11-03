package com.sinxn.mytasks.domain.usecase.folder

import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface

data class FolderUseCases(
    val getFolders: GetFolders,
    val getArchivedFolders: GetArchivedFolders,
    val deleteFolder: DeleteFolder,
    val addFolder: AddFolder,
    val getFolder: GetFolder,
    val archiveFolder: ArchiveFolder,
    val unarchiveFolder: UnarchiveFolder,
    val archiveFolders: ArchiveFolders,
    val unarchiveFolders: UnarchiveFolders,
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

class ArchiveFolder(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.archiveFolder(id)
}

class UnarchiveFolder(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.unarchiveFolder(id)
}

class ArchiveFolders(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>) = repository.archiveFolders(ids)
}

class UnarchiveFolders(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>) = repository.unarchiveFolders(ids)
}
