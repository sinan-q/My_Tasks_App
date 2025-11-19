package com.sinxn.mytasks.domain.usecase.pinned

import com.sinxn.mytasks.domain.models.Pinned
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface

class InsertPinnedItems(private val repository: PinnedRepositoryInterface) {
    suspend operator fun invoke(pinnedList: List<Pinned>) {
        repository.insertPinnedItems(pinnedList)
    }
}