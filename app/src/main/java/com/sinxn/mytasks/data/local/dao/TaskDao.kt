package com.sinxn.mytasks.data.local.dao

import androidx.room.*
import com.sinxn.mytasks.data.local.entities.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY isCompleted = true, due ASC, timestamp DESC")
    fun getAllTasksSorted(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE due BETWEEN :startOfMonth AND :endOfMonth ORDER BY due ASC")
    fun getTasksByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): Task?

    @Query("UPDATE tasks SET isCompleted = :status WHERE id = :taskId")
    suspend fun updateStatusTask(taskId: Long, status: Boolean)

    @Query("SELECT * FROM tasks WHERE folderId = :folderId ORDER BY isCompleted = true, due ASC, timestamp DESC")
    fun getTasksByFolderId(folderId: Long?): Flow<List<Task>>
}
