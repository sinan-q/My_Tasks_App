package com.sinxn.mytasks.ui.screens.noteScreen

import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.interfaces.NoteRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.ui.components.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface
) : BaseViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note

    private val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders

    private val _subFolders = MutableStateFlow<List<Folder>>(emptyList())
    val subFolders: StateFlow<List<Folder>> = _subFolders


    init {
        viewModelScope.launch {
            noteRepository.getAllNotes().collect { noteList ->
                _notes.value = noteList
            }
        }
        viewModelScope.launch {
            folderRepository.getAllFolders().collect { folders ->
                _folders.value = folders
            }
        }
    }

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
            fetchFolderById(fetchedNote?.folderId?:0)
            _note.value = fetchedNote
        }
    }

    fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            _subFolders.value = subFolders
            _folder.value = fetchedFolder
            _note.value = note.value?.copy(
                folderId = fetchedFolder.folderId,
            )
        }
    }

    fun newNoteByFolder(folderId: Long) {
        viewModelScope.launch {
            _note.value = Note()
            fetchFolderById(folderId)
        }
    }

    fun getPath(folderId: Long, hideLocked: Boolean): String? {
        val path = StringBuilder()
        var curr = folderId
        while (curr != 0L) {
            val folder = folders.value.find { it.folderId == curr }
            path.insert(0, "/")
            path.insert(0, folder?.name)
            curr = folder?.parentFolderId ?: 0L
            if (folder?.isLocked == true && hideLocked) return null
        }
        return path.toString()
    }
}