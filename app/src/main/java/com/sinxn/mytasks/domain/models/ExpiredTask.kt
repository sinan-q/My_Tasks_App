package com.sinxn.mytasks.domain.models

data class ExpiredTask(
    val taskId: Long,
    val expireAfterDueDate: Boolean,
)
