package com.sinxn.mytasks.ui.features.notes.addedit


import com.sinxn.mytasks.domain.models.Note

sealed class AddEditNoteAction {
    data class UpdateNote(val note: Note) : AddEditNoteAction()
    data class InsertNote(val note: Note) : AddEditNoteAction()
    data class DeleteNote(val note: Note) : AddEditNoteAction()
    data class FetchNoteById(val noteId: Long) : AddEditNoteAction()
    data class FetchFolderById(val folderId: Long) : AddEditNoteAction()
    data class NewNoteByFolder(val folderId: Long) : AddEditNoteAction()

}
