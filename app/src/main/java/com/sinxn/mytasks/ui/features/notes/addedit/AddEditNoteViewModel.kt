package com.sinxn.mytasks.ui.features.notes.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.ui.features.notes.list.NoteScreenUiState
import com.sinxn.mytasks.ui.features.notes.list.toListItemUiModel
import com.sinxn.mytasks.utils.Constants
import com.sinxn.mytasks.domain.models.RelationItemType
import com.sinxn.mytasks.domain.models.ItemRelation
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.domain.usecase.event.EventUseCases
import com.sinxn.mytasks.domain.usecase.relation.ItemRelationUseCases
import com.sinxn.mytasks.ui.components.ParentItemOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val folderUseCases: FolderUseCases,
    private val itemRelationUseCases: ItemRelationUseCases,
    private val taskUseCases: TaskUseCases,
    private val eventUseCases: EventUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteScreenUiState>(NoteScreenUiState.Loading)
    val uiState: StateFlow<NoteScreenUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    val allTasks = taskUseCases.getTasks()
    val allEvents = eventUseCases.getEvents()
    val allNotes = noteUseCases.getNotes()

    fun onAction(action: AddEditNoteAction) {
        when (action) {
            is AddEditNoteAction.UpdateNote -> onNoteUpdate(action.note)
            is AddEditNoteAction.InsertNote -> addNote(action.note)
            is AddEditNoteAction.DeleteNote -> deleteNote(action.note)
            is AddEditNoteAction.FetchNoteById -> fetchNoteById(action.noteId)
            is AddEditNoteAction.FetchFolderById -> fetchFolderById(action.folderId)
            is AddEditNoteAction.NewNoteByFolder -> newNoteByFolder(action.folderId)
            is AddEditNoteAction.SetParent -> setParent(action.parent)
            is AddEditNoteAction.RemoveParent -> removeParent()
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    private fun onNoteUpdate(note: Note) {
        val currentState = _uiState.value
        if (currentState is NoteScreenUiState.Success) {
            _uiState.value = currentState.copy(note = note)
        }
    }

    private fun addNote(note: Note) {
        viewModelScope.launch {
            if (note.title.isEmpty() && note.content.isEmpty()) {
                showToast(Constants.SAVE_FAILED_EMPTY)
                return@launch
            }

            var noteId = note.id
            if (noteId != null) {
                noteUseCases.updateNote(note)
            } else {
                noteId = noteUseCases.addNote(note)
            }

            // Handle Parent Relation
            val currentState = _uiState.value
            if (currentState is NoteScreenUiState.Success) {
                val currentParent = currentState.parentItem
                if (currentParent != null) {
                    itemRelationUseCases.addRelation(
                        ItemRelation(
                            parentId = currentParent.id,
                            parentType = currentParent.type,
                            childId = noteId,
                            childType = RelationItemType.NOTE
                        )
                    )
                } else {
                    itemRelationUseCases.removeRelationsForItem(noteId, RelationItemType.NOTE)
                }
            }

            showToast(Constants.SAVE_SUCCESS)
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch {
            val deleted = noteUseCases.deleteNote(note)
            if (deleted == 0) {
                showToast(Constants.DELETE_FAILED)
                return@launch
            }
            showToast(Constants.DELETE_SUCCESS)
        }
    }

    private fun fetchNoteById(noteId: Long) {
        viewModelScope.launch {
            _uiState.value = NoteScreenUiState.Loading
            try {
                val fetchedNote = noteUseCases.getNote(noteId)
                if (fetchedNote == null) {
                    _uiState.value = NoteScreenUiState.Error(Constants.NOT_FOUND)
                    return@launch
                }
                val fetchedFolder = folderUseCases.getFolder(fetchedNote.folderId)
                val subFolders = folderUseCases.getSubFolders(fetchedNote.folderId).first()
                val notes = noteUseCases.getNotes().map { notes -> notes.map { it.toListItemUiModel() } }.first()
                
                // Fetch Parent
                val parentRelation = itemRelationUseCases.getParent(noteId, RelationItemType.NOTE).first()
                val parentOption = parentRelation?.let { fetchParentDetails(it) }

                // Fetch Children
                val childrenRelations = itemRelationUseCases.getChildren(noteId, RelationItemType.NOTE).first()
                val childrenOptions = fetchChildrenDetails(childrenRelations)

                _uiState.value = NoteScreenUiState.Success(
                    note = fetchedNote,
                    notes = notes,
                    folder = fetchedFolder,
                    folders = subFolders,
                    parentItem = parentOption,
                    relatedItems = childrenOptions
                )
            } catch (e: Exception) {
                _uiState.value = NoteScreenUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderUseCases.getFolder(folderId)
            val subFolders = folderUseCases.getSubFolders(folderId).first()
            val currentState = _uiState.value
            if (currentState is NoteScreenUiState.Success) {
                _uiState.value = currentState.copy(
                    folder = fetchedFolder,
                    folders = subFolders,
                    note = currentState.note.copy(folderId = folderId)
                )
            } else {
                 val notes = noteUseCases.getNotes().map { notes -> notes.map { it.toListItemUiModel() } }.first()
                _uiState.value = NoteScreenUiState.Success(
                    note = Note(folderId = folderId),
                    notes = notes,
                    folder = fetchedFolder,
                    folders = subFolders
                )
            }
        }
    }

    private fun newNoteByFolder(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderUseCases.getFolder(folderId)
            val subFolders = folderUseCases.getSubFolders(folderId).first()
            val notes = noteUseCases.getNotes().map { notes -> notes.map { it.toListItemUiModel() } }.first()
            _uiState.value = NoteScreenUiState.Success(
                note = Note(folderId = folderId),
                notes = notes,
                folder = fetchedFolder,
                folders = subFolders
            )
        }
    }

    private fun setParent(parent: ParentItemOption) {
        val currentState = _uiState.value
        if (currentState is NoteScreenUiState.Success) {
            _uiState.value = currentState.copy(parentItem = parent)
        }
    }

    private fun removeParent() {
        val currentState = _uiState.value
        if (currentState is NoteScreenUiState.Success) {
            _uiState.value = currentState.copy(parentItem = null)
        }
    }

    private suspend fun fetchParentDetails(relation: ItemRelation): ParentItemOption? {
        return try {
            val title = when (relation.parentType) {
                RelationItemType.TASK -> taskUseCases.getTask(relation.parentId)?.title
                RelationItemType.EVENT -> eventUseCases.getEvent(relation.parentId)?.title
                RelationItemType.NOTE -> noteUseCases.getNote(relation.parentId)?.title
            }
            title?.let { ParentItemOption(relation.parentId, it, relation.parentType) }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchChildrenDetails(relations: List<ItemRelation>): List<ParentItemOption> {
        return relations.mapNotNull { relation ->
            try {
                val title = when (relation.childType) {
                    RelationItemType.TASK -> taskUseCases.getTask(relation.childId)?.title
                    RelationItemType.EVENT -> eventUseCases.getEvent(relation.childId)?.title
                    RelationItemType.NOTE -> noteUseCases.getNote(relation.childId)?.title
                }
                title?.let { ParentItemOption(relation.childId, it, relation.childType) }
            } catch (e: Exception) {
                null
            }
        }
    }
}
