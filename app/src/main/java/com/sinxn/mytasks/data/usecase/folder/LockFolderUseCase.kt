package com.sinxn.mytasks.data.usecase.folder

import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import javax.inject.Inject

class LockFolderUseCase @Inject constructor(private val folderRepository: FolderRepositoryInterface) {
    suspend operator fun invoke(folder: Folder, isLocked: Boolean) {
        // Logic to update the folder's lock status
        // This might just be updating a field, or could involve more complex operations
        folderRepository.lockFolder(folder) // Example
    }
}