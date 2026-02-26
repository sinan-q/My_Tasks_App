package com.sinxn.mytasks.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.core.SelectionActionHandler
import com.sinxn.mytasks.core.SelectionStateHolder
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.usecase.event.EventUseCases
import com.sinxn.mytasks.domain.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.folder.LockFolderUseCase
import com.sinxn.mytasks.domain.usecase.home.HomeUseCases
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.domain.usecase.pinned.PinnedUseCases
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.ui.features.events.toListItemUiModel
import com.sinxn.mytasks.ui.features.folders.toListItemUiModel
import com.sinxn.mytasks.ui.features.notes.list.toListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.toListItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
    private val eventUseCases: EventUseCases,
    private val pinnedUseCases: PinnedUseCases,
    private val folderRepository: FolderRepositoryInterface,
    private val deleteFolderAndItsContentsUseCase: DeleteFolderAndItsContentsUseCase,
    private val lockFolderUseCase: LockFolderUseCase,
    private val selectionActionHandler: SelectionActionHandler,
    private val selectionStateHolder: SelectionStateHolder) : ViewModel() {


    val selectedAction = selectionStateHolder.action
    val selectionCount = selectionStateHolder.selectionCount
    
    // Expose all items for search

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Computed search results based on query and all items
    val searchResults: StateFlow<List<Any>> = combine(
        _searchQuery,
        taskUseCases.getTasks(),
        eventUseCases.getEvents(),
        noteUseCases.getNotes(),
        folderUseCases.getFolders()
    ) { query, tasks, events, notes, folders ->
        if (query.isBlank()) {
            emptyList()
        } else {
            buildList {
                // Add matching tasks
                addAll(tasks.map { it.toListItemUiModel() }.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.description?.contains(query, ignoreCase = true) == true
                })
                
                // Add matching events
                addAll(events.map { it.toListItemUiModel() }.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.description?.contains(query, ignoreCase = true) == true
                })
                
                // Add matching notes
                addAll(notes.map { it.toListItemUiModel() }.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true)
                })
                
                // Add matching folders
                addAll(folders.map { it.toListItemUiModel() }.filter {
                    it.name.contains(query, ignoreCase = true)
                })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun onSelectionTask(id: Long) = viewModelScope.launch {
        taskUseCases.getTask(id)?.let { selectionStateHolder.toggleTask(it) }
    }

    fun onSelectionNote(id: Long) = viewModelScope.launch {
        noteUseCases.getNote(id)?.let { selectionStateHolder.toggleNote(it) }
    }

    fun onSelectionFolder(id: Long) = viewModelScope.launch {
        folderUseCases.getFolder(id).let { it?.let{ folder-> selectionStateHolder.toggleFolder(folder)} }
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
            // Combine pinned + selection into a Pair first (since combine supports max 5 flows)
            val pinnedAndSelection = combine(
                pinnedUseCases.getPinnedItems(),
                selectionStateHolder.selectedState
            ) { pinned, selected -> Pair(pinned, selected) }

            combine(
                taskUseCases.getTasks(),
                eventUseCases.getEvents(),
                noteUseCases.getNotes(),
                folderUseCases.getFolders(),
                pinnedAndSelection
            ) { allTasks, allEvents, allNotes, allFolders, (pinnedItems, selectedState) ->

                // Get parent folder (root folder for home screen)
                val parentFolder = folderRepository.getFolderById(0L)
                
                // Call refactored use case with all items - performs in-memory filtering
                val dashboardData = homeUseCases.getDashboardData(
                    allFolders = allFolders,
                    allEvents = allEvents,
                    allTasks = allTasks,
                    allNotes = allNotes,
                    pinnedItems = pinnedItems,
                    parentFolder = parentFolder,
                    parentFolderId = 0L
                )
                
                // Apply selection state to UI models
                HomeScreenUiState.Success(dashboardData.copy(
                    folders = dashboardData.folders.map { folderItem ->
                        folderItem.copy(
                            isSelected = selectedState.folders.any { it.folderId == folderItem.id }
                        )
                    },
                    tasks = dashboardData.tasks.map { taskItem ->
                        taskItem.copy(
                            isSelected = selectedState.tasks.any { it.id == taskItem.id }
                        )
                    },
                    notes = dashboardData.notes.map { noteItem ->
                        noteItem.copy(
                            isSelected = selectedState.notes.any { it.id == noteItem.id }
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



