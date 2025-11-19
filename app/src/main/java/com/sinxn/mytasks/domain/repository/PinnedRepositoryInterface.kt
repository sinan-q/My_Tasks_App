package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.domain.models.ItemType
import com.sinxn.mytasks.domain.models.Pinned
import kotlinx.coroutines.flow.Flow

interface PinnedRepositoryInterface {

    fun getPinnedItems(): Flow<List<Pinned>>

    suspend fun isPinned(itemId: Long, itemType: ItemType): Pinned?

    suspend fun insertPinned(pinned: Pinned)

    suspend fun deletePinned(pinned: Pinned)
    suspend fun deletePinnedItems(pinnedList: List<Pinned>)
    suspend fun insertPinnedItems(pinnedList: List<Pinned>)
}
