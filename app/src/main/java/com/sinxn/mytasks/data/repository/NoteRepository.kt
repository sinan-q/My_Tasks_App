package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.interfaces.NoteRepositoryInterface
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

    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    override suspend fun getNoteById(noteId: Long): Note {
        return noteDao.getNoteById(noteId)
    }

    override fun getNotesByFolderId(folderId: Long): Flow<List<Note>> {
        return noteDao.getNotesByFolderId(folderId)

    }
}