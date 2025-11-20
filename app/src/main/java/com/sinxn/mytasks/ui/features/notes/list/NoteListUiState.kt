package com.sinxn.mytasks.ui.features.notes.list

import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.models.Note

sealed class NoteScreenUiState {
    object Loading : NoteScreenUiState()
    data class Success(
        val note: Note,
        val notes: List<NoteListItemUiModel>,
        val folder: Folder?,
        val folders: List<Folder>,
        val parentItem: com.sinxn.mytasks.ui.components.ParentItemOption? = null,
        val relatedItems: List<com.sinxn.mytasks.ui.components.ParentItemOption> = emptyList()
    ) : NoteScreenUiState()
    data class Error(val message: String) : NoteScreenUiState()
}