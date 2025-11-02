package com.sinxn.mytasks.ui.features.events

import com.sinxn.mytasks.data.local.entities.Event
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * A UI-specific model representing an event to be displayed in a list.
 * This class is tailored for the UI and contains only the data needed for rendering the list item.
 */
data class EventListItemUiModel(
    val id: Long,
    val title: String,
    val startDay: String,
    val startMonth: String,
    val formattedDuration: String
)

/**
 * Mapper function to convert an [Event] data entity to an [EventListItemUiModel].
 * This transforms the raw data into a UI-friendly format for display.
 */
fun Event.toListItemUiModel(): EventListItemUiModel {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    return EventListItemUiModel(
        id = this.id ?: 0, // Assuming id is never null for an event in a list
        title = this.title,
        startDay = this.start?.dayOfMonth?.toString()?:"Error",
        startMonth = this.start?.month?.getDisplayName(TextStyle.SHORT, Locale.getDefault())?:"Error",
        formattedDuration = this.start?.let { start ->
            val end = this.end
            val startTime = start.format(timeFormatter)
            if (end == null) {
                return@let startTime
            }
            val endTime = end.format(timeFormatter)
            if (end.toLocalDate() == start.toLocalDate()) {
                "$startTime - $endTime"
            } else {
                if (end.toLocalDate().year == start.toLocalDate().year)
                "$startTime - ${end.format(dateFormatter)} $endTime"
                else "$startTime - ${end.format(dateFormatter)} ${end.year}"
            }
        } ?: ""
    )
}

