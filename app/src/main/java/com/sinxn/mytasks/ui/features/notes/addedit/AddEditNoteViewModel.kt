package com.sinxn.mytasks.ui.features.notes.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.ui.features.notes.list.NoteScreenUiState
import com.sinxn.mytasks.ui.features.notes.list.toListItemUiModel
import com.sinxn.mytasks.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val folderUseCases: FolderUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteScreenUiState>(NoteScreenUiState.Loading)
    val uiState: StateFlow<NoteScreenUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

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

    private fun showToast(message: String) {
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
            noteUseCases.addNote(note)
            showToast(Constants.SAVE_SUCCESS)
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch {
            val deleted = noteUseCases.deleteNote(note)
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
                val fetchedNote = noteUseCases.getNote(noteId)
                if (fetchedNote == null) {
                    _uiState.value = NoteScreenUiState.Error(Constants.NOT_FOUND)
                    return@launch
                }
                val fetchedFolder = folderUseCases.getFolder(fetchedNote.folderId)
                val subFolders = folderUseCases.getSubFolders(fetchedNote.folderId).first()
                val notes = noteUseCases.getNotes().map { notes -> notes.map { it.toListItemUiModel() } }.first()
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
            val fetchedFolder = folderUseCases.getFolder(folderId)
            val subFolders = folderUseCases.getSubFolders(folderId).first()
            val currentState = _uiState.value
            if (currentState is NoteScreenUiState.Success) {
                _uiState.value = currentState.copy(
                    folder = fetchedFolder,
                    folders = subFolders,
                    note = currentState.note.copy(folderId = folderId)
                )
            } else {
                 val notes = noteUseCases.getNotes().map { notes -> notes.map { it.toListItemUiModel() } }.first()
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
            val fetchedFolder = folderUseCases.getFolder(folderId)
            val subFolders = folderUseCases.getSubFolders(folderId).first()
            val notes = noteUseCases.getNotes().map { notes -> notes.map { it.toListItemUiModel() } }.first()
            _uiState.value = NoteScreenUiState.Success(
                note = Note(folderId = folderId),
                notes = notes,
                folder = fetchedFolder,
                folders = subFolders
            )
        }
    }
}
