package com.sinxn.mytasks.domain.usecase.note

import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface

data class NoteUseCases(
    val getNotes: GetNotes,
    val getArchivedNotes: GetArchivedNotes,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote,
    val archiveNote: ArchiveNote,
    val unarchiveNote: UnarchiveNote,
    val archiveNotes: ArchiveNotes,
    val unarchiveNotes: UnarchiveNotes,
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

class ArchiveNote(private val repository: NoteRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.archiveNote(id)
}

class UnarchiveNote(private val repository: NoteRepositoryInterface) {
    suspend operator fun invoke(id: Long) = repository.unarchiveNote(id)
}

class ArchiveNotes(private val repository: NoteRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>) = repository.archiveNotes(ids)
}

class UnarchiveNotes(private val repository: NoteRepositoryInterface) {
    suspend operator fun invoke(ids: List<Long>) = repository.unarchiveNotes(ids)
}
