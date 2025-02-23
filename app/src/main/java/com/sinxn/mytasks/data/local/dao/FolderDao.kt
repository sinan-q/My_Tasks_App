package com.sinxn.mytasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sinxn.mytasks.data.local.entities.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders")
    fun getAllFolders(): Flow<List<Folder>>

    @Insert
    suspend fun insertFolder(folder: Folder): Long

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("SELECT * FROM folders WHERE parentFolderId = :parentId")
    fun getSubFolders(parentId: Long?): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE folderId = :folderId")
    suspend fun getFolderById(folderId: Long): Folder

    // Additional methods as needed
}
