package com.sinxn.mytasks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long? = 0,
    val folderId: Long = 0L,
    val title: String = "",
    val content: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now()
)
