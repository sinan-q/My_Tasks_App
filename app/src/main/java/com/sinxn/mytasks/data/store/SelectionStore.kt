package com.sinxn.mytasks.data.store

import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Singleton
class SelectionStore @Inject constructor() {
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


    fun clear() {
        _selectedTasks.update { emptySet() }
        _selectedNotes.update { emptySet() }
        _selectedFolders.update { emptySet() }
        _action.update { SelectionActions.NONE }
    }

    fun setAction(action: SelectionActions) {
        _action.update { action }
    }
}

enum class SelectionActions {
    CUT, COPY, DELETE, NONE
}
