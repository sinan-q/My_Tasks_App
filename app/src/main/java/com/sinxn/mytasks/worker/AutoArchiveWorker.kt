package com.sinxn.mytasks.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sinxn.mytasks.domain.usecase.expired_task.ExpiredTaskUseCases
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

@HiltWorker
class AutoArchiveWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskUseCases: TaskUseCases,
    private val expiredTaskUseCases: ExpiredTaskUseCases
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val expiredTaskRules = expiredTaskUseCases.getExpiredTasks().first()
            val now = LocalDateTime.now()

            for (rule in expiredTaskRules) {
                if (rule.expireAfterDueDate) {
                    val task = taskUseCases.getTask(rule.taskId)
                    if (task != null && task.isCompleted && task.due != null && task.due.isBefore(now)) {
                        taskUseCases.archiveTask(task.id!!)
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
