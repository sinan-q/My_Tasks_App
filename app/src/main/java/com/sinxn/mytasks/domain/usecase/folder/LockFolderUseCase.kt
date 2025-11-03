package com.sinxn.mytasks.domain.usecase.folder

import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import javax.inject.Inject

class LockFolderUseCase @Inject constructor(private val folderRepository: FolderRepositoryInterface) {
    suspend operator fun invoke(folder: Folder, isLocked: Boolean) {
        // Logic to update the folder's lock status
        // This might just be updating a field, or could involve more complex operations
        folderRepository.lockFolder(folder.folderId) // Example
    }
}