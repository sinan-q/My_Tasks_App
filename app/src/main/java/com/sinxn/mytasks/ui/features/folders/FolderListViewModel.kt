package com.sinxn.mytasks.ui.features.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.ui.features.notes.list.NoteListItemUiModel
import com.sinxn.mytasks.ui.features.notes.list.toListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.TaskListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.toListItemUiModel
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
    private val noteUseCases: NoteUseCases,
    private val taskUseCases: TaskUseCases,
    private val folderUseCases: FolderUseCases,
    private val selectionStore: SelectionStore
) : ViewModel() {

    val selectedTasks = selectionStore.selectedTasks
    val selectedNotes = selectionStore.selectedNotes
    val selectedFolders = selectionStore.selectedFolders
    val selectedAction = selectionStore.action
    val selectionCount = selectionStore.selectionCount

    fun onSelectionTask(id: Long) = viewModelScope.launch {
        taskUseCases.getTask(id)?.let { selectionStore.toggleTask(it) }
    }
    fun onSelectionNote(id: Long) = viewModelScope.launch {
        noteUseCases.getNote(id)?.let { selectionStore.toggleNote(it) }
    }
    fun onSelectionFolder(id: Long) = viewModelScope.launch {
        folderUseCases.getFolder(id).let { it?.let {folder -> selectionStore.toggleFolder(folder) }}
    }

    private val _uiState = MutableStateFlow<FolderScreenUiState>(FolderScreenUiState.Loading)
    val uiState: StateFlow<FolderScreenUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun onAction(action: FolderListAction) {
        viewModelScope.launch {
            when (action) {
                is FolderListAction.AddFolderList -> addFolder(action.folder)
                is FolderListAction.DeleteFolderList -> deleteFolder(action.folder)
                is FolderListAction.LockFolderList -> lockFolder(action.folder)
                is FolderListAction.UpdateFolderListName -> updateFolderName(action.folderId, action.newName)
                is FolderListAction.GetSubFolders -> getSubFolders(action.folderId)
                is FolderListAction.UpdateTaskStatus -> updateTaskStatus(action.taskId, action.status)
                is FolderListAction.OnSelectionListAction -> selectionStore.onAction(action.action)
            }
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
                folderUseCases.addFolder(folder)
                showToast("Folder Added")
            } catch (e: Exception) {
                showToast("Error adding folder: ${e.message}")
            }
        }
    }

    private fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                folderUseCases.deleteFolder(folder)
                showToast("Folder Deleted")
            } catch (e: Exception) {
                showToast("Error deleting folder: ${e.message}")
            }
        }
    }

    private fun lockFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                folderUseCases.lockFolder(folder, !folder.isLocked)
                showToast(if (!folder.isLocked) "Folder Locked" else "Folder Unlocked")
            } catch (e: Exception) {
                showToast("Error updating folder lock state: ${e.message}")
            }
        }
    }

    fun onBack(folder: Folder) = getSubFolders(folder.parentFolderId ?: 0L)

    private fun updateFolderName(folderId: Long, newName: String) {
        viewModelScope.launch {
            folderUseCases.updateFolderName(folderId, newName)
        }
    }

    private fun updateTaskStatus(taskId: Long, status: Boolean) {
        viewModelScope.launch {
            taskUseCases.updateStatusTask(taskId, status)
        }
    }

    private fun getSubFolders(folderId: Long) {
        viewModelScope.launch {
            _uiState.value = FolderScreenUiState.Loading
            try {
                val folder = folderUseCases.getFolder(folderId)
                combine(
                    folderUseCases.getSubFolders(folderId).map { folders -> folders.map { it.toListItemUiModel() } },
                    taskUseCases.getTasksByFolderId(folderId).map { tasks -> tasks.map { it.toListItemUiModel() } },
                    noteUseCases.getNotesByFolderId(folderId).map { notes -> notes.map { it.toListItemUiModel() } }
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


}
