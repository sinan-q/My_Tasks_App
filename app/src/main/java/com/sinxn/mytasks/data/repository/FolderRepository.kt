package com.sinxn.mytasks.data.repository

import android.util.Log
import com.sinxn.mytasks.data.local.dao.FolderDao
import com.sinxn.mytasks.data.mapper.toDomain
import com.sinxn.mytasks.data.mapper.toEntity
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
    private val folderDao: FolderDao
) : FolderRepositoryInterface {
    override fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders().map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)

    override fun getArchivedFolders(): Flow<List<Folder>> = folderDao.getArchivedFolders().map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)

    override fun getSubFolders(parentId: Long?): Flow<List<Folder>> = folderDao.getSubFolders(parentId).map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)

    override suspend fun insertFolder(folder: Folder): Long = folderDao.insertFolder(folder.toEntity())
    override suspend fun insertFolders(folders: List<Folder>) = folderDao.insertFolders(folders.map { it.toEntity() })
    override suspend fun clearAllFolders() = folderDao.clearAllFolders()

    override suspend fun updateFolder(folder: Folder) = folderDao.updateFolder(folder.toEntity())

    override suspend fun deleteFolder(folder: Folder) = folderDao.deleteFolder(folder.toEntity())

    override suspend fun lockFolder(folderId: Long) = folderDao.lockFolder(folderId)

    override suspend fun getFolderById(folderId: Long): Folder? {
        Log.d("TAG", "getFolderById: $folderId  ")
        if (folderId == 0L) return Folder(name = "Root", folderId = 0L)
        return folderDao.getFolderById(folderId)?.toDomain()
    }

    override suspend fun updateFolderName(folderId: Long, newName: String) = folderDao.updateFolderName(folderId, newName)

    override suspend fun archiveFolder(folderId: Long) = folderDao.archiveFolder(folderId)

    override suspend fun unarchiveFolder(folderId: Long) = folderDao.unarchiveFolder(folderId)

    override suspend fun archiveFolders(folderIds: List<Long>) = folderDao.archiveFolders(folderIds)

    override suspend fun unarchiveFolders(folderIds: List<Long>) = folderDao.unarchiveFolders(folderIds)
}