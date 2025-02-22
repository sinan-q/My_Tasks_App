package com.sinxn.mytasks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    val folderId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val timestamp: Date = Date(),
    val due: Date? = null
)
