package com.sinxn.mytasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sinxn.mytasks.data.local.entities.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isArchived = 0")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isArchived = 1")
    fun getArchivedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isArchived = 0 ORDER BY isCompleted = true, due DESC, timestamp DESC")
    fun getAllTasksSorted(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isArchived = 0 AND due BETWEEN :startOfMonth AND :endOfMonth ORDER BY due ASC")
    fun getTasksByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Task>>
    @Query("SELECT * FROM tasks WHERE isArchived = 0 AND isCompleted = 0 AND due != 0 ORDER BY due ASC LIMIT :limit ")
    fun getTasksWithDueDate(limit: Int = 10): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTasks(tasks: List<Task>)

    @Delete
    suspend fun deleteTask(task: Task): Int

    @Delete
    suspend fun deleteTasks(tasks: List<Task>)

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId AND isArchived = 0 LIMIT 1")
    suspend fun getTaskById(taskId: Long): Task?

    @Query("UPDATE tasks SET isCompleted = :status WHERE id = :taskId")
    suspend fun updateStatusTask(taskId: Long, status: Boolean)

    @Query("SELECT * FROM tasks WHERE folderId = :folderId AND isArchived = 0 ORDER BY isCompleted = true, due DESC, timestamp DESC")
    fun getTasksByFolderId(folderId: Long?): Flow<List<Task>>

    @Query("UPDATE tasks SET isArchived = 1 WHERE id = :taskId")
    suspend fun archiveTask(taskId: Long)

    @Query("UPDATE tasks SET isArchived = 0 WHERE id = :taskId")
    suspend fun unarchiveTask(taskId: Long)

    @Query("UPDATE tasks SET isArchived = 1 WHERE id IN (:taskIds)")
    suspend fun archiveTasks(taskIds: List<Long>)

    @Query("UPDATE tasks SET isArchived = 0 WHERE id IN (:taskIds)")
    suspend fun unarchiveTasks(taskIds: List<Long>)
}
