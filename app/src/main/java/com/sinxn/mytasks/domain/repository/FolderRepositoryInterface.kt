package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.data.local.entities.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepositoryInterface {
    suspend fun getAllFolders(): Flow<List<Folder>>
    fun getSubFolders(parentId: Long?): Flow<List<Folder>>
    suspend fun insertFolder(folder: Folder): Long
    suspend fun insertFolders(folders: List<Folder>)
    suspend fun clearAllFolders()
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
    suspend fun lockFolder(folder: Folder)
    suspend fun getFolderById(folderId: Long): Folder
    suspend fun updateFolderName(folderId: Long, newName: String)
}
