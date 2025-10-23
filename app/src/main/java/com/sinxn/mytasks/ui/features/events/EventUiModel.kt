package com.sinxn.mytasks.ui.features.events

import com.sinxn.mytasks.data.local.entities.Event
import java.time.format.DateTimeFormatter

/**
 * A UI-specific model representing an event to be displayed in a list.
 * This class is tailored for the UI and contains only the data needed for rendering the list item.
 */
data class EventListItemUiModel(
    val id: Long,
    val title: String,
    val formattedStartDate: String,
)

/**
 * Mapper function to convert an [Event] data entity to an [EventListItemUiModel].
 * This transforms the raw data into a UI-friendly format for display.
 */
fun Event.toListItemUiModel(): EventListItemUiModel {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return EventListItemUiModel(
        id = this.id!!, // Assuming id is never null for an event in a list
        title = this.title,
        formattedStartDate = this.start?.format(formatter) ?: ""
    )
}
