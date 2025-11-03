package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.data.local.dao.NoteDao
import com.sinxn.mytasks.data.local.entities.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepositoryInterface {

    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    override fun getArchivedNotes(): Flow<List<Note>> = noteDao.getArchivedNotes()

    override suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    override suspend fun insertNotes(notes: List<Note>) = noteDao.insertNotes(notes)

    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    override suspend fun deleteNotes(notes: List<Note>) = noteDao.deleteNotes(notes)
    override suspend fun clearAllNotes() = noteDao.clearAllNotes()


    override suspend fun updateNote(note: Note) = noteDao.updateNote(note)


    override suspend fun getNoteById(noteId: Long): Note? = noteDao.getNoteById(noteId)

    override fun getNotesByFolderId(folderId: Long): Flow<List<Note>> = noteDao.getNotesByFolderId(folderId)

    override suspend fun archiveNote(noteId: Long) = noteDao.archiveNote(noteId)

    override suspend fun unarchiveNote(noteId: Long) = noteDao.unarchiveNote(noteId)

    override suspend fun archiveNotes(noteIds: List<Long>) = noteDao.archiveNotes(noteIds)

    override suspend fun unarchiveNotes(noteIds: List<Long>) = noteDao.unarchiveNotes(noteIds)
}