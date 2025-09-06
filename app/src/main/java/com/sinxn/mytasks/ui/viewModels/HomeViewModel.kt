package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.FolderStore
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.data.interfaces.EventRepositoryInterface
import com.sinxn.mytasks.data.interfaces.NoteRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.usecase.folder.AddFolderUseCase
import com.sinxn.mytasks.data.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.data.usecase.folder.LockFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    noteRepository: NoteRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    eventRepository: EventRepositoryInterface,
    private val addFolderUseCase: AddFolderUseCase,
    private val deleteFolderAndItsContentsUseCase: DeleteFolderAndItsContentsUseCase,
    private val lockFolderUseCase: LockFolderUseCase,
    private val selectionStore: SelectionStore,
    folderStore: FolderStore
    ) : ViewModel() {

    val selectedTasks = selectionStore.selectedTasks
    val selectedNotes = selectionStore.selectedNotes
    val selectedFolders = selectionStore.selectedFolders
    val selectedAction = selectionStore.action
    val selectionCount = selectionStore.selectionCount

    fun onSelectionTask(task: Task) = selectionStore.toggleTask(task)
    fun onSelectionNote(note: Note) = selectionStore.toggleNote(note)
    fun onSelectionFolder(folder: Folder) = selectionStore.toggleFolder(folder)

    fun setSelectionAction(action: SelectionActions) = selectionStore.setAction(action)
    fun clearSelection() = selectionStore.clearSelection()
    fun pasteSelection() {
        viewModelScope.launch {
            if (selectedAction.value == SelectionActions.COPY)
                selectionStore.pasteSelection(folderId = 0L)
        }
    }
    fun deleteSelection() { viewModelScope.launch {
        selectionStore.deleteSelection()
    }}

    val parentFolder = folderStore.parentFolder
    val folders = folderStore.folders

    val upcomingEvents = eventRepository.getUpcomingEvents(4).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    val pendingTasks = taskRepository.getPendingTasks(4).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    val mainFolders = folderStore.folderRepository.getSubFolders(0).stateIn(
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
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            folderStore.fetchFolderById(folderId = 0L)
        }
    }
    fun addFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                addFolderUseCase(folder) // Or addFolderUseCase.invoke(folder)
                showToast("Folder Added") // From BaseViewModel
            } catch (e: Exception) {
                showToast("Error adding folder: ${e.message}")
            }
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                deleteFolderAndItsContentsUseCase(folder)
                showToast("Folder Deleted") // From BaseViewModel
            } catch (e: Exception) {
                showToast("Error deleting folder: ${e.message}")
            }
        }
    }

    fun lockFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                lockFolderUseCase(folder, !folder.isLocked) // Assuming use case takes folder and new lock state
                showToast(if (!folder.isLocked) "Folder Locked" else "Folder Unlocked")
            } catch (e: Exception) {
                showToast("Error updating folder lock state: ${e.message}")
            }
        }
    }
    fun updateStatusTask(taskId: Long, status: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateStatusTask(taskId, status)
        }
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}
