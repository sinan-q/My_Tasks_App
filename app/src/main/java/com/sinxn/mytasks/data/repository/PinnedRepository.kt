package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.PinnedDao
import com.sinxn.mytasks.data.local.entities.ItemType
import com.sinxn.mytasks.data.local.entities.Pinned
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PinnedRepository @Inject constructor(
    private val dao: PinnedDao
) : PinnedRepositoryInterface {
    override fun getPinnedItems(): Flow<List<Pinned>> {
        return dao.getPinnedItems()
    }

    override suspend fun isPinned(itemId: Long, itemType: ItemType): Pinned? {
        return dao.isPinned(itemId, itemType)
    }

    override suspend fun insertPinned(pinned: Pinned) {
        dao.upsertPinned(pinned)
    }

    override suspend fun insertPinnedItems(pinnedList: List<Pinned>) {
        dao.upsertPinnedItems(pinnedList)
    }

    override suspend fun deletePinned(pinned: Pinned) {
        dao.deletePinned(pinned)
    }

    override suspend fun deletePinnedItems(pinnedList: List<Pinned>) {
        dao.deletePinnedItems(pinnedList)
    }

}
