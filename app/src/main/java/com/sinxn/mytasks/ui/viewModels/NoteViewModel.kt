package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.domain.usecase.folder.GetPathUseCase
import com.sinxn.mytasks.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    private val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder.asStateFlow()

    suspend fun getPath(folderId: Long, hideLocked: Boolean): String? {
            return getPathUseCase(folderId, hideLocked)
    }
    fun onSelectionNote(note: Note) = selectionStore.toggleNote(note)

    fun setSelectionAction(action: SelectionActions) = selectionStore.setAction(action)

    fun clearSelection() {
        selectionStore.clearSelection()
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

    val notes = noteRepository.getAllNotes().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    private val _note = MutableStateFlow(Note())
    val note: StateFlow<Note> = _note

    fun onNoteUpdate(note: Note) {
        _note.value = note
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            if (note.title.isEmpty() && note.content.isEmpty()) {
                showToast(Constants.SAVE_FAILED_EMPTY)
                return@launch
            }
            noteRepository.insertNote(note)
            showToast(Constants.SAVE_SUCCESS)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            val deleted = noteRepository.deleteNote(note)
            if (deleted == 0) {
                showToast(Constants.DELETE_FAILED)
                return@launch
            }
            showToast(Constants.DELETE_SUCCESS)
        }
    }

    fun fetchNoteById(noteId: Long) {
        viewModelScope.launch {
            val fetchedNote = noteRepository.getNoteById(noteId)
            if (fetchedNote == null) {
                showToast(Constants.NOT_FOUND)
                return@launch
            }
            fetchFolderById(fetchedNote.folderId)
            _note.value = fetchedNote
        }
    }

    fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            _folder.value = fetchedFolder
            _folders.value = subFolders
            _note.value = note.value.copy(
                folderId = folderId,
            )
        }
    }

    fun newNoteByFolder(folderId: Long) {
        viewModelScope.launch {
            _note.value = Note(folderId = folderId)
            fetchFolderById(folderId)
        }
    }

}