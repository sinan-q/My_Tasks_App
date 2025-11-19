package com.sinxn.mytasks.ui.features.events.list

import com.sinxn.mytasks.domain.models.Event
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.ui.features.events.EventListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.TaskListItemUiModel
import java.time.YearMonth

data class EventsUiState(
    val eventListItems: List<EventListItemUiModel> = emptyList(),
    val month: YearMonth = YearMonth.now(),
    val eventsOnMonth: List<EventListItemUiModel> = emptyList(),
    val taskListItems: List<TaskListItemUiModel> = emptyList(),
    val taskOnMonth: List<TaskListItemUiModel> = emptyList(),

    val folders: List<Folder> = emptyList(),
    val folder: Folder? = null,
    val event: Event = Event(),
)
