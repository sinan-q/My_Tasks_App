package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.domain.models.ItemRelation
import com.sinxn.mytasks.domain.models.RelationItemType
import kotlinx.coroutines.flow.Flow

interface ItemRelationRepository {
    suspend fun addRelation(relation: ItemRelation)
    suspend fun removeRelation(relation: ItemRelation)
    suspend fun removeRelationByChild(childId: Long, childType: RelationItemType)
    suspend fun removeRelationByParent(parentId: Long, parentType: RelationItemType)
    fun getParent(childId: Long, childType: RelationItemType): Flow<ItemRelation?>
    fun getChildren(parentId: Long, parentType: RelationItemType): Flow<List<ItemRelation>>
}
