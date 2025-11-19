package com.sinxn.mytasks.domain.usecase.note

import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface

data class NoteUseCases(
    val getNotes: GetNotes,
    val getArchivedNotes: GetArchivedNotes,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote,
    val toggleArchive: ToggleNoteArchive,
    val toggleArchives: ToggleNotesArchive,
)

class GetNotes(private val repository: NoteRepositoryInterface) {
    operator fun invoke() = repository.getAllNotes()
}

class GetArchivedNotes(private val repository: NoteRepositoryInterface) {
    operator fun invoke() = repository.getArchivedNotes()
}

class DeleteNote(private val repository: NoteRepositoryInterface) {
    suspend operator fun invoke(note: Note) = repository.deleteNote(note)
}

class AddNote(private val repository: NoteRepositoryInterface) {
    suspend operator fun invoke(note: Note) = repository.insertNote(note)
}

class GetNote(private val repository: NoteRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.getNoteById(id)
}

class ToggleNoteArchive(private val repository: NoteRepositoryInterface) {
    suspend operator fun invoke(id: Long, archive: Boolean) {
        if (archive) repository.archiveNote(id) else repository.unarchiveNote(id)
    }
}

class ToggleNotesArchive(private val repository: NoteRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>, archive: Boolean) {
        if (ids.isEmpty()) return
        if (archive) repository.archiveNotes(ids) else repository.unarchiveNotes(ids)
    }
}
