package com.sinxn.mytasks.data.interfaces

import com.sinxn.mytasks.data.local.entities.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepositoryInterface {
    fun getAllFolders(): Flow<List<Folder>>
    fun getSubFolders(parentId: Long?): Flow<List<Folder>>
    suspend fun insertFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
    suspend fun lockFolder(folder: Folder)
    suspend fun getFolderById(folderId: Long): Folder
}
