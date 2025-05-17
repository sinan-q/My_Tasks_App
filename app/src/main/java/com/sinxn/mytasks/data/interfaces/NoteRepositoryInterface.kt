package com.sinxn.mytasks.data.interfaces

import com.sinxn.mytasks.data.local.entities.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepositoryInterface {
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun getNoteById(noteId: Long): Note
    fun getAllNotes(): Flow<List<Note>>

    suspend fun updateNote(note: Note)
    fun getNotesByFolderId(folderId: Long): Flow<List<Note>>
}