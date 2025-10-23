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
    @Query("SELECT * FROM folders")
    fun getAllFolders(): Flow<List<Folder>>

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

    @Query("SELECT * FROM folders WHERE parentFolderId = :parentId")
    fun getSubFolders(parentId: Long?): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE folderId = :folderId")
    suspend fun getFolderById(folderId: Long): Folder?

    @Query("UPDATE folders SET name = :newName WHERE folderId = :folderId")
    suspend fun updateFolderName(folderId: Long, newName: String)

}
