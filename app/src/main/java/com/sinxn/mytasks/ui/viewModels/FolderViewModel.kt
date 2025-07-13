package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.interfaces.NoteRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.store.SelectionActions
import com.sinxn.mytasks.data.store.SelectionStore
import com.sinxn.mytasks.data.usecase.folder.AddFolderUseCase
import com.sinxn.mytasks.data.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.data.usecase.folder.LockFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val noteRepository: NoteRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    folderRepo: FolderRepositoryInterface,
    private val addFolderUseCase: AddFolderUseCase,
    private val deleteFolderAndItsContentsUseCase: DeleteFolderAndItsContentsUseCase,
    private val lockFolderUseCase: LockFolderUseCase,
    private val selectionStore: SelectionStore,

    ) : BaseViewModel(folderRepo) {

    val selectedTasks = selectionStore.selectedTasks
    val selectedNotes = selectionStore.selectedNotes
    val selectedFolders = selectionStore.selectedFolders
    val selectedAction = selectionStore.action
    val selectionCount = selectionStore.selectionCount


    fun onSelectionTask(task: Task) = selectionStore.toggleTask(task)
    fun onSelectionNote(note: Note) = selectionStore.toggleNote(note)
    fun onSelectionFolder(folder: Folder) = selectionStore.toggleFolder(folder)

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

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
    fun onBack(folder: Folder) = getSubFolders(folder.parentFolderId?: 0L)


    fun updateTaskStatus(taskId: Long, status: Boolean) {
        viewModelScope.launch {
            taskRepository.updateStatusTask(taskId, status)
        }
    }

    fun getSubFolders(folderId: Long) {
        viewModelScope.launch {
            _folder.value = folderRepository.getFolderById(folderId)
        }
        viewModelScope.launch {
            taskRepository.getTasksByFolderId(folderId).collectLatest { taskList ->
                _tasks.value = taskList
            }
        }
        viewModelScope.launch {
            folderRepository.getSubFolders(folderId).collectLatest { folderList ->
                _folders.value = folderList
            }
        }
        viewModelScope.launch {
            noteRepository.getNotesByFolderId(folderId).collectLatest { noteList ->
                _notes.value = noteList
            }
        }
    }

    fun setSelectionAction(action: SelectionActions) = selectionStore.setAction(action)

    fun pasteSelection() {
        viewModelScope.launch {
            val folderId = folder.value?.folderId?:0L
            selectionStore.pasteSelection(folderId)
        }

    }

    fun clearSelection() {
        selectionStore.clearSelection()
    }

    fun deleteSelection() {
        viewModelScope.launch {
            selectionStore.deleteSelection()
        }
    }


}
