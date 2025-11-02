package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.data.local.entities.ItemType
import com.sinxn.mytasks.data.local.entities.Pinned
import kotlinx.coroutines.flow.Flow

interface PinnedRepositoryInterface {

    fun getPinnedItems(): Flow<List<Pinned>>

    suspend fun isPinned(itemId: Long, itemType: ItemType): Pinned?

    suspend fun insertPinned(pinned: Pinned)

    suspend fun deletePinned(pinned: Pinned)
}
