package com.sinxn.mytasks.data.local.dao

import androidx.room.*
import com.sinxn.mytasks.data.local.entities.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY start DESC")
    fun getAlLEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE start BETWEEN :startOfMonth AND :endOfMonth ORDER BY timestamp ASC")
    fun getEventsByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Event>>

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

    @Query("SELECT * FROM events WHERE `end` > :now ORDER BY `end` ASC LIMIT :limit")
    fun getUpcomingEvents(now: LocalDateTime, limit: Int): Flow<List<Event>>


}
