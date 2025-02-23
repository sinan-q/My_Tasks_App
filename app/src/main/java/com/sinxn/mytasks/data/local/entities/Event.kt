package com.sinxn.mytasks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    val folderId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val start: LocalDateTime? = null,
    val end: LocalDateTime? = null
)