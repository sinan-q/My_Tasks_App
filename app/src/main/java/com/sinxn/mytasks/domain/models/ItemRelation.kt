package com.sinxn.mytasks.domain.models

enum class RelationItemType {
    TASK, EVENT, NOTE
}

data class ItemRelation(
    val id: Long? = null,
    val parentId: Long,
    val parentType: RelationItemType,
    val childId: Long,
    val childType: RelationItemType
)
