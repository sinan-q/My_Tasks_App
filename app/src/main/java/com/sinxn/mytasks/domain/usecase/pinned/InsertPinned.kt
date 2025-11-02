package com.sinxn.mytasks.domain.usecase.pinned

import com.sinxn.mytasks.data.local.entities.Pinned
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface

class InsertPinned(private val repository: PinnedRepositoryInterface) {
    suspend operator fun invoke(pinned: Pinned) {
        repository.insertPinned(pinned)
    }
}