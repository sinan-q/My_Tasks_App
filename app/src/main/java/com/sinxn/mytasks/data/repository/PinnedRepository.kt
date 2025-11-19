package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.PinnedDao
import com.sinxn.mytasks.data.mapper.toDomain
import com.sinxn.mytasks.data.mapper.toEntity
import com.sinxn.mytasks.domain.models.ItemType
import com.sinxn.mytasks.domain.models.Pinned
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PinnedRepository @Inject constructor(
    private val dao: PinnedDao
) : PinnedRepositoryInterface {
    override fun getPinnedItems(): Flow<List<Pinned>> {
        return dao.getPinnedItems().map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)
    }

    override suspend fun isPinned(itemId: Long, itemType: ItemType): Pinned? {
        return dao.isPinned(itemId, itemType.toEntity())?.toDomain()
    }

    override suspend fun insertPinned(pinned: Pinned) {
        dao.upsertPinned(pinned.toEntity())
    }

    override suspend fun insertPinnedItems(pinnedList: List<Pinned>) {
        dao.upsertPinnedItems(pinnedList.map { it.toEntity() })
    }

    override suspend fun deletePinned(pinned: Pinned) {
        dao.deletePinned(pinned.toEntity())
    }

    override suspend fun deletePinnedItems(pinnedList: List<Pinned>) {
        dao.deletePinnedItems(pinnedList.map { it.toEntity() })
    }

}
