package com.sinxn.mytasks.data.local.dao

import androidx.room.*
import com.sinxn.mytasks.data.local.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): Task?

    @Query("UPDATE tasks SET isCompleted = :status WHERE id = :taskId")
    suspend fun updateStatusTask(taskId: Long, status: Boolean)
}
