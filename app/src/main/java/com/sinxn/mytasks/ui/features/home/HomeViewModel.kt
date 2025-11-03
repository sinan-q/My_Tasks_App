package com.sinxn.mytasks.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.ItemType
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val pinnedRepository: PinnedRepositoryInterface,
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
        folderRepository.getFolderById(id).let { selectionStore.toggleFolder(it) }
    }

    fun onAction(action: SelectionAction) = viewModelScope.launch {
        selectionStore.onAction(action)
    }

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            val parentFolder = folderRepository.getFolderById(0L)
            combine(
                flow=folderRepository.getSubFolders(0).map { folders -> folders.map { it.toListItemUiModel() } },
                flow2=eventRepository.getUpcomingEvents(4).map { events -> events.map { it.toListItemUiModel() } },
                flow3=taskRepository.getTasksWithDueDate().map { tasks -> tasks.map { it.toListItemUiModel() } },
                flow4=noteRepository.getNotesByFolderId(0).map { notes -> notes.map { it.toListItemUiModel() } },
                flow5=taskRepository.getTasksByFolderId(0).map { tasks -> tasks.map { it.toListItemUiModel() } },
                flow6=pinnedRepository.getPinnedItems().map { pinnedList -> pinnedList.map { pinned ->
                        when (pinned.itemType) {
                            ItemType.NOTE -> noteRepository.getNoteById(pinned.itemId)?.toListItemUiModel()

                            ItemType.TASK -> taskRepository.getTaskById(pinned.itemId)?.toListItemUiModel()
                            ItemType.EVENT -> eventRepository.getEventById(pinned.itemId)!!.toListItemUiModel()

                            ItemType.FOLDER -> folderRepository.getFolderById(pinned.itemId).toListItemUiModel()
                        }
                    }},
            ) { folders, upcomingEvents, pendingTasks, notes, tasks, pinned ->

                HomeScreenUiState.Success(
                    HomeUiModel(
                        folders = folders,
                        upcomingEvents = upcomingEvents,
                        pendingTasks = pendingTasks,
                        notes = notes,
                        tasks = tasks,
                        parentFolder = parentFolder,
                        pinnedItems = pinned.filterNotNull()
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
                lockFolderUseCase(
                    folder,
                    !folder.isLocked
                ) // Assuming use case takes folder and new lock state
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
inline fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> {
    return kotlinx.coroutines.flow.combine(flow, flow2, flow3, flow4, flow5, flow6) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
        )
    }
}
