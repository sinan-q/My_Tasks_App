package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.ExpiredTaskDao
import com.sinxn.mytasks.data.mapper.toDomain
import com.sinxn.mytasks.data.mapper.toEntity
import com.sinxn.mytasks.domain.models.ExpiredTask
import com.sinxn.mytasks.domain.repository.ExpiredTaskRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpiredTaskRepository @Inject constructor(
    private val dao: ExpiredTaskDao
) : ExpiredTaskRepositoryInterface {

    override suspend fun insert(expiredTask: ExpiredTask) {
        dao.insert(expiredTask.toEntity())
    }

    override suspend fun delete(expiredTask: ExpiredTask) {
        dao.delete(expiredTask.toEntity())
    }

    override suspend fun getExpiredTask(taskId: Long): ExpiredTask? {
        return dao.getExpiredTask(taskId)?.toDomain()
    }

    override fun getAllExpiredTasks(): Flow<List<ExpiredTask>> {
        return dao.getAllExpiredTasks().map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)
    }
}