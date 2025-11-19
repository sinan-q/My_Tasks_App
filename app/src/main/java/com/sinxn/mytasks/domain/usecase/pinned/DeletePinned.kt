package com.sinxn.mytasks.domain.usecase.pinned

import com.sinxn.mytasks.domain.models.Pinned
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface

class DeletePinned(private val repository: PinnedRepositoryInterface) {
    suspend operator fun invoke(pinned: Pinned) {
        repository.deletePinned(pinned)
    }
}