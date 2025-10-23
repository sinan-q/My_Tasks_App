package com.sinxn.mytasks.ui.features.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.domain.usecase.folder.AddFolderUseCase
import com.sinxn.mytasks.domain.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.domain.usecase.folder.LockFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FolderScreenUiState {
    object Loading : FolderScreenUiState()
    data class Success(
        val folder: Folder?,
        val folders: List<Folder>,
        val notes: List<Note>,
        val tasks: List<Task>
    ) : FolderScreenUiState()
    data class Error(val message: String) : FolderScreenUiState()
}

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val noteRepository: NoteRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
    private val addFolderUseCase: AddFolderUseCase,
    private val deleteFolderAndItsContentsUseCase: DeleteFolderAndItsContentsUseCase,
    private val lockFolderUseCase: LockFolderUseCase,
    private val selectionStore: SelectionStore
) : ViewModel() {

    val selectedTasks = selectionStore.selectedTasks
    val selectedNotes = selectionStore.selectedNotes
    val selectedFolders = selectionStore.selectedFolders
    val selectedAction = selectionStore.action
    val selectionCount = selectionStore.selectionCount

    fun onSelectionTask(task: Task) = selectionStore.toggleTask(task)
    fun onSelectionNote(note: Note) = selectionStore.toggleNote(note)
    fun onSelectionFolder(folder: Folder) = selectionStore.toggleFolder(folder)

    private val _uiState = MutableStateFlow<FolderScreenUiState>(FolderScreenUiState.Loading)
    val uiState: StateFlow<FolderScreenUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
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
    fun onBack(folder: Folder) = getSubFolders(folder.parentFolderId?: 0L)

    fun updateFolderName(folderId: Long, newName: String) {
        viewModelScope.launch {
            folderRepository.updateFolderName(folderId, newName)
        }
    }

    fun updateTaskStatus(taskId: Long, status: Boolean) {
        viewModelScope.launch {
            taskRepository.updateStatusTask(taskId, status)
        }
    }

    fun getSubFolders(folderId: Long) {
        viewModelScope.launch {
            _uiState.value = FolderScreenUiState.Loading
            try {
                val folder = folderRepository.getFolderById(folderId)
                combine(
                    folderRepository.getSubFolders(folderId),
                    taskRepository.getTasksByFolderId(folderId),
                    noteRepository.getNotesByFolderId(folderId)
                ) { folders, tasks, notes ->
                    FolderScreenUiState.Success(
                        folders = folders,
                        tasks = tasks,
                        notes = notes,
                        folder = folder
                    )
                }.collectLatest { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = FolderScreenUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun setSelectionAction(action: SelectionActions) = selectionStore.setAction(action)

    fun pasteSelection() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is FolderScreenUiState.Success) {
                val folderId = currentState.folder?.folderId ?: 0L
                selectionStore.pasteSelection(folderId)
            }
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
