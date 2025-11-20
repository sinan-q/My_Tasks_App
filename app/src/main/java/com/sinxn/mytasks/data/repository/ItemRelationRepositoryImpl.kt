package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.ItemRelationDao
import com.sinxn.mytasks.domain.models.ItemRelation
import com.sinxn.mytasks.domain.models.RelationItemType
import com.sinxn.mytasks.domain.repository.ItemRelationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.sinxn.mytasks.data.local.entities.ItemRelation as ItemRelationEntity

class ItemRelationRepositoryImpl @Inject constructor(
    private val dao: ItemRelationDao
) : ItemRelationRepository {

    override suspend fun addRelation(relation: ItemRelation) {
        dao.insertRelation(relation.toEntity())
    }

    override suspend fun removeRelation(relation: ItemRelation) {
        dao.deleteRelation(relation.toEntity())
    }

    override suspend fun removeRelationByChild(childId: Long, childType: RelationItemType) {
        dao.deleteRelationByChild(childId, childType)
    }

    override suspend fun removeRelationByParent(parentId: Long, parentType: RelationItemType) {
        dao.deleteRelationByParent(parentId, parentType)
    }

    override fun getParent(childId: Long, childType: RelationItemType): Flow<ItemRelation?> {
        return dao.getParent(childId, childType).map { it?.toDomain() }
    }

    override fun getChildren(parentId: Long, parentType: RelationItemType): Flow<List<ItemRelation>> {
        return dao.getChildren(parentId, parentType).map { list ->
            list.map { it.toDomain() }
        }
    }

    private fun ItemRelation.toEntity(): ItemRelationEntity {
        return ItemRelationEntity(
            id = id,
            parentId = parentId,
            parentType = parentType,
            childId = childId,
            childType = childType
        )
    }

    private fun ItemRelationEntity.toDomain(): ItemRelation {
        return ItemRelation(
            id = id,
            parentId = parentId,
            parentType = parentType,
            childId = childId,
            childType = childType
        )
    }
}
