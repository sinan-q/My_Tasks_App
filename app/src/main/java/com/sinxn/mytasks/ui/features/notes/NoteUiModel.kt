package com.sinxn.mytasks.ui.features.notes

import com.sinxn.mytasks.data.local.entities.Note
import java.time.format.DateTimeFormatter

/**
 * A UI-specific model representing a note to be displayed in a list.
 * This class is tailored for the UI and contains only the data needed for rendering the list item.
 */
data class NoteListItemUiModel(
    val id: Long,
    val title: String,
    val lastModified: String,
)

/**
 * Mapper function to convert a [Note] data entity to a [NoteListItemUiModel].
 * This transforms the raw data into a UI-friendly format for display.
 */
fun Note.toListItemUiModel(): NoteListItemUiModel {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return NoteListItemUiModel(
        id = this.id!!, // Assuming id is never null for a note in a list
        title = this.title.ifEmpty { "Untitled Note" },
        lastModified = this.timestamp.format(formatter)
    )
}
