package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.ExpiredTaskDao
import com.sinxn.mytasks.data.local.entities.ExpiredTask
import com.sinxn.mytasks.domain.repository.ExpiredTaskRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpiredTaskRepository @Inject constructor(
    private val dao: ExpiredTaskDao
) : ExpiredTaskRepositoryInterface {

    override suspend fun insert(expiredTask: ExpiredTask) {
        dao.insert(expiredTask)
    }

    override suspend fun delete(expiredTask: ExpiredTask) {
        dao.delete(expiredTask)
    }

    override suspend fun getExpiredTask(taskId: Long): ExpiredTask? {
        return dao.getExpiredTask(taskId)
    }

    override fun getAllExpiredTasks(): Flow<List<ExpiredTask>> {
        return dao.getAllExpiredTasks()
    }
}