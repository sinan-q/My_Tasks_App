package com.sinxn.mytasks.domain.usecase.folder

import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface

data class FolderUseCases(
    val getFolders: GetFolders,
    val getArchivedFolders: GetArchivedFolders,
    val deleteFolder: DeleteFolderAndItsContentsUseCase,
    val addFolder: AddFolderUseCase,
    val getFolder: GetFolder,
    val toggleArchive: ToggleFolderArchive,
    val toggleArchives: ToggleFoldersArchive,
    val lockFolder: LockFolderUseCase,
    val getPath: GetPathUseCase,
    val updateFolderName: UpdateFolderName,
    val getSubFolders: GetSubFolders
)

class GetFolders(private val repository: FolderRepositoryInterface) {
    operator fun invoke() = repository.getAllFolders()
}

class GetArchivedFolders(private val repository: FolderRepositoryInterface) {
    operator fun invoke() = repository.getArchivedFolders()
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

class UpdateFolderName(private val repository: FolderRepositoryInterface) {
    suspend operator fun invoke(id: Long, name: String) = repository.updateFolderName(id, name)
}

class GetSubFolders(private val repository: FolderRepositoryInterface) {
    operator fun invoke(parentId: Long) = repository.getSubFolders(parentId)
}
