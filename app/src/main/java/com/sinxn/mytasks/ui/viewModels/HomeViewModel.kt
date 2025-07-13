package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.interfaces.EventRepositoryInterface
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.interfaces.NoteRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.store.SelectionActions
import com.sinxn.mytasks.data.store.SelectionStore
import com.sinxn.mytasks.data.usecase.folder.AddFolderUseCase
import com.sinxn.mytasks.data.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.data.usecase.folder.LockFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    noteRepository: NoteRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    eventRepository: EventRepositoryInterface,
    folderRepo: FolderRepositoryInterface,
    private val addFolderUseCase: AddFolderUseCase,
    private val deleteFolderAndItsContentsUseCase: DeleteFolderAndItsContentsUseCase,
    private val lockFolderUseCase: LockFolderUseCase,
    private val selectionStore: SelectionStore,
    ) : BaseViewModel(folderRepo) {

    val selectedTasks = selectionStore.selectedTasks
    val selectedAction = selectionStore.action

    fun onSelectionTask(task: Task) = selectionStore.toggleTask(task)

    val events = eventRepository.getUpcomingEvents(3).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
    val mainFolders = folderRepository.getSubFolders(0).stateIn(
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

    fun setSelectionAction(action: SelectionActions) = selectionStore.setAction(action)

    fun pasteSelection() {
        viewModelScope.launch {
            selectedTasks.value.forEach {
                taskRepository.insertTask(it.copy(id = null, folderId = 0L))
            }
            if (selectedAction.value == SelectionActions.CUT) {
                deleteTasks()
            }
            clearSelection()
            showToast("Tasks Pasted")
        }

    }

    fun clearSelection() {
        selectionStore.clear()
        selectionStore.setAction(SelectionActions.NONE)
    }

    fun deleteTasks() {
        viewModelScope.launch {
            try {
                selectedTasks.value.forEach {
                    taskRepository.deleteTask(it)
                }
                selectionStore.clear()
                showToast("Tasks Deleted")
            } catch (e: Exception) {
                showToast("Error deleting tasks: ${e.message}")
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


}
