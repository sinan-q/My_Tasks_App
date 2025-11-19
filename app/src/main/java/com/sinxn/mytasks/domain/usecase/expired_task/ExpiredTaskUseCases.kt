package com.sinxn.mytasks.domain.usecase.expired_task

import com.sinxn.mytasks.domain.models.ExpiredTask
import com.sinxn.mytasks.domain.repository.ExpiredTaskRepositoryInterface
import kotlinx.coroutines.flow.Flow

class GetExpiredTasks(private val repository: ExpiredTaskRepositoryInterface) {
    operator fun invoke(): Flow<List<ExpiredTask>> {
        return repository.getAllExpiredTasks()
    }
}

class GetExpiredTask(private val repository: ExpiredTaskRepositoryInterface) {
    suspend operator fun invoke(taskId: Long): ExpiredTask? {
        return repository.getExpiredTask(taskId)
    }
}

class InsertExpiredTask(private val repository: ExpiredTaskRepositoryInterface) {
    suspend operator fun invoke(expiredTask: ExpiredTask) {
        repository.insert(expiredTask)
    }
}

class DeleteExpiredTask(private val repository: ExpiredTaskRepositoryInterface) {
    suspend operator fun invoke(expiredTask: ExpiredTask) {
        repository.delete(expiredTask)
    }
}

data class ExpiredTaskUseCases(
    val getExpiredTasks: GetExpiredTasks,
    val getExpiredTask: GetExpiredTask,
    val insertExpiredTask: InsertExpiredTask,
    val deleteExpiredTask: DeleteExpiredTask
)
