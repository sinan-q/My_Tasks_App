package com.sinxn.mytasks.ui.features.events.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.domain.models.Event
import com.sinxn.mytasks.domain.usecase.event.EventUseCases
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.ui.features.events.list.EventsUiState
import com.sinxn.mytasks.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditEventViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
    private val folderUseCases: FolderUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

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
        _uiState.value = currentState.copy(
            event = event,
        )
    }

    private fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderUseCases.getFolder(folderId)
            val subFolders = folderUseCases.getSubFolders(folderId).first()
            val currentState = _uiState.value
            _uiState.value = currentState.copy(
                folder = fetchedFolder,
                folders = subFolders,
                event = currentState.event.copy(folderId = folderId)
            )
        }
    }

    private fun fetchEventById(eventId: Long) {
        viewModelScope.launch {
            val fetchedEvent = eventUseCases.getEvent(eventId)
            if (fetchedEvent == null) {
                showToast(Constants.NOT_FOUND)
                return@launch
            }
            val currentState = _uiState.value
            _uiState.value = currentState.copy(event = fetchedEvent)

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
        if (event.id != null) {
            eventUseCases.updateEvent(event)
        } else {
            eventUseCases.addEvent(event)
        }
        showToast(Constants.SAVE_SUCCESS)
    }

    private fun deleteEvent(event: Event) = viewModelScope.launch {
        val deleted = eventUseCases.deleteEvent(event)
        if (deleted == 0) {
            showToast(Constants.DELETE_FAILED)
            return@launch
        }
        showToast(Constants.DELETE_SUCCESS)
    }
}
