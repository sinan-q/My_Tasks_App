package com.sinxn.mytasks.ui.features.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.ui.features.tasks.TaskListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.toListItemUiModel
import com.sinxn.mytasks.utils.Constants
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.dmfs.rfc5545.recur.RecurrenceRule
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.TimeZone
import javax.inject.Inject

data class EventScreenUiModel(
    val eventListItems: List<EventListItemUiModel> = emptyList(),
    val month: YearMonth = YearMonth.now(),
    val eventsOnMonth: List<EventListItemUiModel> = emptyList(),
    val taskListItems: List<TaskListItemUiModel> = emptyList(),
    val taskOnMonth: List<TaskListItemUiModel> = emptyList(),

    val folders: List<Folder> = emptyList(),
    val folder: Folder? = null,
    val event: Event = Event(),
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
) : ViewModel() {


    private val _uiState = MutableStateFlow(EventScreenUiModel())
    val uiState: StateFlow<EventScreenUiModel> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun onAction(action: AddEditEventAction) {
        when (action) {
            is AddEditEventAction.UpdateEvent -> onUpdateEvent(action.event)
            is AddEditEventAction.InsertEvent -> insertEvent(action.event)
            is AddEditEventAction.DeleteEvent -> deleteEvent(action.event)
            is AddEditEventAction.FetchEventById -> fetchEventById(action.eventId)
            is AddEditEventAction.FetchFolderById -> fetchFolderById(action.folderId)
            is AddEditEventAction.OnMonthChange -> onMonthChange(action.month)
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
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

    private fun onUpdateEvent(event: Event) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            event = event,
        )

    }

    init {
        viewModelScope.launch {
            combine(
                repository.getAllEvents(),
                taskRepository.getAllTasks(),
            ) { events, tasks ->
                val generatedEvents = generateRecurringInstances(events)
                val generatedTasks = generateRecurringInstancesTask(tasks)
                EventScreenUiModel(
                    eventListItems = generatedEvents.map { it.toListItemUiModel() },
                    taskListItems = generatedTasks.map { it.toListItemUiModel() },
                    folders = emptyList(),
                    folder =  null,
                    event = Event(),
                    eventsOnMonth = generatedEvents.filter { it.start?.dayOfMonth == LocalDateTime.now().dayOfMonth }.map { it.toListItemUiModel() },
                    taskOnMonth = generatedTasks.filter { it.due?.dayOfMonth == LocalDateTime.now().dayOfMonth }.map { it.toListItemUiModel() } ,
                    month = YearMonth.now()
                )
            }
            .collect { eventScreenUiModel ->
                _uiState.value = eventScreenUiModel
            }
        }
    }


    private fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            val currentState = _uiState.value
            _uiState.value = currentState.copy(
                folder = fetchedFolder,
                folders = subFolders,
                event = currentState.event.copy(folderId = folderId)
            )

        }

    }

    private fun fetchEventById(eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedEvent = repository.getEventById(eventId)
            if (fetchedEvent == null) {
                showToast(Constants.NOT_FOUND)
                return@launch
            }
            val currentState = _uiState.value
            _uiState.value = currentState.copy(event = fetchedEvent)

            fetchFolderById(fetchedEvent.folderId)
        }
    }

    private fun insertEvent(event: Event) = viewModelScope.launch {
        if (event.title.isEmpty() && event.description.isEmpty()) {
            showToast(Constants.SAVE_FAILED_EMPTY)
            return@launch
        } else if (event.title.isEmpty()) {
            showToast(Constants.SAVE_FAILED_EMPTY)
            return@launch
        }
        if (event.start == null || event.end == null) {
            showToast(Constants.EVENT_SAVE_FAILED_DATE_EMPTY)
            return@launch
        }
        if (event.start.isAfter(event.end)) {
            showToast(Constants.EVENT_SAVE_FAILED_END_AFTER_START)
            return@launch
        }
        if (event.id != null) {
            repository.updateEvent(event)
        } else {
            repository.insertEvent(event)
        }
        showToast(Constants.SAVE_SUCCESS)
    }

    private fun deleteEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        val deleted = repository.deleteEvent(event)
        if (deleted == 0) {
            showToast(Constants.DELETE_FAILED)
            return@launch
        }
        showToast(Constants.DELETE_SUCCESS)
    }

    private fun updateEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateEvent(event)
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