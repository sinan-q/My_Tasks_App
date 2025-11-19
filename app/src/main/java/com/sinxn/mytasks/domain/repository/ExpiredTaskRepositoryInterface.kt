package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.domain.models.ExpiredTask
import kotlinx.coroutines.flow.Flow

interface ExpiredTaskRepositoryInterface {

    suspend fun insert(expiredTask: ExpiredTask)

    suspend fun delete(expiredTask: ExpiredTask)

    suspend fun getExpiredTask(taskId: Long): ExpiredTask?

    fun getAllExpiredTasks(): Flow<List<ExpiredTask>>
}