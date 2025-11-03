package com.sinxn.mytasks.core

import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.ItemType
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Pinned
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.domain.usecase.pinned.PinnedUseCases
import com.sinxn.mytasks.domain.usecase.selection.DeleteSelectionUseCase
import com.sinxn.mytasks.domain.usecase.selection.PasteSelectionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectionStore @Inject constructor(
    private val pasteSelectionUseCase: PasteSelectionUseCase,
    private val deleteSelectionUseCase: DeleteSelectionUseCase,
    private val pinnedUseCases: PinnedUseCases
) {
    private val _selectedTasks = MutableStateFlow<Set<Task>>(emptySet())
    val selectedTasks: StateFlow<Set<Task>> = _selectedTasks

    private val _selectedNotes = MutableStateFlow<Set<Note>>(emptySet())
    val selectedNotes: StateFlow<Set<Note>> = _selectedNotes

    private val _selectedFolders = MutableStateFlow<Set<Folder>>(emptySet())
    val selectedFolders: StateFlow<Set<Folder>> = _selectedFolders

    private val _action = MutableStateFlow(SelectionActions.NONE)
    val action: StateFlow<SelectionActions> = _action

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


    fun setAction(action: SelectionActions) {
        _action.update { action }
    }

    suspend fun pasteSelection(folderId: Long) {
        pasteSelectionUseCase(
            action = action.value,
            selectedTasks = selectedTasks.value,
            selectedNotes = selectedNotes.value,
            selectedFolders = selectedFolders.value,
            destinationFolderId = folderId
        )
        clearSelection()
    }

    suspend fun pinSelection() {
        val itemsToPin = mutableListOf<Pinned>()

        _selectedNotes.value.forEach { note ->
            note.id != null && itemsToPin.add(Pinned(itemId = note.id, itemType = ItemType.NOTE))
        }
        _selectedTasks.value.forEach { task ->
            task.id != null && itemsToPin.add(Pinned(itemId = task.id, itemType = ItemType.TASK))
        }
        _selectedFolders.value.forEach { folder ->
            itemsToPin.add(Pinned(itemId = folder.folderId, itemType = ItemType.FOLDER))
        }

        if (itemsToPin.isNotEmpty()) {
            pinnedUseCases.insertPinnedItems(itemsToPin)
        }
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
        deleteSelectionUseCase(
            selectedTasks = selectedTasks.value,
            selectedNotes = selectedNotes.value,
            selectedFolders = selectedFolders.value
        )
        clearSelection()
    }
}

enum class SelectionActions {
    CUT, COPY, DELETE, NONE
}
