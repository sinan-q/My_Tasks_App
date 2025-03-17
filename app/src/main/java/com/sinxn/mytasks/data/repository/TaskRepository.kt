package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.TaskDao
import com.sinxn.mytasks.data.local.entities.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getAllTasksSorted(): Flow<List<Task>> = taskDao.getAllTasksSorted()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)
    }

    suspend fun updateStatusTask(taskId: Long, status: Boolean) {
        taskDao.updateStatusTask(taskId, status)
    }

    fun getTasksByFolderId(folderId: Long?): Flow<List<Task>> {
        return taskDao.getTasksByFolderId(folderId)
    }
}
