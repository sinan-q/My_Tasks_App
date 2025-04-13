package com.sinxn.mytasks.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true) val folderId: Long = 0L,
    val name: String,
    val parentFolderId: Long? = 0L,
    @ColumnInfo(defaultValue = "0")
    val isLocked: Boolean = false,
)
