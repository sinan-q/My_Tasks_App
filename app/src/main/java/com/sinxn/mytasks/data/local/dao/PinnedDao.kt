package com.sinxn.mytasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.sinxn.mytasks.data.local.entities.ItemType
import com.sinxn.mytasks.data.local.entities.Pinned
import kotlinx.coroutines.flow.Flow

@Dao
interface PinnedDao {

    @Upsert
    suspend fun upsertPinned(pinned: Pinned)

    @Upsert
    suspend fun upsertPinnedItems(pinnedList: List<Pinned>)

    @Delete
    suspend fun deletePinned(pinned: Pinned)

    @Delete
    suspend fun deletePinnedItems(pinnedList: List<Pinned>)

    @Query("SELECT * FROM pinned")
    fun getPinnedItems(): Flow<List<Pinned>>

    @Query("SELECT * FROM pinned WHERE itemId = :itemId AND itemType = :itemType")
    suspend fun isPinned(itemId: Long, itemType: ItemType): Pinned?

}
