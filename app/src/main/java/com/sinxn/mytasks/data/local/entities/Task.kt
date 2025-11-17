package com.sinxn.mytasks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val folderId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val due: LocalDateTime? = null,
    val recurrenceRule: String? = null,
    val isArchived: Boolean = false,
)
