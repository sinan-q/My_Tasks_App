package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.NoteDao
import com.sinxn.mytasks.data.mapper.toDomain
import com.sinxn.mytasks.data.mapper.toEntity
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepositoryInterface {

    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes().map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)

    override fun getArchivedNotes(): Flow<List<Note>> = noteDao.getArchivedNotes().map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)

    override suspend fun insertNote(note: Note): Long = noteDao.insertNote(note.toEntity())
    override suspend fun insertNotes(notes: List<Note>) = noteDao.insertNotes(notes.map { it.toEntity() })

    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note.toEntity())
    override suspend fun deleteNotes(notes: List<Note>) = noteDao.deleteNotes(notes.map { it.toEntity() })
    override suspend fun clearAllNotes() = noteDao.clearAllNotes()

    override suspend fun updateNote(note: Note) = noteDao.updateNote(note.toEntity())

    override suspend fun getNoteById(noteId: Long): Note? = noteDao.getNoteById(noteId)?.toDomain()

    override fun getNotesByFolderId(folderId: Long): Flow<List<Note>> = noteDao.getNotesByFolderId(folderId).map { it.map { e -> e.toDomain() } }.flowOn(Dispatchers.IO)

    override suspend fun archiveNote(noteId: Long) = noteDao.archiveNote(noteId)

    override suspend fun unarchiveNote(noteId: Long) = noteDao.unarchiveNote(noteId)

    override suspend fun archiveNotes(noteIds: List<Long>) = noteDao.archiveNotes(noteIds)

    override suspend fun unarchiveNotes(noteIds: List<Long>) = noteDao.unarchiveNotes(noteIds)
}