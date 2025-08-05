package com.sinxn.mytasks.data.interfaces

import com.sinxn.mytasks.data.local.entities.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface EventRepositoryInterface {
    fun getAllEvents(): Flow<List<Event>>
    fun getEventsByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Event>>
    suspend fun insertEvent(event: Event)
    suspend fun insertEvents(events: List<Event>)
    suspend fun deleteEvent(event: Event)
    suspend fun clearAllEvents()
    suspend fun updateEvent(event: Event)
    suspend fun getEventById(eventId: Long): Event?
    fun getEventsByFolderId(folderId: Long?): Flow<List<Event>>
    fun getUpcomingEvents(limit: Int): Flow<List<Event>>
}