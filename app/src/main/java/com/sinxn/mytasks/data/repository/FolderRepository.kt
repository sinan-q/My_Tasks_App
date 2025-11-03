package com.sinxn.mytasks.data.repository

import android.util.Log
import com.sinxn.mytasks.data.local.dao.FolderDao
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
    private val folderDao: FolderDao
) : FolderRepositoryInterface {
    override fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders()

    override fun getArchivedFolders(): Flow<List<Folder>> = folderDao.getArchivedFolders()

    override fun getSubFolders(parentId: Long?): Flow<List<Folder>> = folderDao.getSubFolders(parentId)

    override suspend fun insertFolder(folder: Folder): Long = folderDao.insertFolder(folder)
    override suspend fun insertFolders(folders: List<Folder>) = folderDao.insertFolders(folders)
    override suspend fun clearAllFolders() = folderDao.clearAllFolders()

    override suspend fun updateFolder(folder: Folder) = folderDao.updateFolder(folder)

    override suspend fun deleteFolder(folder: Folder) = folderDao.deleteFolder(folder)

    override suspend fun lockFolder(folderId: Long) = folderDao.lockFolder(folderId)

    override suspend fun getFolderById(folderId: Long): Folder? {
        Log.d("TAG", "getFolderById: $folderId  ")
        if (folderId == 0L) return Folder(name = "Root", folderId = 0L)
        return folderDao.getFolderById(folderId)
    }

    override suspend fun updateFolderName(folderId: Long, newName: String) = folderDao.updateFolderName(folderId, newName)

    override suspend fun archiveFolder(folderId: Long) = folderDao.archiveFolder(folderId)

    override suspend fun unarchiveFolder(folderId: Long) = folderDao.unarchiveFolder(folderId)

    override suspend fun archiveFolders(folderIds: List<Long>) = folderDao.archiveFolders(folderIds)

    override suspend fun unarchiveFolders(folderIds: List<Long>) = folderDao.unarchiveFolders(folderIds)
}