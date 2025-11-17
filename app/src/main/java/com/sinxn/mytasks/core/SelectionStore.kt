package com.sinxn.mytasks.core

import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.ItemType
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Pinned
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.domain.usecase.pinned.PinnedUseCases
import com.sinxn.mytasks.domain.usecase.selection.DeleteSelectionUseCase
import com.sinxn.mytasks.domain.usecase.selection.PasteSelectionUseCase
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectionStore @Inject constructor(
    private val pasteSelectionUseCase: PasteSelectionUseCase,
    private val deleteSelectionUseCase: DeleteSelectionUseCase,
    private val pinnedUseCases: PinnedUseCases,
    private val noteUseCases: NoteUseCases,
    private val taskUseCases: TaskUseCases,
    private val folderUseCases: FolderUseCases
) {
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


    private fun setAction(action: SelectionAction) {
        _action.update { action }
    }

    suspend fun onAction(action: SelectionAction) {
        when (action) {
            is SelectionAction.Cut -> setAction(SelectionAction.Cut)
            is SelectionAction.Copy -> setAction(SelectionAction.Copy)
            is SelectionAction.Delete -> setAction(SelectionAction.Delete)
            is SelectionAction.None -> clearSelection()
            is SelectionAction.Paste -> pasteSelection(action.folderId)
            is SelectionAction.Pin -> togglePinSelection()
            is SelectionAction.Archive -> toggleArchiveSelection(true)
            is SelectionAction.Unarchive -> toggleArchiveSelection(false)
            is SelectionAction.DeleteConfirm -> deleteSelection(action.confirm)

        }
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

    suspend fun togglePinSelection() {
        val itemsToPin = mutableListOf<Pinned>()
        val itemsToUnpin = mutableListOf<Pinned>()

        _selectedNotes.value.forEach { note -> note.id?.let {
            val pin = Pinned(itemId = note.id, itemType = ItemType.NOTE)
            val isExisting = pinnedUseCases.isPinned(pin.itemId, pin.itemType)
            if (isExisting == null ) itemsToPin.add(pin) else itemsToUnpin.add(isExisting)
        }}
        _selectedTasks.value.forEach { task -> task.id?.let {
            val pin = Pinned(itemId = task.id, itemType = ItemType.TASK)
            val isExisting = pinnedUseCases.isPinned(pin.itemId, pin.itemType)
            if (isExisting == null ) itemsToPin.add(pin) else itemsToUnpin.add(isExisting)
        }}
        _selectedFolders.value.forEach { folder ->
            val pin = Pinned(itemId = folder.folderId, itemType = ItemType.FOLDER)
            val isExisting = pinnedUseCases.isPinned(pin.itemId, pin.itemType)
            if (isExisting == null ) itemsToPin.add(pin) else itemsToUnpin.add(isExisting)
        }

        if (itemsToPin.isNotEmpty()) {
            pinnedUseCases.insertPinnedItems(itemsToPin)
        }
        if (itemsToUnpin.isNotEmpty()) {
            pinnedUseCases.deletePinnedItems(itemsToUnpin)
        }
        clearSelection()
    }

    private suspend fun toggleArchiveSelection(archive: Boolean) {
        val noteIds = _selectedNotes.value.mapNotNull { it.id }
        val taskIds = _selectedTasks.value.mapNotNull { it.id }
        val folderIds = _selectedFolders.value.map { it.folderId }

        if (archive) {
            if (noteIds.isNotEmpty()) noteUseCases.archiveNotes(noteIds)
            if (taskIds.isNotEmpty()) taskUseCases.archiveTasks(taskIds)
            if (folderIds.isNotEmpty()) folderUseCases.archiveFolders(folderIds)
        } else {
            if (noteIds.isNotEmpty()) noteUseCases.unarchiveNotes(noteIds)
            if (taskIds.isNotEmpty()) taskUseCases.unarchiveTasks(taskIds)
            if (folderIds.isNotEmpty()) folderUseCases.unarchiveFolders(folderIds)
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
        setAction(SelectionAction.None)
        updateSelectionCount()
    }

    suspend fun deleteSelection(confirmed: Boolean) {
        if (!confirmed) return // If not confirmed, do nothing
        deleteSelectionUseCase(
            selectedTasks = selectedTasks.value,
            selectedNotes = selectedNotes.value,
            selectedFolders = selectedFolders.value
        )
        clearSelection()
    }
}

sealed class SelectionAction {
    data object Copy : SelectionAction()
    data object Cut : SelectionAction()
    data class Paste(val folderId: Long) : SelectionAction()

    data object Delete : SelectionAction()
    data class DeleteConfirm(val confirm: Boolean) : SelectionAction()
    data object None : SelectionAction()
    data object Pin : SelectionAction()
    data object Archive : SelectionAction()
    data object Unarchive : SelectionAction()
}
