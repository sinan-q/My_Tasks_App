package com.sinxn.mytasks.core

import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.usecase.folder.CopyFolderAndItsContentsUseCase
import com.sinxn.mytasks.data.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Singleton
class SelectionStore @Inject constructor(
    private val taskRepository: TaskRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
    private val copyFolderAndItsContentsUseCase: CopyFolderAndItsContentsUseCase,
    private val deleteFolderAndItsContentsUseCase: DeleteFolderAndItsContentsUseCase
) {
    private val _selectedTasks = MutableStateFlow<Set<Task>>(emptySet())
    val selectedTasks: StateFlow<Set<Task>> = _selectedTasks

    private val _selectedNotes = MutableStateFlow<Set<Note>>(emptySet())
    val selectedNotes: StateFlow<Set<Note>> = _selectedNotes

    // Add StateFlow for Folders if you don't have one
    private val _selectedFolders = MutableStateFlow<Set<Folder>>(emptySet())
    val selectedFolders: StateFlow<Set<Folder>> = _selectedFolders

    private val _action = MutableStateFlow<SelectionActions>(SelectionActions.NONE)
    val action: StateFlow<SelectionActions> = _action

    private val _selectionCount = MutableStateFlow(0)
    val selectionCount: StateFlow<Int> = _selectionCount

    // Specific toggle function for Task
    fun toggleTask(task: Task) = _selectedTasks.update { current ->
        if (task in current) current - task else current + task

    }.also { updateSelectionCount() }

    // Specific toggle function for Note
    fun toggleNote(note: Note) = _selectedNotes.update { current ->
        if (note in current) current - note else current + note
    }.also { updateSelectionCount() }

    // Specific toggle function for Folder
    fun toggleFolder(folder: Folder) = _selectedFolders.update { current ->
        if (folder in current) current - folder else current + folder
    }.also { updateSelectionCount() }

    fun setAction(action: SelectionActions) {
        _action.update { action }
    }

    suspend fun pasteSelection(folderId: Long) {
        if (action.value == SelectionActions.COPY){
            selectedFolders.value.forEach {
                copyFolderAndItsContentsUseCase(it, parentId = folderId)
            }
            selectedTasks.value.forEach {
                taskRepository.insertTask(it.copy(id = null, folderId = folderId ))
            }
            selectedNotes.value.forEach {
                noteRepository.insertNote(it.copy(id = null, folderId = folderId))
            }

        } else if (action.value == SelectionActions.CUT) {
            selectedTasks.value.forEach {
                taskRepository.updateTask(it.copy(folderId = folderId))
            }
            selectedNotes.value.forEach {
                noteRepository.updateNote(it.copy(folderId = folderId))
            }
            selectedFolders.value.forEach {
                folderRepository.updateFolder(it.copy(parentFolderId = folderId))
            }
        }
        clearSelection()
    }

    private fun updateSelectionCount() {
        _selectionCount.value = _selectedTasks.value.size + _selectedNotes.value.size + _selectedFolders.value.size
    }

    fun clearSelection() {
        _selectedTasks.update { emptySet() }
        _selectedNotes.update { emptySet() }
        _selectedFolders.update { emptySet() }
        _action.update { SelectionActions.NONE }
        setAction(SelectionActions.NONE)
        updateSelectionCount()
    }

    suspend fun deleteSelection() {
        taskRepository.deleteTasks(selectedTasks.value.toList())
        noteRepository.deleteNotes(selectedNotes.value.toList())
        selectedFolders.value.forEach {
            deleteFolderAndItsContentsUseCase(it)
        }
        clearSelection()
    }
}

enum class SelectionActions {
    CUT, COPY, DELETE, NONE
}
