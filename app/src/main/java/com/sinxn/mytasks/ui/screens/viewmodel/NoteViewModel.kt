package com.sinxn.mytasks.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.repository.FolderRepository
import com.sinxn.mytasks.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note

    private val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder

    init {
        viewModelScope.launch {
            noteRepository.getAllNotes().collect { noteList ->
                _notes.value = noteList
            }
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            noteRepository.insertNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun fetchNoteById(noteId: Long) {
        viewModelScope.launch {
            val fetchedNote = noteRepository.getNoteById(noteId)
            _note.value = fetchedNote

        }
    }

    fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            _folder.value = fetchedFolder
            _note.value = Note(
                folderId = fetchedFolder.folderId,
            )
        }
    }
}