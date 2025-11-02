package com.sinxn.mytasks.domain.usecase.pinned

import com.sinxn.mytasks.data.local.entities.Pinned
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface
import kotlinx.coroutines.flow.Flow

class GetPinnedItems(private val repository: PinnedRepositoryInterface) {
    operator fun invoke(): Flow<List<Pinned>> {
        return repository.getPinnedItems()
    }
}