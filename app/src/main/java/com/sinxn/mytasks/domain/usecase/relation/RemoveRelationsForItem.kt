package com.sinxn.mytasks.domain.usecase.relation

import com.sinxn.mytasks.domain.models.RelationItemType
import com.sinxn.mytasks.domain.repository.ItemRelationRepository

class RemoveRelationsForItem(
    private val repository: ItemRelationRepository
) {
    suspend operator fun invoke(itemId: Long, itemType: RelationItemType) {
        repository.removeRelationByChild(itemId, itemType)
        repository.removeRelationByParent(itemId, itemType)
    }
}
