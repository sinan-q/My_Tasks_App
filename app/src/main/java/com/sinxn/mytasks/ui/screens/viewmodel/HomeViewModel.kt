package com.sinxn.mytasks.ui.screens.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.repository.FolderRepository
import com.sinxn.mytasks.data.repository.NoteRepository
import com.sinxn.mytasks.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()


    private val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder

    init {
        _folder.value = Folder(name = "Root", folderId = 0L)
        viewModelScope.launch {
            taskRepository.getTasksByFolderId(0L).collect { taskList ->
                _tasks.value = taskList
            }
        }
        viewModelScope.launch {
            folderRepository.getSubFolders(0L).collect { folderList ->
                _folders.value = folderList
            }
        }
        viewModelScope.launch {
            noteRepository.getNotesByFolderId(0L).collect { noteList ->
                _notes.value = noteList
            }
        }
    }

    fun addFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepository.insertFolder(folder)
        }
    }
//
//    fun deleteFolder(folder: Folder) {
//        viewModelScope.launch {
//            folderRepository.deleteFolder(folder)
//        }
//    }

    fun getSubFolders(folderId: Long?) {
        viewModelScope.launch {
            folderRepository.getSubFolders(folderId).collect { folderList ->
                _folders.value = folderList
            }
        }
    }


}
