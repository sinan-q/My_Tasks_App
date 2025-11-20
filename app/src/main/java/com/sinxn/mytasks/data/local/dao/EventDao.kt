package com.sinxn.mytasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sinxn.mytasks.data.local.entities.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE isArchived = 0 ORDER BY start DESC")
    fun getAlLEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isArchived = 1 ORDER BY start DESC")
    fun getArchivedEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isArchived = 0 AND start BETWEEN :startOfMonth AND :endOfMonth ORDER BY timestamp ASC")
    fun getEventsByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)

    @Query("DELETE FROM events")
    suspend fun clearAllEvents()

    @Delete
    suspend fun deleteEvent(event: Event): Int

    @Update
    suspend fun updateEvent(event: Event)

    @Query("SELECT * FROM events WHERE id = :eventId AND isArchived = 0 LIMIT 1")
    suspend fun getEventById(eventId: Long): Event?

    @Query("SELECT * FROM events WHERE folderId = :folderId AND isArchived = 0")
    fun getEventsByFolderId(folderId: Long?): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isArchived = 0 AND `end` > :now ORDER BY `end` ASC LIMIT :limit")
    fun getUpcomingEvents(now: LocalDateTime, limit: Int): Flow<List<Event>>

    @Query("UPDATE events SET isArchived = 1 WHERE id = :eventId")
    suspend fun archiveEvent(eventId: Long)

    @Query("UPDATE events SET isArchived = 0 WHERE id = :eventId")
    suspend fun unarchiveEvent(eventId: Long)

    @Query("UPDATE events SET isArchived = 1 WHERE id IN (:eventIds)")
    suspend fun archiveEvents(eventIds: List<Long>)

    @Query("UPDATE events SET isArchived = 0 WHERE id IN (:eventIds)")
    suspend fun unarchiveEvents(eventIds: List<Long>)
}
