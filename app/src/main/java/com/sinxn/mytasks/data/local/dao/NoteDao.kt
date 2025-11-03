package com.sinxn.mytasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sinxn.mytasks.data.local.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isArchived = 1 ORDER BY timestamp DESC")
    fun getArchivedNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<Note>)

    @Delete
    suspend fun deleteNote(note: Note): Int

    @Delete
    suspend fun deleteNotes(notes: List<Note>)

    @Query("DELETE FROM notes")
    suspend fun clearAllNotes()

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM notes WHERE id = :noteId AND isArchived = 0 LIMIT 1")
    suspend fun getNoteById(noteId: Long): Note?

    @Query("SELECT * FROM notes WHERE folderId = :folderId AND isArchived = 0")
    fun getNotesByFolderId(folderId: Long?): Flow<List<Note>>

    @Query("UPDATE notes SET isArchived = 1 WHERE id = :noteId")
    suspend fun archiveNote(noteId: Long)

    @Query("UPDATE notes SET isArchived = 0 WHERE id = :noteId")
    suspend fun unarchiveNote(noteId: Long)

    @Query("UPDATE notes SET isArchived = 1 WHERE id IN (:noteIds)")
    suspend fun archiveNotes(noteIds: List<Long>)

    @Query("UPDATE notes SET isArchived = 0 WHERE id IN (:noteIds)")
    suspend fun unarchiveNotes(noteIds: List<Long>)
}