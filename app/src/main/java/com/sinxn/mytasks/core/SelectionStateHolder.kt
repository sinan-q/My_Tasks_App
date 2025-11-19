package com.sinxn.mytasks.core

import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

data class SelectedItems(
    val tasks: Set<Task> = emptySet(),
    val notes: Set<Note> = emptySet(),
    val folders: Set<Folder> = emptySet(),
)
@Singleton
class SelectionStateHolder @Inject constructor() {
    private val _selectedState = MutableStateFlow<SelectedItems>(SelectedItems())
    val selectedState: StateFlow<SelectedItems> = _selectedState

    private val _action = MutableStateFlow<SelectionAction>(SelectionAction.None)
    val action: StateFlow<SelectionAction> = _action

    private val _selectionCount = MutableStateFlow(0)
    val selectionCount: StateFlow<Int> = _selectionCount

    private fun <T> toggle(item: T) {
        _selectedState.update { current ->
            fun <E> Set<E>.toggle(element: E): Set<E> =
                if (element in this) this - element else this + element

            when (item) {
                is Task -> current.copy(tasks = current.tasks.toggle(item))
                is Note -> current.copy(notes = current.notes.toggle(item))
                is Folder -> current.copy(folders = current.folders.toggle(item))
                else -> current
            }
        }
        updateSelectionCount()
    }

    fun toggleTask(task: Task) = toggle(task)

    fun toggleNote(note: Note) = toggle(note )

    fun toggleFolder(folder: Folder) = toggle(folder)

    fun setAction(action: SelectionAction) {
        _action.update { action }
    }

    private fun updateSelectionCount() {
        _selectionCount.value = _selectedState.value.let {
            it.tasks.size + it.notes.size + it.folders.size
        }
    }

    fun clearSelection() {
        _selectedState.update { SelectedItems() }
        setAction(SelectionAction.None)
        updateSelectionCount()
    }
}
