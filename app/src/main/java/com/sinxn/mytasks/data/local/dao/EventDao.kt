package com.sinxn.mytasks.data.local.dao

import androidx.room.*
import com.sinxn.mytasks.data.local.entities.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY timestamp DESC")
    fun getAlLEvents(): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: Long): Event?

    @Query("SELECT * FROM events WHERE folderId = :folderId")
    fun getEventsByFolderId(folderId: Long?): Flow<List<Event>>
}
