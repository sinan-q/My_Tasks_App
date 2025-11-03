package com.sinxn.mytasks.ui.features.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.usecase.folder.GetPathUseCase
import com.sinxn.mytasks.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NoteScreenUiState {
    object Loading : NoteScreenUiState()
    data class Success(
        val note: Note,
        val notes: List<NoteListItemUiModel>,
        val folder: Folder?,
        val folders: List<Folder>
    ) : NoteScreenUiState()
    data class Error(val message: String) : NoteScreenUiState()
}

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
    private val selectionStore: SelectionStore,
    private val getPathUseCase: GetPathUseCase
) : ViewModel() {

    val selectedNotes = selectionStore.selectedNotes
    val selectedAction = selectionStore.action
    val selectionCount = selectionStore.selectionCount

    private val _uiState = MutableStateFlow<NoteScreenUiState>(NoteScreenUiState.Loading)
    val uiState: StateFlow<NoteScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            noteRepository.getAllNotes().map { notes -> notes.map { it.toListItemUiModel() } }.collectLatest { notes ->
                val currentState = _uiState.value
                if (currentState is NoteScreenUiState.Success) {
                    _uiState.value = currentState.copy(notes = notes)
                } else {
                    _uiState.value = NoteScreenUiState.Success(Note(), notes, null, emptyList())
                }
            }
        }
    }

    fun onAction(action: AddEditNoteAction) {
        when (action) {
            is AddEditNoteAction.UpdateNote -> onNoteUpdate(action.note)
            is AddEditNoteAction.InsertNote -> addNote(action.note)
            is AddEditNoteAction.DeleteNote -> deleteNote(action.note)
            is AddEditNoteAction.FetchNoteById -> fetchNoteById(action.noteId)
            is AddEditNoteAction.FetchFolderById -> fetchFolderById(action.folderId)
            is AddEditNoteAction.NewNoteByFolder -> newNoteByFolder(action.folderId)
        }
    }

    suspend fun getPath(folderId: Long, hideLocked: Boolean): String? {
            return getPathUseCase(folderId, hideLocked)
    }
    fun onSelectionNote(id: Long) = viewModelScope.launch {
        noteRepository.getNoteById(id)?.let { selectionStore.toggleNote(it) }
    }

    fun setSelectionAction(action: SelectionActions) = selectionStore.setAction(action)

    fun clearSelection() {
        selectionStore.clearSelection()
    }

    fun pinSelection() {
        viewModelScope.launch {
            selectionStore.togglePinSelection()
        }
    }


    fun deleteSelection() {
        viewModelScope.launch {
            selectionStore.deleteSelection()
        }
    }

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    private fun onNoteUpdate(note: Note) {
        val currentState = _uiState.value
        if (currentState is NoteScreenUiState.Success) {
            _uiState.value = currentState.copy(note = note)
        }
    }

    private fun addNote(note: Note) {
        viewModelScope.launch {
            if (note.title.isEmpty() && note.content.isEmpty()) {
                showToast(Constants.SAVE_FAILED_EMPTY)
                return@launch
            }
            noteRepository.insertNote(note)
            showToast(Constants.SAVE_SUCCESS)
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch {
            val deleted = noteRepository.deleteNote(note)
            if (deleted == 0) {
                showToast(Constants.DELETE_FAILED)
                return@launch
            }
            showToast(Constants.DELETE_SUCCESS)
        }
    }

    private fun fetchNoteById(noteId: Long) {
        viewModelScope.launch {
            _uiState.value = NoteScreenUiState.Loading
            try {
                val fetchedNote = noteRepository.getNoteById(noteId)
                if (fetchedNote == null) {
                    _uiState.value = NoteScreenUiState.Error(Constants.NOT_FOUND)
                    return@launch
                }
                val fetchedFolder = folderRepository.getFolderById(fetchedNote.folderId)
                val subFolders = folderRepository.getSubFolders(fetchedNote.folderId).first()
                val notes = noteRepository.getAllNotes().map { notes -> notes.map { it.toListItemUiModel() } }.first()
                _uiState.value = NoteScreenUiState.Success(
                    note = fetchedNote,
                    notes = notes,
                    folder = fetchedFolder,
                    folders = subFolders
                )
            } catch (e: Exception) {
                _uiState.value = NoteScreenUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            val currentState = _uiState.value
            if (currentState is NoteScreenUiState.Success) {
                _uiState.value = currentState.copy(
                    folder = fetchedFolder,
                    folders = subFolders,
                    note = currentState.note.copy(folderId = folderId)
                )
            } else {
                 val notes = noteRepository.getAllNotes().map { notes -> notes.map { it.toListItemUiModel() } }.first()
                _uiState.value = NoteScreenUiState.Success(
                    note = Note(folderId = folderId),
                    notes = notes,
                    folder = fetchedFolder,
                    folders = subFolders
                )
            }
        }
    }

    private fun newNoteByFolder(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            val notes = noteRepository.getAllNotes().map { notes -> notes.map { it.toListItemUiModel() } }.first()
            _uiState.value = NoteScreenUiState.Success(
                note = Note(folderId = folderId),
                notes = notes,
                folder = fetchedFolder,
                folders = subFolders
            )
        }
    }

}