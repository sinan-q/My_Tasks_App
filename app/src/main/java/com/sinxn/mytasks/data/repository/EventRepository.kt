package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.EventDao
import com.sinxn.mytasks.data.mapper.toDomain
import com.sinxn.mytasks.data.mapper.toEntity
import com.sinxn.mytasks.domain.models.Event
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao
) : EventRepositoryInterface {
    override fun getAllEvents(): Flow<List<Event>> = eventDao.getAlLEvents().map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)

    override fun getArchivedEvents(): Flow<List<Event>> = eventDao.getArchivedEvents().map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)

    override fun getEventsByMonth(startOfMonth: LocalDateTime, endOfMonth: LocalDateTime): Flow<List<Event>> {
        return eventDao.getEventsByMonth(startOfMonth, endOfMonth).map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)
    }
    override suspend fun insertEvent(event: Event) {
        eventDao.insertEvent(event.toEntity())
    }
    override suspend fun insertEvents(events: List<Event>) {
        eventDao.insertEvents(events.map { it.toEntity() })
    }
    override suspend fun clearAllEvents() {
        eventDao.clearAllEvents()
    }

    override suspend fun deleteEvent(event: Event) = eventDao.deleteEvent(event.toEntity())


    override suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event.toEntity())
    }

    override suspend fun getEventById(eventId: Long): Event? {
        return eventDao.getEventById(eventId)?.toDomain()
    }

    override fun getEventsByFolderId(folderId: Long?): Flow<List<Event>> {
        return eventDao.getEventsByFolderId(folderId).map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)
    }

    override fun getUpcomingEvents(limit: Int): Flow<List<Event>> {
        return eventDao.getUpcomingEvents(LocalDateTime.now(), limit).map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)

    }

    override suspend fun archiveEvent(eventId: Long) = eventDao.archiveEvent(eventId)

    override suspend fun unarchiveEvent(eventId: Long) = eventDao.unarchiveEvent(eventId)

    override suspend fun archiveEvents(eventIds: List<Long>) = eventDao.archiveEvents(eventIds)

    override suspend fun unarchiveEvents(eventIds: List<Long>) = eventDao.unarchiveEvents(eventIds)
}