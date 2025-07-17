package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.FolderStore
import com.sinxn.mytasks.data.interfaces.EventRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val folderStore: FolderStore,
) : ViewModel() {

    val upcomingEvents = repository.getUpcomingEvents(10).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    val folders = folderStore.folders
    val folder = folderStore.parentFolder

    private val _eventsOnMonth = MutableStateFlow<List<Event>>(emptyList())
    val eventsOnMonth: StateFlow<List<Event>> = _eventsOnMonth

    private val _tasksOnMonth = MutableStateFlow<List<Task>>(emptyList())
    val tasksOnMonth: StateFlow<List<Task>> = _tasksOnMonth

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    fun onMonthChange(month: LocalDate) {
        getEventsAndTasksByMonth(month)
    }

    init {
        getEventsAndTasksByMonth(LocalDate.now())
    }
    private fun getEventsAndTasksByMonth(date: LocalDate) {
        val startOfMonth = date.withDayOfMonth(1).atStartOfDay()
        val endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1)
        viewModelScope.launch {
            repository.getEventsByMonth(startOfMonth, endOfMonth)
                .collectLatest { events ->
                    _eventsOnMonth.value = events
                }
        }
        viewModelScope.launch {
            taskRepository.getTasksByMonth(startOfMonth, endOfMonth ).collectLatest { tasks ->
                _tasksOnMonth.value = tasks
            }
        }
    }

    fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            folderStore.fetchFolderById(folderId = folderId)
            _event.value = event.value?.copy(
                folderId = folderId,
            )
        }

    }

    fun fetchEventById(eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedEvent = repository.getEventById(eventId)
            _event.value = fetchedEvent
            fetchFolderById(fetchedEvent?.folderId?:0L)
        }
    }

    fun insertEvent(event: Event) = viewModelScope.launch {
        if (event.start != null && event.end !=null)
            if (event.start.isBefore(event.end)) repository.insertEvent(event)
            else return@launch
        else {
            repository.insertEvent(event)
            showToast("Event Created")
        }
    }

    fun deleteEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteEvent(event)
        showToast("Event Deleted")
    }

    fun updateEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateEvent(event)
    }


}