package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.interfaces.NoteRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepositoryInterface,
    folderRepository: FolderRepositoryInterface
) : BaseViewModel(folderRepository) {

    val notes = noteRepository.getAllNotes().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    private val _note = MutableStateFlow(Note())
    val note: StateFlow<Note> = _note

    fun addNote(note: Note) {
        viewModelScope.launch {
            noteRepository.insertNote(note)
            showToast("Note added successfully")
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
            showToast("Note Deleted")
        }
    }

    fun fetchNoteById(noteId: Long) {
        viewModelScope.launch {
            val fetchedNote = noteRepository.getNoteById(noteId)
            fetchFolderById(fetchedNote.folderId) {}
            _note.value = fetchedNote
        }
    }

    fun fetchFolderById(folderId: Long) {
            fetchFolderById(folderId, action = {
                _note.value = note.value.copy(
                folderId = it,
            )})

    }

    fun newNoteByFolder(folderId: Long) {
        viewModelScope.launch {
            _note.value = Note()
            fetchFolderById(folderId)
        }
    }

}