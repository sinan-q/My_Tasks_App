package com.sinxn.mytasks.ui.screens.noteScreen

import androidx.compose.runtime.saveable.Saver
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

val NoteSaver = Saver<Note, List<Any?>>(
    save = { note ->
        listOf(
            note.id,
            note.folderId,
            note.title,
            note.content,
            note.timestamp.toMillis() // Convert LocalDateTime to String
        )
    },
    restore = { list ->
        val id = list[0] as Long?
        val folderId = list[1] as Long
        val title = list[2] as String
        val content = list[3] as String
        val timestampString = list[4] as Long

        val timestamp = try {
            fromMillis(timestampString) // Convert String back to LocalDateTime
        } catch (e: DateTimeParseException) {
            // Handle error or default if parsing fails, though it shouldn't if saved correctly
            LocalDateTime.now()
        }

        Note(id, folderId, title, content, timestamp)
    }
)