package com.sinxn.mytasks.domain.usecase.event

import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface

data class EventUseCases(
    val getEvents: GetEvents,
    val getArchivedEvents: GetArchivedEvents,
    val deleteEvent: DeleteEvent,
    val addEvent: AddEvent,
    val getEvent: GetEvent,
    val archiveEvent: ArchiveEvent,
    val unarchiveEvent: UnarchiveEvent,
    val archiveEvents: ArchiveEvents,
    val unarchiveEvents: UnarchiveEvents,
)

class GetEvents(private val repository: EventRepositoryInterface) {
    operator fun invoke() = repository.getAllEvents()
}

class GetArchivedEvents(private val repository: EventRepositoryInterface) {
    operator fun invoke() = repository.getArchivedEvents()
}

class DeleteEvent(private val repository: EventRepositoryInterface) {
    suspend operator fun invoke(event: Event) = repository.deleteEvent(event)
}

class AddEvent(private val repository: EventRepositoryInterface) {
    suspend operator fun invoke(event: Event) = repository.insertEvent(event)
}

class GetEvent(private val repository: EventRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.getEventById(id)
}

class ArchiveEvent(private val repository: EventRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.archiveEvent(id)
}

class UnarchiveEvent(private val repository: EventRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.unarchiveEvent(id)
}

class ArchiveEvents(private val repository: EventRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>) = repository.archiveEvents(ids)
}

class UnarchiveEvents(private val repository: EventRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>) = repository.unarchiveEvents(ids)
}
