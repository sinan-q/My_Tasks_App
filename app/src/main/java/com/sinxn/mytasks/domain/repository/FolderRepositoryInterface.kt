package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.data.local.entities.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepositoryInterface {
    fun getAllFolders(): Flow<List<Folder>>
    fun getArchivedFolders(): Flow<List<Folder>>
    fun getSubFolders(parentId: Long?): Flow<List<Folder>>
    suspend fun insertFolder(folder: Folder): Long
    suspend fun insertFolders(folders: List<Folder>)
    suspend fun clearAllFolders()
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
    suspend fun lockFolder(folderId: Long)
    suspend fun getFolderById(folderId: Long): Folder?
    suspend fun updateFolderName(folderId: Long, newName: String)
    suspend fun archiveFolder(folderId: Long)
    suspend fun unarchiveFolder(folderId: Long)
    suspend fun archiveFolders(folderIds: List<Long>)
    suspend fun unarchiveFolders(folderIds: List<Long>)
}
