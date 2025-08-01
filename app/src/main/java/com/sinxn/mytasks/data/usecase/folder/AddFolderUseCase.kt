package com.sinxn.mytasks.data.usecase.folder

import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import javax.inject.Inject
import kotlin.text.isBlank

class AddFolderUseCase @Inject constructor(private val folderRepository: FolderRepositoryInterface) {
    suspend operator fun invoke(folder: Folder) {
        // Potentially add validation logic here, e.g., check name uniqueness at this level
        if (folder.name.isBlank()) throw IllegalArgumentException("Folder name cannot be blank.")
        folderRepository.insertFolder(folder)
    }
}