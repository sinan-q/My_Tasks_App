package com.sinxn.mytasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sinxn.mytasks.data.local.entities.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders WHERE isArchived = 0")
    fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE isArchived = 1")
    fun getArchivedFolders(): Flow<List<Folder>>

    @Insert
    suspend fun insertFolder(folder: Folder): Long

    @Insert
    suspend fun insertFolders(folders: List<Folder>)

    @Query("DELETE FROM folders")
    suspend fun clearAllFolders()

    @Update
    suspend fun updateFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("UPDATE folders SET isLocked = NOT isLocked WHERE folderId = :folderId")
    suspend fun lockFolder(folderId: Long)

    @Query("SELECT * FROM folders WHERE parentFolderId = :parentId AND isArchived = 0")
    fun getSubFolders(parentId: Long?): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE folderId = :folderId AND isArchived = 0")
    suspend fun getFolderById(folderId: Long): Folder?

    @Query("UPDATE folders SET name = :newName WHERE folderId = :folderId")
    suspend fun updateFolderName(folderId: Long, newName: String)

    @Query("UPDATE folders SET isArchived = 1 WHERE folderId = :folderId")
    suspend fun archiveFolder(folderId: Long)

    @Query("UPDATE folders SET isArchived = 0 WHERE folderId = :folderId")
    suspend fun unarchiveFolder(folderId: Long)

    @Query("UPDATE folders SET isArchived = 1 WHERE folderId IN (:folderIds)")
    suspend fun archiveFolders(folderIds: List<Long>)

    @Query("UPDATE folders SET isArchived = 0 WHERE folderId IN (:folderIds)")
    suspend fun unarchiveFolders(folderIds: List<Long>)
}
