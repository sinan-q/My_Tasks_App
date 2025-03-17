package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.EventDao
import com.sinxn.mytasks.data.local.entities.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao
) {
    fun getAllEvents(): Flow<List<Event>> = eventDao.getAlLEvents()

    fun getEventsByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Event>> {
        return eventDao.getEventsByMonth(startOfMonth, endOfMonth)
    }
    suspend fun insertEvent(event: Event) {
        eventDao.insertEvent(event)
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event)
    }

    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    suspend fun getEventById(eventId: Long): Event? {
        return eventDao.getEventById(eventId)
    }

    fun getEventsByFolderId(folderId: Long?): Flow<List<Event>> {
        return eventDao.getEventsByFolderId(folderId)
    }

    fun getUpcomingEvents(limit: Int): Flow<List<Event>> {
        return eventDao.getUpcomingEvents(LocalDateTime.now(), limit)

    }
}