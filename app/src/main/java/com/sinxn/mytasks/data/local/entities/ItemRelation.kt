package com.sinxn.mytasks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sinxn.mytasks.domain.models.RelationItemType

@Entity(tableName = "item_relations")
data class ItemRelation(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val parentId: Long,
    val parentType: RelationItemType,
    val childId: Long,
    val childType: RelationItemType
)
