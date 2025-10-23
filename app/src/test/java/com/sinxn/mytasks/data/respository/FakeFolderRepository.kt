package com.sinxn.mytasks.data.respository

import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeFolderRepository : FolderRepositoryInterface {

    private val folderList = mutableListOf<Folder>()
    private val folderFlow = MutableStateFlow<List<Folder>>(emptyList())

    override fun getAllFolders(): Flow<List<Folder>> {
        return folderFlow.asStateFlow()
    }

    override fun getSubFolders(parentId: Long?): Flow<List<Folder>> {
        return MutableStateFlow(folderList.filter { it.parentFolderId == parentId }).asStateFlow()
    }

    override suspend fun insertFolder(folder: Folder) {
        folderList.add(folder)
        folderFlow.value = folderList
    }

    override suspend fun deleteFolder(folder: Folder) {
        folderList.remove(folder)
        folderFlow.value = folderList
    }

    override suspend fun lockFolder(folder: Folder) {
//        folderList.find { it.folderId == folder.folderId }?.let {
//            it.isLocked = true
//            folderFlow.value = folderList
//        }
    }

    override suspend fun getFolderById(folderId: Long): Folder {
        return folderList.find { it.folderId == folderId }
            ?: if (folderId == 0L) Folder(name = "Root", folderId = 0L)
            else throw IllegalArgumentException("Folder not found")
    }
}