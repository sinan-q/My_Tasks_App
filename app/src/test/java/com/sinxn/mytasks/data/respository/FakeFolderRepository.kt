package com.sinxn.mytasks.data.respository

import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.models.Folder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class FakeFolderRepository : FolderRepositoryInterface {

    private val folderList = mutableListOf<Folder>()
    private val folderFlow = MutableStateFlow<List<Folder>>(emptyList())

    override fun getAllFolders(): Flow<List<Folder>> {
        return folderFlow.asStateFlow()
    }

    override fun getSubFolders(parentId: Long?): Flow<List<Folder>> {
        return MutableStateFlow(folderList.filter { it.parentFolderId == parentId }).asStateFlow()
    }

    override suspend fun insertFolder(folder: Folder): Long {
        val newId = (folderList.maxOfOrNull { it.folderId } ?: 0) + 1
        val newFolder = folder.copy(folderId = newId)
        folderList.add(newFolder)
        folderFlow.value = folderList
        return newId
    }

    override suspend fun deleteFolder(folder: Folder) {
        folderList.remove(folder)
        folderFlow.value = folderList
    }

    override suspend fun lockFolder(folderId: Long) {
        folderList.find { it.folderId == folderId }?.let {
            updateFolder(it.copy(isLocked = !it.isLocked))
        }
    }

    override suspend fun getFolderById(folderId: Long): Folder? {
        return folderList.find { it.folderId == folderId }
            ?: if (folderId == 0L) Folder(name = "Root", folderId = 0L)
            else null
    }

    override fun getArchivedFolders(): Flow<List<Folder>> = flow {
        emit(folderList.filter { it.isArchived })
    }

    override suspend fun insertFolders(folders: List<Folder>) {
        folders.forEach { insertFolder(it) }
    }

    override suspend fun clearAllFolders() {
        folderList.clear()
        folderFlow.value = emptyList()
    }

    override suspend fun updateFolder(folder: Folder) {
        folderList.replaceAll { if (it.folderId == folder.folderId) folder else it }
        folderFlow.value = folderList
    }

    override suspend fun updateFolderName(folderId: Long, newName: String) {
        folderList.find { it.folderId == folderId }?.let {
            updateFolder(it.copy(name = newName))
        }
    }

    override suspend fun archiveFolder(folderId: Long) {
        folderList.find { it.folderId == folderId }?.let {
            updateFolder(it.copy(isArchived = true))
        }
    }

    override suspend fun unarchiveFolder(folderId: Long) {
        folderList.find { it.folderId == folderId }?.let {
            updateFolder(it.copy(isArchived = false))
        }
    }

    override suspend fun archiveFolders(folderIds: List<Long>) {
        folderIds.forEach { archiveFolder(it) }
    }

    override suspend fun unarchiveFolders(folderIds: List<Long>) {
        folderIds.forEach { unarchiveFolder(it) }
    }
}