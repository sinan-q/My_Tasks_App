package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.FolderStore
import com.sinxn.mytasks.data.interfaces.EventRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.screens.eventScreen.EventConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _event = MutableStateFlow<Event>(Event())
    val event: StateFlow<Event> = _event

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    fun onUpdateEvent(event: Event) {
        _event.value = event
    }

    init {
        viewModelScope.launch {
            repository.getAllEvents().collectLatest { events ->
                _events.value = events
            }
        }
        viewModelScope.launch {
            taskRepository.getAllTasks().collectLatest { tasks ->
                _tasks.value = tasks
            }
        }
    }


    fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            folderStore.fetchFolderById(folderId = folderId)
            _event.value = event.value.copy(
                folderId = folderId,
            )
        }

    }

    fun fetchEventById(eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedEvent = repository.getEventById(eventId)
            if (fetchedEvent==null) {
                showToast(EventConstants.EVENT_NOT_FOUND)
                return@launch
            }
            _event.value = fetchedEvent
            fetchFolderById(fetchedEvent.folderId)
        }
    }

    fun insertEvent(event: Event) = viewModelScope.launch {
        if (event.start == null || event.end == null) {
            showToast(EventConstants.EVENT_SAVE_FAILED_DATE_EMPTY)
            return@launch
        }
        if (event.start.isAfter(event.end)) {
            showToast(EventConstants.EVENT_SAVE_FAILED_END_AFTER_START)
            return@launch
        }
        repository.insertEvent(event)
        showToast(EventConstants.EVENT_SAVE_SUCCESS)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteEvent(event)
        showToast(EventConstants.EVENT_DELETE_SUCCESS)
    }

    fun updateEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateEvent(event)
    }


}