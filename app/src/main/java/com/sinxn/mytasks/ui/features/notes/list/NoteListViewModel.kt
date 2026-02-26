package com.sinxn.mytasks.ui.features.notes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.core.SelectionActionHandler
import com.sinxn.mytasks.core.SelectionStateHolder
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.usecase.folder.GetPathUseCase
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val getPathUseCase: GetPathUseCase,
    private val selectionActionHandler: SelectionActionHandler,
    private val selectionStateHolder: SelectionStateHolder
) : ViewModel() {

    val selectedNotes = selectionStateHolder.selectedState
    val selectedAction = selectionStateHolder.action
    val selectionCount = selectionStateHolder.selectionCount

    private val _uiState = MutableStateFlow<NoteScreenUiState>(NoteScreenUiState.Loading)
    val uiState: StateFlow<NoteScreenUiState> = _uiState.asStateFlow()

    // Pre-computed paths map: noteId -> path string
    private val _paths = MutableStateFlow<Map<Long, String?>>(emptyMap())
    val paths: StateFlow<Map<Long, String?>> = _paths.asStateFlow()

    private val _hideLocked = MutableStateFlow(true)
    val hideLocked: StateFlow<Boolean> = _hideLocked.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            noteUseCases.getNotes().map { notes -> notes.map { it.toListItemUiModel() } }.collectLatest { notes ->
                val currentState = _uiState.value
                if (currentState is NoteScreenUiState.Success) {
                    _uiState.value = currentState.copy(notes = notes)
                } else {
                    _uiState.value = NoteScreenUiState.Success(Note(), notes, null, emptyList())
                }
                // Pre-compute paths for all notes
                computePaths(notes, _hideLocked.value)
            }
        }
    }

    private suspend fun computePaths(notes: List<NoteListItemUiModel>, hideLocked: Boolean) {
        val pathsMap = mutableMapOf<Long, String?>()
        for (note in notes) {
            pathsMap[note.id] = getPathUseCase(note.folderId, hideLocked)
        }
        _paths.value = pathsMap
    }

    fun setHideLocked(hide: Boolean) {
        _hideLocked.value = hide
        val state = _uiState.value
        if (state is NoteScreenUiState.Success) {
            viewModelScope.launch {
                computePaths(state.notes, hide)
            }
        }
    }

    fun onAction(action: NoteListAction) {
        when (action) {
            is NoteListAction.OnSelectionAction -> onSelectionAction(action.action)
        }
    }

    fun onSelectionNote(id: Long) = viewModelScope.launch {
        noteUseCases.getNote(id)?.let { selectionStateHolder.toggleNote(it) }
    }

    private fun onSelectionAction(action: SelectionAction) = viewModelScope.launch {
        selectionActionHandler.onAction(action)
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}
