package com.sinxn.mytasks.domain.usecase.task

import com.sinxn.mytasks.domain.models.Task
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface

data class TaskUseCases(
    val getTasks: GetTasks,
    val getArchivedTasks: GetArchivedTasks,
    val deleteTask: DeleteTask,
    val addTask: AddTask,
    val getTask: GetTask,
    val toggleArchive: ToggleTaskArchive,
    val toggleArchives: ToggleTasksArchive,
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

class ToggleTaskArchive(private val repository: TaskRepositoryInterface) {
    suspend operator fun invoke(id: Long, archive: Boolean) {
        if (archive) repository.archiveTask(id) else repository.unarchiveTask(id)
    }
}

class ToggleTasksArchive(private val repository: TaskRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>, archive: Boolean) {
        if (ids.isEmpty()) return
        if (archive) repository.archiveTasks(ids) else repository.unarchiveTasks(ids)
    }
}
