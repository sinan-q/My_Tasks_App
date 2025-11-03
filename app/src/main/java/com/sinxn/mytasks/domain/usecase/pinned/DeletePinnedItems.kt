package com.sinxn.mytasks.domain.usecase.pinned

import com.sinxn.mytasks.data.local.entities.Pinned
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface

class DeletePinnedItems(private val repository: PinnedRepositoryInterface) {
    suspend operator fun invoke(pinnedList: List<Pinned>) {
        repository.deletePinnedItems(pinnedList)
    }
}
