package com.sinxn.mytasks.ui.features.events.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.core.SelectionAction
import com.sinxn.mytasks.core.SelectionActionHandler
import com.sinxn.mytasks.domain.models.Event
import com.sinxn.mytasks.domain.models.Task
import com.sinxn.mytasks.domain.usecase.event.EventUseCases
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.ui.features.events.toListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.toListItemUiModel
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.dmfs.rfc5545.recur.RecurrenceRule
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
    private val taskUseCases: TaskUseCases,
    private val selectionActionHandler: SelectionActionHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                eventUseCases.getEvents(),
                taskUseCases.getTasks(),
            ) { events, tasks ->
                val generatedEvents = generateRecurringInstances(events)
                val generatedTasks = generateRecurringInstancesTask(tasks)
                EventsUiState(
                    eventListItems = generatedEvents.map { it.toListItemUiModel() },
                    taskListItems = generatedTasks.map { it.toListItemUiModel() },
                    folders = emptyList(),
                    folder = null,
                    event = Event(),
                )
            }
            .collect { eventScreenUiModel ->
                _uiState.value = eventScreenUiModel
                onMonthChange(YearMonth.now())
            }
        }
    }

    fun onAction(action: EventListAction) {
        when (action) {
            is EventListAction.OnMonthChange -> onMonthChange(action.month)
            is EventListAction.OnSelectionAction -> onSelectionAction(action.action)
        }
    }

    private fun onMonthChange(month: YearMonth) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            month = month,
            eventsOnMonth = currentState.eventListItems.filter {
                it.month == month
            },
            taskOnMonth = currentState.taskListItems.filter {
                it.month == month
            }
        )
    }

    private fun onSelectionAction(action: SelectionAction) = viewModelScope.launch {
        selectionActionHandler.onAction(action)
    }

    private fun generateRecurringInstances(events: List<Event>): List<Event> {
        val instances = mutableListOf<Event>()
        val now = LocalDateTime.now()
        val windowStart = now.minusMonths(2)
        val windowEnd = now.plusMonths(2)

        for (event in events) {
            if (event.recurrenceRule != null && event.start != null && event.end != null) {
                val rule = RecurrenceRule(event.recurrenceRule)
                val startMillis = event.start.toMillis()
                val iterator = rule.iterator(startMillis, TimeZone.getDefault())

                while (iterator.hasNext()) {
                    val nextMillis = iterator.nextMillis()
                    if (nextMillis > windowEnd.toMillis()) {
                        break
                    }
                    if (nextMillis >= windowStart.toMillis()) {
                        val duration = Duration.between(event.start, event.end).toMillis()
                        val instanceStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(nextMillis), ZoneId.systemDefault())
                        val instanceEnd = instanceStart.plusNanos(duration * 1_000_000)
                        instances.add(event.copy(id = null, start = instanceStart, end = instanceEnd, recurrenceRule = null))
                    }
                }
            } else {
                instances.add(event)
            }
        }
        return instances
    }

    private fun generateRecurringInstancesTask(tasks: List<Task>): List<Task> {
        val instances = mutableListOf<Task>()
        val now = LocalDateTime.now()
        val windowStart = now.minusMonths(2)
        val windowEnd = now.plusMonths(2)

        for (task in tasks) {
            if (task.recurrenceRule != null && task.due != null) {
                val rule = RecurrenceRule(task.recurrenceRule)
                val startMillis = task.due.toMillis()
                val iterator = rule.iterator(startMillis, TimeZone.getDefault())

                while (iterator.hasNext()) {
                    val nextMillis = iterator.nextMillis()
                    if (nextMillis > windowEnd.toMillis()) {
                        break
                    }
                    if (nextMillis >= windowStart.toMillis()) {
                        val instanceStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(nextMillis), ZoneId.systemDefault())
                        instances.add(task.copy(id = null, due = instanceStart, recurrenceRule = null))
                    }
                }
            } else {
                instances.add(task)
            }
        }
        return instances
    }
}
