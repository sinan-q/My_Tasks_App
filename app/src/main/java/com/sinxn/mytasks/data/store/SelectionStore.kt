package com.sinxn.mytasks.data.store

import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.data.repository.FolderRepository
import com.sinxn.mytasks.data.repository.NoteRepository
import com.sinxn.mytasks.data.repository.TaskRepository
import com.sinxn.mytasks.data.usecase.folder.CopyFolderAndItsContentsUseCase
import com.sinxn.mytasks.data.usecase.folder.DeleteFolderAndItsContentsUseCase
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Singleton
class SelectionStore @Inject constructor(
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
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

    // Specific toggle function for Task
    fun toggleTask(task: Task) = _selectedTasks.update { current ->
        if (task in current) current - task else current + task
    }

    // Specific toggle function for Note
    fun toggleNote(note: Note) = _selectedNotes.update { current ->
        if (note in current) current - note else current + note
    }

    // Specific toggle function for Folder
    fun toggleFolder(folder: Folder) = _selectedFolders.update { current ->
        if (folder in current) current - folder else current + folder
    }

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

    fun clearSelection() {
        _selectedTasks.update { emptySet() }
        _selectedNotes.update { emptySet() }
        _selectedFolders.update { emptySet() }
        _action.update { SelectionActions.NONE }
        setAction(SelectionActions.NONE)
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
