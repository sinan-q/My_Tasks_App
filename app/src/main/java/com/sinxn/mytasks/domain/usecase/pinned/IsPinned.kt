package com.sinxn.mytasks.domain.usecase.pinned

import com.sinxn.mytasks.domain.models.ItemType
import com.sinxn.mytasks.domain.models.Pinned
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface

class IsPinned(private val repository: PinnedRepositoryInterface) {
    suspend operator fun invoke(itemId: Long, itemType: ItemType): Pinned? {
        return repository.isPinned(itemId, itemType)
    }
}