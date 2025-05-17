package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.interfaces.EventRepositoryInterface
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    folderRepo: FolderRepositoryInterface
) : BaseViewModel(folderRepo) {

    val upcomingEvents = repository.getUpcomingEvents(10).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    private val _eventsOnMonth = MutableStateFlow<List<Event>>(emptyList())
    val eventsOnMonth: StateFlow<List<Event>> = _eventsOnMonth

    private val _tasksOnMonth = MutableStateFlow<List<Task>>(emptyList())
    val tasksOnMonth: StateFlow<List<Task>> = _tasksOnMonth

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event

    private val _startOfMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0))
    val startOfMonth: StateFlow<LocalDateTime> = _startOfMonth
    private val _endOfMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59))
    val endOfMonth: StateFlow<LocalDateTime> = _endOfMonth

    fun onMonthChange(month: LocalDate) {
        _startOfMonth.value = month.withDayOfMonth(1).atStartOfDay()
        _endOfMonth.value = startOfMonth.value.plusMonths(1).minusSeconds(1)
        getEventsByMonth()
        getTasksByMonth()
    }


    init {

        getEventsByMonth()
        getTasksByMonth()
    }
    private fun getEventsByMonth() = viewModelScope.launch {
            repository.getEventsByMonth(startOfMonth.value, endOfMonth.value ).collectLatest { events ->
                _eventsOnMonth.value = events
        }
    }

    fun fetchFolderById(folderId: Long) {
        fetchFolderById(
            folderId = folderId,
            action = {
                _event.value = event.value?.copy(
                    folderId = it,
                )
            }
        )
    }
    private fun getTasksByMonth() = viewModelScope.launch {
        taskRepository.getTasksByMonth(startOfMonth.value, endOfMonth.value ).collectLatest { tasks ->
            _tasksOnMonth.value = tasks
        }
    }

    fun fetchEventById(eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedEvent = repository.getEventById(eventId)
            _event.value = fetchedEvent
            fetchFolderById(fetchedEvent?.folderId?:0L, action = {})
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