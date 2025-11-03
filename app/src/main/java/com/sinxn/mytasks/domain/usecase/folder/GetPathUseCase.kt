package com.sinxn.mytasks.domain.usecase.folder

import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetPathUseCase @Inject constructor(
    private val folderRepository: FolderRepositoryInterface
) {
    suspend operator fun invoke(folderId: Long, hideLocked: Boolean): String? {
        val allFolders = folderRepository.getAllFolders().first()
        val path = StringBuilder("/")
        var curr = folderId
        while (curr != 0L) {
            val folder = allFolders.find { it.folderId == curr } ?: folderRepository.getFolderById(curr)
            path.insert(0, folder?.name)
            path.insert(0, "/")
            curr = folder?.parentFolderId ?: 0L
            if (folder?.isLocked == true && hideLocked) return null
        }
        return path.toString()
    }
}