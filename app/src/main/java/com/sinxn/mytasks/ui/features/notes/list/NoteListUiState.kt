package com.sinxn.mytasks.ui.features.notes.list

import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.models.Note

sealed class NoteScreenUiState {
    object Loading : NoteScreenUiState()
    data class Success(
        val note: Note,
        val notes: List<NoteListItemUiModel>,
        val folder: Folder?,
        val folders: List<Folder>
    ) : NoteScreenUiState()
    data class Error(val message: String) : NoteScreenUiState()
}