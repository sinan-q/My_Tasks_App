package com.sinxn.mytasks.core

import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectionStateHolder @Inject constructor() {
    private val _selectedTasks = MutableStateFlow<Set<Task>>(emptySet())
    val selectedTasks: StateFlow<Set<Task>> = _selectedTasks

    private val _selectedNotes = MutableStateFlow<Set<Note>>(emptySet())
    val selectedNotes: StateFlow<Set<Note>> = _selectedNotes

    private val _selectedFolders = MutableStateFlow<Set<Folder>>(emptySet())
    val selectedFolders: StateFlow<Set<Folder>> = _selectedFolders

    private val _action = MutableStateFlow<SelectionAction>(SelectionAction.None)
    val action: StateFlow<SelectionAction> = _action

    private val _selectionCount = MutableStateFlow(0)
    val selectionCount: StateFlow<Int> = _selectionCount

    private fun <T> toggle(item: T, flow: MutableStateFlow<Set<T>>) {
        flow.update { current ->
            if (item in current) current - item else current + item
        }
        updateSelectionCount()
    }

    fun toggleTask(task: Task) = toggle(task, _selectedTasks)

    fun toggleNote(note: Note) = toggle(note, _selectedNotes)

    fun toggleFolder(folder: Folder) = toggle(folder, _selectedFolders)

    fun setAction(action: SelectionAction) {
        _action.update { action }
    }

    private fun updateSelectionCount() {
        _selectionCount.value = _selectedTasks.value.size + _selectedNotes.value.size + _selectedFolders.value.size
    }

    fun clearSelection() {
        _selectedTasks.update { emptySet() }
        _selectedNotes.update { emptySet() }
        _selectedFolders.update { emptySet() }
        setAction(SelectionAction.None)
        updateSelectionCount()
    }
}
