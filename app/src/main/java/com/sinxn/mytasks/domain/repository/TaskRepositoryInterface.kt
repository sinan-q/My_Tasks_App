package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.domain.models.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TaskRepositoryInterface {
    fun getAllTasks(): Flow<List<Task>>
    fun getArchivedTasks(): Flow<List<Task>>
    fun getAllTasksSorted(): Flow<List<Task>>
    fun getTasksByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Task>>
    fun getTasksWithDueDate(limit: Int = 10): Flow<List<Task>>

    suspend fun insertTask(task: Task): Long
    suspend fun insertTasks(tasks: List<Task>)
    suspend fun deleteTask(task: Task): Int
    suspend fun deleteTasks(tasks: List<Task>)
    suspend fun clearAllTasks()
    suspend fun updateTask(task: Task)
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun updateStatusTask(taskId: Long, status: Boolean)
    fun getTasksByFolderId(folderId: Long?): Flow<List<Task>>
    suspend fun archiveTask(taskId: Long)
    suspend fun unarchiveTask(taskId: Long)
    suspend fun archiveTasks(taskIds: List<Long>)
    suspend fun unarchiveTasks(taskIds: List<Long>)
}