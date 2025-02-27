package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.NoteDao
import com.sinxn.mytasks.data.local.entities.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteById(noteId)
    }

    fun getNotesByFolderId(folderId: Long?): Flow<List<Note>> {
        return noteDao.getNotesByFolderId(folderId)

    }
}