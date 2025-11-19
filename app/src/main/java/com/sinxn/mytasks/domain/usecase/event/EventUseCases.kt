package com.sinxn.mytasks.domain.usecase.event

import com.sinxn.mytasks.domain.models.Event
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface

data class EventUseCases(
    val getEvents: GetEvents,
    val getArchivedEvents: GetArchivedEvents,
    val deleteEvent: DeleteEvent,
    val addEvent: AddEvent,
    val getEvent: GetEvent,
    val toggleArchive: ToggleEventArchive,
    val toggleArchives: ToggleEventsArchive,
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

class ToggleEventArchive(private val repository: EventRepositoryInterface) {
    suspend operator fun invoke(id: Long, archive: Boolean) {
        if (archive) repository.archiveEvent(id) else repository.unarchiveEvent(id)
    }
}

class ToggleEventsArchive(private val repository: EventRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>, archive: Boolean) {
        if (ids.isEmpty()) return
        if (archive) repository.archiveEvents(ids) else repository.unarchiveEvents(ids)
    }
}
