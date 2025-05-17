package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.local.dao.FolderDao
import com.sinxn.mytasks.data.local.entities.Folder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
    private val folderDao: FolderDao
) : FolderRepositoryInterface {
    override fun getAllFolders(): Flow<List<Folder>> {
        return folderDao.getAllFolders()
    }

    override fun getSubFolders(parentId: Long?): Flow<List<Folder>> {
        return folderDao.getSubFolders(parentId)
    }
    override suspend fun insertFolder(folder: Folder) {
        folderDao.insertFolder(folder)
    }
    override suspend fun deleteFolder(folder: Folder) {
        folderDao.deleteFolder(folder)
    }

    override suspend fun lockFolder(folder: Folder) {
        folderDao.lockFolder(folder.folderId)
    }

    override suspend fun getFolderById(folderId: Long): Folder {
        if (folderId == 0L) return Folder(name = "Root", folderId = 0L)
        return folderDao.getFolderById(folderId)
    }

}