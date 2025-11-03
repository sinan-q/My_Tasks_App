package com.sinxn.mytasks.domain.usecase.task

import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface

data class TaskUseCases(
    val getTasks: GetTasks,
    val getArchivedTasks: GetArchivedTasks,
    val deleteTask: DeleteTask,
    val addTask: AddTask,
    val getTask: GetTask,
    val archiveTask: ArchiveTask,
    val unarchiveTask: UnarchiveTask,
    val archiveTasks: ArchiveTasks,
    val unarchiveTasks: UnarchiveTasks,
)

class GetTasks(private val repository: TaskRepositoryInterface) {
    operator fun invoke() = repository.getAllTasks()
}

class GetArchivedTasks(private val repository: TaskRepositoryInterface) {
    operator fun invoke() = repository.getArchivedTasks()
}

class DeleteTask(private val repository: TaskRepositoryInterface) {
    suspend operator fun invoke(task: Task) = repository.deleteTask(task)
}

class AddTask(private val repository: TaskRepositoryInterface) {
    suspend operator fun invoke(task: Task) = repository.insertTask(task)
}

class GetTask(private val repository: TaskRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.getTaskById(id)
}

class ArchiveTask(private val repository: TaskRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.archiveTask(id)
}

class UnarchiveTask(private val repository: TaskRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.unarchiveTask(id)
}

class ArchiveTasks(private val repository: TaskRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>) = repository.archiveTasks(ids)
}

class UnarchiveTasks(private val repository: TaskRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>) = repository.unarchiveTasks(ids)
}
