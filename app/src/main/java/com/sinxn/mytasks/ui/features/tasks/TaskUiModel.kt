package com.sinxn.mytasks.ui.features.tasks

import com.sinxn.mytasks.domain.models.Task
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * A UI-specific model representing a task to be displayed in a list.
 * This class is tailored for the UI and contains only the data needed for rendering the list item.
 */
data class TaskListItemUiModel(
    val id: Long,
    val title: String,
    val isCompleted: Boolean,
    val formattedDueDate: String?,
    val month: YearMonth,
    val date: LocalDate?
)

/**
 * Mapper function to convert a [Task] data entity to a [TaskListItemUiModel].
 * This is where raw data is transformed into a UI-friendly format for display in a list.
 */
fun Task.toListItemUiModel(): TaskListItemUiModel {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return TaskListItemUiModel(
        id = this.id?: 0L,
        title = this.title,
        isCompleted = this.isCompleted,
        formattedDueDate = this.due?.format(formatter),
        month = this.due?.let { YearMonth.from(it) } ?: YearMonth.now(),
        date = this.due?.toLocalDate()
    )
}
