package com.sinxn.mytasks.domain.repository

import com.sinxn.mytasks.domain.models.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepositoryInterface {
    suspend fun insertNote(note: Note)
    suspend fun insertNotes(notes: List<Note>)
    suspend fun deleteNote(note: Note): Int
    suspend fun deleteNotes(notes: List<Note>)
    suspend fun clearAllNotes()
    suspend fun getNoteById(noteId: Long): Note?
    fun getAllNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>

    suspend fun updateNote(note: Note)
    fun getNotesByFolderId(folderId: Long): Flow<List<Note>>

    suspend fun archiveNote(noteId: Long)
    suspend fun unarchiveNote(noteId: Long)
    suspend fun archiveNotes(noteIds: List<Long>)
    suspend fun unarchiveNotes(noteIds: List<Long>)
}