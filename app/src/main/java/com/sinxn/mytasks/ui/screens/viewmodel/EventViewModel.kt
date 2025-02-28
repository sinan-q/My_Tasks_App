package com.sinxn.mytasks.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.repository.EventRepository
import com.sinxn.mytasks.data.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {

    val upcomingEvents = repository.getUpcomingEvents(10).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    private val _eventsOnMonth = MutableStateFlow<List<Event>>(emptyList())
    val eventsOnMonth: StateFlow<List<Event>> = _eventsOnMonth

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event

    private val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders

    private val _startOfMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0))
    val startOfMonth: StateFlow<LocalDateTime> = _startOfMonth
    private val _endOfMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59))
    val endOfMonth: StateFlow<LocalDateTime> = _endOfMonth

    fun onMonthChange(month: LocalDateTime) {
        _startOfMonth.value = month.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        _endOfMonth.value = month.withDayOfMonth(month.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59)
        getEventsByMonth()
    }


    init {
        viewModelScope.launch {
            folderRepository.getAllFolders().collect { folders ->
                _folders.value = folders
            }
        }
        getEventsByMonth()
    }
    private fun getEventsByMonth() = viewModelScope.launch {
            repository.getEventsByMonth(startOfMonth.value, endOfMonth.value ).collectLatest { events ->
                _eventsOnMonth.value = events

        }
    }

    fun fetchEventById(eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedEvent = repository.getEventById(eventId)
            val fetchedFolder = folderRepository.getFolderById(fetchedEvent?.folderId?: 0)
            _event.value = fetchedEvent
            _folder.value = fetchedFolder
        }
    }

    fun insertEvent(event: Event) = viewModelScope.launch {
        if (event.start != null && event.end !=null)
            if (event.start.isBefore(event.end)) repository.insertEvent(event)
            else return@launch
        else repository.insertEvent(event)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteEvent(event)
    }

    fun updateEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateEvent(event)
    }

    fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            _folders.value = subFolders
            _folder.value = fetchedFolder
            _event.value = event.value?.copy(
                folderId = fetchedFolder.folderId,
            )
        }
    }

    fun getPath(folderId: Long): String {
        val path = StringBuilder()
        var curr = folderId
        while (curr != 0L) {
            val folder = folders.value.find { it.folderId == curr }
            path.insert(0, "/")
            path.insert(0, folder?.name)
            curr = folder?.parentFolderId ?: 0L

        }

        return path.toString()
    }
}