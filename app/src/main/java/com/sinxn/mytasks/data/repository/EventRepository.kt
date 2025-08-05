package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.interfaces.EventRepositoryInterface
import com.sinxn.mytasks.data.local.dao.EventDao
import com.sinxn.mytasks.data.local.entities.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao
) : EventRepositoryInterface {
    override fun getAllEvents(): Flow<List<Event>> = eventDao.getAlLEvents()

    override fun getEventsByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Event>> {
        return eventDao.getEventsByMonth(startOfMonth, endOfMonth)
    }
    override suspend fun insertEvent(event: Event) {
        eventDao.insertEvent(event)
    }
    override suspend fun insertEvents(events: List<Event>) {
        eventDao.insertEvents(events)
    }
    override suspend fun clearAllEvents() {
        eventDao.clearAllEvents()
    }

    override suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event)
    }

    override suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    override suspend fun getEventById(eventId: Long): Event? {
        return eventDao.getEventById(eventId)
    }

    override fun getEventsByFolderId(folderId: Long?): Flow<List<Event>> {
        return eventDao.getEventsByFolderId(folderId)
    }

    override fun getUpcomingEvents(limit: Int): Flow<List<Event>> {
        return eventDao.getUpcomingEvents(LocalDateTime.now(), limit)

    }
}