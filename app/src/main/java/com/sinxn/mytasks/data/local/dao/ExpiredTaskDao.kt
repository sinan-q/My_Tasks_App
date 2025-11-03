package com.sinxn.mytasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinxn.mytasks.data.local.entities.ExpiredTask
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpiredTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expiredTask: ExpiredTask)

    @Delete
    suspend fun delete(expiredTask: ExpiredTask)

    @Query("SELECT * FROM expired_tasks WHERE taskId = :taskId")
    suspend fun getExpiredTask(taskId: Long): ExpiredTask?

    @Query("SELECT * FROM expired_tasks")
    fun getAllExpiredTasks(): Flow<List<ExpiredTask>>
}