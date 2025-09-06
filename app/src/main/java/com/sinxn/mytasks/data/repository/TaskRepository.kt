package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.dao.TaskDao
import com.sinxn.mytasks.data.local.entities.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepositoryInterface {
    override fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    override fun getAllTasksSorted(): Flow<List<Task>> = taskDao.getAllTasksSorted()
    override fun getPendingTasks(limit: Int): Flow<List<Task>> {
        return taskDao.getPendingTasks(limit)
    }
    override fun getTasksByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Task>> {
        return taskDao.getTasksByMonth(startOfMonth, endOfMonth)
    }
    override suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    override suspend fun insertTasks(tasks: List<Task>) = taskDao.insertTasks(tasks)

    override suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    override suspend fun deleteTasks(tasks: List<Task>) = taskDao.deleteTasks(tasks)
    override suspend fun clearAllTasks() = taskDao.clearAllTasks()


    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)
    }

    override suspend fun updateStatusTask(taskId: Long, status: Boolean) {
        taskDao.updateStatusTask(taskId, status)
    }

    override fun getTasksByFolderId(folderId: Long?): Flow<List<Task>> {
        return taskDao.getTasksByFolderId(folderId)
    }
}
