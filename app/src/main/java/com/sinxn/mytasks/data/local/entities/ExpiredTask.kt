package com.sinxn.mytasks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expired_tasks")
data class ExpiredTask(
    @PrimaryKey
    val taskId: Long, // Foreign key to the Task table
    val expireAfterDueDate: Boolean = true // Flag to enable/disable this rule
)
