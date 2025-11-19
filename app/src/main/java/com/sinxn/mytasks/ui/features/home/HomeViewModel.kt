package com.sinxn.mytasks.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.core.SelectionActionHandler
import com.sinxn.mytasks.core.SelectionStateHolder
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.folder.LockFolderUseCase
import com.sinxn.mytasks.domain.usecase.home.HomeUseCases
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.ui.features.folders.toListItemUiModel
import com.sinxn.mytasks.ui.features.notes.list.toListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.toListItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeScreenUiState {
    object Loading : HomeScreenUiState()
    data class Success(
        val homeUiModel: HomeUiModel
    ) : HomeScreenUiState()

    data class Error(val message: String) : HomeScreenUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCases: HomeUseCases,
    private val taskUseCases: TaskUseCases,
    private val noteUseCases: NoteUseCases,
    private val folderUseCases: FolderUseCases,
    private val deleteFolderAndItsContentsUseCase: DeleteFolderAndItsContentsUseCase,
    private val lockFolderUseCase: LockFolderUseCase,
    private val selectionActionHandler: SelectionActionHandler,
    private val selectionStateHolder: SelectionStateHolder) : ViewModel() {


    val selectedAction = selectionStateHolder.action
    val selectionCount = selectionStateHolder.selectionCount

    fun onSelectionTask(id: Long) = viewModelScope.launch {
        taskUseCases.getTask(id)?.let { selectionStateHolder.toggleTask(it) }
    }

    fun onSelectionNote(id: Long) = viewModelScope.launch {
        noteUseCases.getNote(id)?.let { selectionStateHolder.toggleNote(it) }
    }

    fun onSelectionFolder(id: Long) = viewModelScope.launch {
        folderUseCases.getFolder(id).let { it?.let{ folder->  selectionStateHolder.toggleFolder(folder)} }
    }

    fun onAction(action: SelectionAction) = viewModelScope.launch {
        selectionActionHandler.onAction(action)
    }

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                homeUseCases.getDashboardData(),
                selectionStateHolder.selectedTasks,
                selectionStateHolder.selectedNotes,
                selectionStateHolder.selectedFolders
            ) { dashboardData, selectedTasks, selectedNotes, selectedFolders ->
                HomeScreenUiState.Success(dashboardData.copy(
                    folders = selectedFolders.map { folderItem ->
                        folderItem.toListItemUiModel().copy(
                            isSelected = selectedFolders.any { it.folderId == folderItem.folderId }
                        )
                    },
                    tasks = selectedTasks.map { taskItem ->
                        taskItem.toListItemUiModel().copy(
                            isSelected = selectedTasks.any { it.id == taskItem.id }
                        )
                    },
                    notes = selectedNotes.map { noteItem ->
                        noteItem.toListItemUiModel().copy(
                            isSelected = selectedNotes.any { it.id == noteItem.id }
                        )
                    },
                ))
            }
            .collectLatest { homeUiModel ->
                _uiState.value = homeUiModel
            }
        }
    }

    fun addFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                folderUseCases.addFolder(folder)
                showToast("Folder Added")
            } catch (e: Exception) {
                showToast("Error adding folder: ${e.message}")
            }
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                deleteFolderAndItsContentsUseCase(folder)
                showToast("Folder Deleted")
            } catch (e: Exception) {
                showToast("Error deleting folder: ${e.message}")
            }
        }
    }

    fun lockFolder(folder: Folder) {
        viewModelScope.launch {
            try {
                lockFolderUseCase(
                    folder,
                    !folder.isLocked
                )
                showToast(if (!folder.isLocked) "Folder Locked" else "Folder Unlocked")
            } catch (e: Exception) {
                showToast("Error updating folder lock state: ${e.message}")
            }
        }
    }

    fun updateStatusTask(taskId: Long, status: Boolean) {
        viewModelScope.launch {
            taskUseCases.updateStatusTask(taskId, status)
        }
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}


