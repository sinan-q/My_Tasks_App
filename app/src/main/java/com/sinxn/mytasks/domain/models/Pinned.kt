package com.sinxn.mytasks.domain.models

data class Pinned(
    val id: Long = 0,
    val itemId: Long,
    val itemType: ItemType,
)

enum class ItemType {
    TASK,
    NOTE,
    EVENT,
    FOLDER
}
