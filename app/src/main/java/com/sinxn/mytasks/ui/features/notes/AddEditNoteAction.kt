package com.sinxn.mytasks.ui.features.notes

import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.data.local.entities.Note

sealed class AddEditNoteAction {
    data class UpdateNote(val note: Note) : AddEditNoteAction()
    data class InsertNote(val note: Note) : AddEditNoteAction()
    data class DeleteNote(val note: Note) : AddEditNoteAction()
    data class FetchNoteById(val noteId: Long) : AddEditNoteAction()
    data class FetchFolderById(val folderId: Long) : AddEditNoteAction()
    data class NewNoteByFolder(val folderId: Long) : AddEditNoteAction()
    data class OnSelectionAction(val action: SelectionAction) : AddEditNoteAction()
}
