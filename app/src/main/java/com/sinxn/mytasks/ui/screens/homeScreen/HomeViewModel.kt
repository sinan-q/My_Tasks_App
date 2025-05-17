package com.sinxn.mytasks.ui.screens.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.interfaces.EventRepositoryInterface
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.interfaces.NoteRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val folderRepository: FolderRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val eventRepository: EventRepositoryInterface,
) : BaseViewModel() {


    val events = eventRepository.getUpcomingEvents(3).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
    val folders = folderRepository.getSubFolders(0).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
    val notes = noteRepository.getNotesByFolderId(0).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
    val tasks = taskRepository.getTasksByFolderId(0).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun addFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepository.insertFolder(folder)
            showToast("Folder Added")
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            val subfolders = folderRepository.getSubFolders(folder.folderId).first { true }
            subfolders.forEach { subfolder -> deleteFolder(subfolder) }
            val noteList = noteRepository.getNotesByFolderId(folder.folderId).first { true }
            noteList.forEach { note -> noteRepository.deleteNote(note) }
            val tasks = taskRepository.getTasksByFolderId(folder.folderId).first {true}
            tasks.forEach { task -> taskRepository.deleteTask(task) }

            folderRepository.deleteFolder(folder)
            showToast("Folder Deleted")
        }
    }

    fun lockFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepository.lockFolder(folder)
            showToast("Folder Locked")
        }
    }


}
