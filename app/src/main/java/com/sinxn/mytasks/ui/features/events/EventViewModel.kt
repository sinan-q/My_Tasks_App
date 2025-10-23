package com.sinxn.mytasks.ui.features.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EventScreenUiState {
    object Loading : EventScreenUiState()
    data class Success(
        val events: List<Event>,
        val tasks: List<Task>,
        val folders: List<Folder>,
        val folder: Folder?,
        val event: Event,
    ) : EventScreenUiState()
    data class Error(val message: String) : EventScreenUiState()
}

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
) : ViewModel() {

    val upcomingEvents = repository.getUpcomingEvents(10).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    private val _uiState = MutableStateFlow<EventScreenUiState>(EventScreenUiState.Loading)
    val uiState: StateFlow<EventScreenUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun onAction(action: AddEditEventAction) {
        when (action) {
            is AddEditEventAction.UpdateEvent -> onUpdateEvent(action.event)
            is AddEditEventAction.InsertEvent -> insertEvent(action.event)
            is AddEditEventAction.DeleteEvent -> deleteEvent(action.event)
            is AddEditEventAction.FetchEventById -> fetchEventById(action.eventId)
            is AddEditEventAction.FetchFolderById -> fetchFolderById(action.folderId)
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    private fun onUpdateEvent(event: Event) {
        val currentState = _uiState.value
        if (currentState is EventScreenUiState.Success) {
            _uiState.value = currentState.copy(event = event)
        }
    }

    init {
        viewModelScope.launch {
            repository.getAllEvents().collectLatest { events ->
                val currentState = _uiState.value
                if (currentState is EventScreenUiState.Success) {
                    _uiState.value = currentState.copy(events = events)
                } else {
                    _uiState.value = EventScreenUiState.Success(events, emptyList(), emptyList(), null, Event())
                }
            }
        }
        viewModelScope.launch {
            taskRepository.getAllTasks().collectLatest { tasks ->
                val currentState = _uiState.value
                if (currentState is EventScreenUiState.Success) {
                    _uiState.value = currentState.copy(tasks = tasks)
                }
            }
        }
    }


    private fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            val currentState = _uiState.value
            if (currentState is EventScreenUiState.Success) {
                _uiState.value = currentState.copy(
                    folder = fetchedFolder,
                    folders = subFolders,
                    event = currentState.event.copy(folderId = folderId)
                )
            } else {
                _uiState.value = EventScreenUiState.Success(
                    events = emptyList(),
                    tasks = emptyList(),
                    folders = subFolders,
                    folder = fetchedFolder,
                    event = Event(folderId = folderId)
                )
            }
        }

    }

    private fun fetchEventById(eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedEvent = repository.getEventById(eventId)
            if (fetchedEvent == null) {
                showToast(Constants.NOT_FOUND)
                return@launch
            }
            val currentState = _uiState.value
            if (currentState is EventScreenUiState.Success) {
                _uiState.value = currentState.copy(event = fetchedEvent)
            } else {
                val fetchedFolder = folderRepository.getFolderById(fetchedEvent.folderId)
                val subFolders = folderRepository.getSubFolders(fetchedEvent.folderId).first()
                _uiState.value = EventScreenUiState.Success(
                    events = emptyList(),
                    tasks = emptyList(),
                    folders = subFolders,
                    folder = fetchedFolder,
                    event = fetchedEvent
                )
            }
            fetchFolderById(fetchedEvent.folderId)
        }
    }

    private fun insertEvent(event: Event) = viewModelScope.launch {
        if (event.title.isEmpty() && event.description.isEmpty()) {
            showToast(Constants.SAVE_FAILED_EMPTY)
            return@launch
        } else if (event.title.isEmpty()) {
            showToast(Constants.SAVE_FAILED_EMPTY)
            return@launch
        }
        if (event.start == null || event.end == null) {
            showToast(Constants.EVENT_SAVE_FAILED_DATE_EMPTY)
            return@launch
        }
        if (event.start.isAfter(event.end)) {
            showToast(Constants.EVENT_SAVE_FAILED_END_AFTER_START)
            return@launch
        }
        repository.insertEvent(event)
        showToast(Constants.SAVE_SUCCESS)
    }

    private fun deleteEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        val deleted = repository.deleteEvent(event)
        if (deleted == 0) {
            showToast(Constants.DELETE_FAILED)
            return@launch
        }
        showToast(Constants.DELETE_SUCCESS)
    }

    private fun updateEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateEvent(event)
    }


}