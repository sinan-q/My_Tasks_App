package com.sinxn.mytasks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pinned")
data class Pinned(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long,
    val itemType: ItemType
)

enum class ItemType {
    TASK,
    NOTE,
    EVENT,
    FOLDER
}
