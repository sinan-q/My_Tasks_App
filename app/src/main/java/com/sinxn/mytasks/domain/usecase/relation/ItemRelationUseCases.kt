package com.sinxn.mytasks.domain.usecase.relation

import com.sinxn.mytasks.domain.models.ItemRelation
import com.sinxn.mytasks.domain.models.RelationItemType
import com.sinxn.mytasks.domain.repository.ItemRelationRepository
import kotlinx.coroutines.flow.Flow

data class ItemRelationUseCases(
    val addRelation: AddRelation,
    val removeRelation: RemoveRelation,
    val removeRelationsForItem: RemoveRelationsForItem,
    val getParent: GetParent,
    val getChildren: GetChildren
)

class AddRelation(private val repository: ItemRelationRepository) {
    suspend operator fun invoke(relation: ItemRelation) {
        // Enforce 1 parent rule: Remove existing parent relation for this child before adding new one
        repository.removeRelationByChild(relation.childId, relation.childType)
        repository.addRelation(relation)
    }
}

class RemoveRelation(private val repository: ItemRelationRepository) {
    suspend operator fun invoke(relation: ItemRelation) {
        repository.removeRelation(relation)
    }
}

class GetParent(private val repository: ItemRelationRepository) {
    operator fun invoke(childId: Long, childType: RelationItemType): Flow<ItemRelation?> {
        return repository.getParent(childId, childType)
    }
}

class GetChildren(private val repository: ItemRelationRepository) {
    operator fun invoke(parentId: Long, parentType: RelationItemType): Flow<List<ItemRelation>> {
        return repository.getChildren(parentId, parentType)
    }
}
