package com.sinxn.mytasks.data.respository

import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class FakeTaskRepository : TaskRepositoryInterface { // Use this interface if you have one, otherwise match the class
    val tasks = mutableListOf<Task>()
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    override fun getAllTasks(): Flow<List<Task>> = tasksFlow.asStateFlow()

    override fun getAllTasksSorted(): Flow<List<Task>> = tasksFlow.asStateFlow()

    override fun getTasksByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Task>> {
        return flow {
            emit(
                tasks.filter { it.due?.isAfter(startOfMonth) == true && it.due?.isBefore(endOfMonth) == true }
            )
        }
    }

    override suspend fun insertTask(task: Task): Long {
        val newId = (tasks.maxOfOrNull { it.id ?: 0 } ?: 0) + 1
        val newTask = task.copy(id = newId)
        tasks.add(newTask)
        tasksFlow.value = tasks.toList()
        return newId
    }

    override suspend fun deleteTask(task: Task) {
        tasks.removeIf { it.id == task.id }
        tasksFlow.value = tasks.toList()
    }

    override suspend fun updateTask(task: Task) {
        tasks.replaceAll { if (it.id == task.id) task else it }
        tasksFlow.value = tasks.toList()
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return tasks.find { it.id == taskId }
    }

    override suspend fun updateStatusTask(taskId: Long, status: Boolean) {
        val task = tasks.find { it.id == taskId }
        task?.let {
            val updated = it.copy(isCompleted = status)
            updateTask(updated)
        }
    }

    override fun getTasksByFolderId(folderId: Long?): Flow<List<Task>> {
        return flow {
            emit(tasks.filter { it.folderId == folderId })
        }
    }
}
