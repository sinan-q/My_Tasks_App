package com.sinxn.mytasks.ui.features.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.domain.usecase.folder.AddFolderUseCase
import com.sinxn.mytasks.domain.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.domain.usecase.folder.LockFolderUseCase
import com.sinxn.mytasks.ui.features.notes.NoteListItemUiModel
import com.sinxn.mytasks.ui.features.notes.toListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.TaskListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.toListItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FolderScreenUiState {
    object Loading : FolderScreenUiState()
    data class Success(
        val folder: Folder?,
        val folders: List<FolderListItemUiModel>,
        val notes: List<NoteListItemUiModel>,
        val tasks: List<TaskListItemUiModel>
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

    fun onSelectionTask(id: Long) = viewModelScope.launch {
        taskRepository.getTaskById(id)?.let { selectionStore.toggleTask(it) }
    }
    fun onSelectionNote(id: Long) = viewModelScope.launch {
        noteRepository.getNoteById(id)?.let { selectionStore.toggleNote(it) }
    }
    fun onSelectionFolder(id: Long) = viewModelScope.launch {
        folderRepository.getFolderById(id)?.let { selectionStore.toggleFolder(it) }
    }

    private val _uiState = MutableStateFlow<FolderScreenUiState>(FolderScreenUiState.Loading)
    val uiState: StateFlow<FolderScreenUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun onAction(action: FolderAction) {
        when (action) {
            is FolderAction.AddFolder -> addFolder(action.folder)
            is FolderAction.DeleteFolder -> deleteFolder(action.folder)
            is FolderAction.LockFolder -> lockFolder(action.folder)
            is FolderAction.UpdateFolderName -> updateFolderName(action.folderId, action.newName)
            is FolderAction.GetSubFolders -> getSubFolders(action.folderId)
            is FolderAction.UpdateTaskStatus -> updateTaskStatus(action.taskId, action.status)
            is FolderAction.PasteSelection -> pasteSelection()
            is FolderAction.PinSelection -> pinSelection()
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    private fun addFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                addFolderUseCase(folder)
                showToast("Folder Added")
            } catch (e: Exception) {
                showToast("Error adding folder: ${e.message}")
            }
        }
    }

    private fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                deleteFolderAndItsContentsUseCase(folder)
                showToast("Folder Deleted")
            } catch (e: Exception) {
                showToast("Error deleting folder: ${e.message}")
            }
        }
    }

    private fun lockFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                lockFolderUseCase(folder, !folder.isLocked)
                showToast(if (!folder.isLocked) "Folder Locked" else "Folder Unlocked")
            } catch (e: Exception) {
                showToast("Error updating folder lock state: ${e.message}")
            }
        }
    }

    fun onBack(folder: Folder) = getSubFolders(folder.parentFolderId ?: 0L)

    private fun updateFolderName(folderId: Long, newName: String) {
        viewModelScope.launch {
            folderRepository.updateFolderName(folderId, newName)
        }
    }

    private fun updateTaskStatus(taskId: Long, status: Boolean) {
        viewModelScope.launch {
            taskRepository.updateStatusTask(taskId, status)
        }
    }

    private fun getSubFolders(folderId: Long) {
        viewModelScope.launch {
            _uiState.value = FolderScreenUiState.Loading
            try {
                val folder = folderRepository.getFolderById(folderId)
                combine(
                    folderRepository.getSubFolders(folderId).map { folders -> folders.map { it.toListItemUiModel() } },
                    taskRepository.getTasksByFolderId(folderId).map { tasks -> tasks.map { it.toListItemUiModel() } },
                    noteRepository.getNotesByFolderId(folderId).map { notes -> notes.map { it.toListItemUiModel() } }
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

    private fun pasteSelection() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is FolderScreenUiState.Success) {
                val folderId = currentState.folder?.folderId ?: 0L
                selectionStore.pasteSelection(folderId)
            }
        }
    }

    private fun pinSelection() {
        viewModelScope.launch {
            selectionStore.pinSelection()
            showToast("Items pinned")
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
