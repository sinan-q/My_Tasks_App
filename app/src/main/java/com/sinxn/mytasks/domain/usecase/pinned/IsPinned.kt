package com.sinxn.mytasks.domain.usecase.pinned

import com.sinxn.mytasks.data.local.entities.ItemType
import com.sinxn.mytasks.data.local.entities.Pinned
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface

class IsPinned(private val repository: PinnedRepositoryInterface) {
    suspend operator fun invoke(itemId: Long, itemType: ItemType): Pinned? {
        return repository.isPinned(itemId, itemType)
    }
}