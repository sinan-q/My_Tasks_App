package com.sinxn.mytasks.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionActions
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.domain.usecase.folder.AddFolderUseCase
import com.sinxn.mytasks.domain.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.domain.usecase.folder.LockFolderUseCase
import com.sinxn.mytasks.ui.features.events.toListItemUiModel
import com.sinxn.mytasks.ui.features.folders.toListItemUiModel
import com.sinxn.mytasks.ui.features.notes.toListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.toListItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

sealed class HomeScreenUiState {
    object Loading : HomeScreenUiState()
    data class Success(
        val homeUiModel: HomeUiModel
    ) : HomeScreenUiState()
    data class Error(val message: String) : HomeScreenUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
    private val eventRepository: EventRepositoryInterface,
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

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            val parentFolder = folderRepository.getFolderById(0L) ?: Folder(folderId = 0, name = "Root", parentFolderId = null, isLocked = false)
            combine(
                folderRepository.getSubFolders(0).map { folders -> folders.map { it.toListItemUiModel() } },
                eventRepository.getUpcomingEvents(4).map { events -> events.map { it.toListItemUiModel() } },
                taskRepository.getPendingTasks(4).map { tasks -> tasks.map { it.toListItemUiModel() } },
                noteRepository.getNotesByFolderId(0).map { notes -> notes.map { it.toListItemUiModel() } },
                taskRepository.getTasksByFolderId(0).map { tasks -> tasks.map { it.toListItemUiModel() } },
            ) { folders, upcomingEvents, pendingTasks, notes, tasks ->
                HomeScreenUiState.Success(
                    HomeUiModel(
                        folders = folders,
                        upcomingEvents = upcomingEvents,
                        pendingTasks = pendingTasks,
                        notes = notes,
                        tasks = tasks,
                        parentFolder = parentFolder
                    )
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
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