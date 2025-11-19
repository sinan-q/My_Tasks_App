package com.sinxn.mytasks.core

import com.sinxn.mytasks.domain.models.ItemType
import com.sinxn.mytasks.domain.models.Pinned
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.domain.usecase.pinned.PinnedUseCases
import com.sinxn.mytasks.domain.usecase.selection.DeleteSelectionUseCase
import com.sinxn.mytasks.domain.usecase.selection.PasteSelectionUseCase
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectionActionHandler @Inject constructor(
    private val stateHolder: SelectionStateHolder,
    private val pasteSelectionUseCase: PasteSelectionUseCase,
    private val deleteSelectionUseCase: DeleteSelectionUseCase,
    private val pinnedUseCases: PinnedUseCases,
    private val noteUseCases: NoteUseCases,
    private val taskUseCases: TaskUseCases,
    private val folderUseCases: FolderUseCases
) {

    suspend fun onAction(action: SelectionAction) {
        when (action) {
            is SelectionAction.Cut -> stateHolder.setAction(SelectionAction.Cut)
            is SelectionAction.Copy -> stateHolder.setAction(SelectionAction.Copy)
            is SelectionAction.Delete -> stateHolder.setAction(SelectionAction.Delete)
            is SelectionAction.None -> stateHolder.clearSelection()
            is SelectionAction.Paste -> pasteSelection(action.folderId)
            is SelectionAction.Pin -> togglePinSelection()
            is SelectionAction.Archive -> toggleArchiveSelection(true)
            is SelectionAction.Unarchive -> toggleArchiveSelection(false)
            is SelectionAction.DeleteConfirm -> deleteSelection(action.confirm)
        }
    }

    private suspend fun pasteSelection(folderId: Long) {
        pasteSelectionUseCase(
            action = stateHolder.action.value,
            selectedTasks = stateHolder.selectedTasks.value,
            selectedNotes = stateHolder.selectedNotes.value,
            selectedFolders = stateHolder.selectedFolders.value,
            destinationFolderId = folderId
        )
        stateHolder.clearSelection()
    }

    private suspend fun togglePinSelection() {
        data class PinCandidate(val itemId: Long, val type: ItemType)

        fun toCandidates(): List<PinCandidate> = buildList {
            addAll(stateHolder.selectedNotes.value.mapNotNull { it.id?.let { id -> PinCandidate(id, ItemType.NOTE) } })
            addAll(stateHolder.selectedTasks.value.mapNotNull { it.id?.let { id -> PinCandidate(id, ItemType.TASK) } })
            addAll(stateHolder.selectedFolders.value.map { PinCandidate(it.folderId, ItemType.FOLDER) })
        }

        val toPin = mutableListOf<Pinned>()
        val toUnpin = mutableListOf<Pinned>()

        toCandidates().forEach { c ->
            val existing = pinnedUseCases.isPinned(c.itemId, c.type)
            if (existing == null) toPin.add(Pinned(itemId = c.itemId, itemType = c.type)) else toUnpin.add(existing)
        }

        if (toPin.isNotEmpty()) pinnedUseCases.insertPinnedItems(toPin)
        if (toUnpin.isNotEmpty()) pinnedUseCases.deletePinnedItems(toUnpin)
        stateHolder.clearSelection()
    }

    private suspend fun toggleArchiveSelection(archive: Boolean) {
        val noteIds = stateHolder.selectedNotes.value.mapNotNull { it.id }
        val taskIds = stateHolder.selectedTasks.value.mapNotNull { it.id }
        val folderIds = stateHolder.selectedFolders.value.map { it.folderId }

        if (noteIds.isNotEmpty()) noteUseCases.toggleArchives(noteIds, archive)
        if (taskIds.isNotEmpty()) taskUseCases.toggleArchives(taskIds, archive)
        if (folderIds.isNotEmpty()) folderUseCases.toggleArchives(folderIds, archive)

        stateHolder.clearSelection()
    }

    private suspend fun deleteSelection(confirmed: Boolean) {
        if (!confirmed) return // If not confirmed, do nothing
        deleteSelectionUseCase(
            selectedTasks = stateHolder.selectedTasks.value,
            selectedNotes = stateHolder.selectedNotes.value,
            selectedFolders = stateHolder.selectedFolders.value
        )
        stateHolder.clearSelection()
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

