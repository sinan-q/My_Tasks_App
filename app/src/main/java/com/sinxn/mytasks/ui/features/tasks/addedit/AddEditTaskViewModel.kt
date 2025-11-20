package com.sinxn.mytasks.ui.features.tasks.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.domain.models.Alarm
import com.sinxn.mytasks.domain.models.Task
import com.sinxn.mytasks.domain.models.RelationItemType
import com.sinxn.mytasks.domain.models.ItemRelation
import com.sinxn.mytasks.domain.usecase.alarm.AlarmUseCases
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.domain.usecase.event.EventUseCases
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.domain.usecase.relation.ItemRelationUseCases
import com.sinxn.mytasks.ui.components.ParentItemOption
import com.sinxn.mytasks.ui.features.tasks.list.TaskScreenUiState
import com.sinxn.mytasks.ui.features.tasks.list.TaskUiState
import com.sinxn.mytasks.utils.Constants
import com.sinxn.mytasks.utils.differenceSeconds
import com.sinxn.mytasks.utils.fromMillis
import com.sinxn.mytasks.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val alarmUseCases: AlarmUseCases,
    private val folderUseCases: FolderUseCases,
    private val itemRelationUseCases: ItemRelationUseCases,
    private val eventUseCases: EventUseCases,
    private val noteUseCases: NoteUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    val allTasks = taskUseCases.getTasks()
    val allEvents = eventUseCases.getEvents()
    val allNotes = noteUseCases.getNotes()

    fun onAction(action: AddEditTaskAction) {
        when (action) {
            is AddEditTaskAction.UpdateTask -> onTaskUpdate(action.task)
            is AddEditTaskAction.InsertTask -> insertTask(action.task, action.reminders)
            is AddEditTaskAction.DeleteTask -> deleteTask(action.task)
            is AddEditTaskAction.FetchTaskById -> fetchTaskById(action.taskId)
            is AddEditTaskAction.FetchFolderById -> fetchFolderById(action.folderId)
            is AddEditTaskAction.AddReminder -> addReminder(action.reminder)
            is AddEditTaskAction.RemoveReminder -> removeReminder(action.reminder)
            is AddEditTaskAction.SetParent -> setParent(action.parent)
            is AddEditTaskAction.RemoveParent -> removeParent()
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    private fun onTaskUpdate(task: TaskUiState) {
        _uiState.value = _uiState.value.copy(task = task)
    }

    private fun fetchTaskById(taskId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val fetchedTask = taskUseCases.getTask(taskId)
                if (fetchedTask == null) {
                    showToast("Task not found")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@launch
                }

                val alarms = mutableListOf<ReminderModel>().apply {
                    alarmUseCases.getAlarmsByTaskId(taskId).forEach { alarm ->
                        val alarmTime = fromMillis(alarm.time)
                        val due = fetchedTask.due

                        when (alarm.trigger) {
                            com.sinxn.mytasks.utils.ReminderTrigger.FROM_END -> {
                                if (due != null) {
                                    val diff = Duration.between(alarmTime, due)
                                    val pair = if (diff.toDaysPart() > 0) Pair(diff.toDaysPart().toInt(), ChronoUnit.DAYS)
                                    else if (diff.toHoursPart() > 0) Pair(diff.toHoursPart(), ChronoUnit.HOURS)
                                    else if (diff.toMinutesPart() > 0) Pair(diff.toMinutesPart(), ChronoUnit.MINUTES)
                                    else Pair(diff.toSecondsPart(), ChronoUnit.SECONDS)
                                    add(ReminderModel(pair.first, pair.second, com.sinxn.mytasks.utils.ReminderTrigger.FROM_END))
                                } else {
                                    // Fallback if due date is missing but trigger is FROM_END
                                    add(ReminderModel(0, ChronoUnit.MINUTES, com.sinxn.mytasks.utils.ReminderTrigger.CUSTOM, alarmTime))
                                }
                            }
                            com.sinxn.mytasks.utils.ReminderTrigger.FROM_START -> {
                                // For FROM_START, we calculate the duration from now to the alarm time.
                                // This effectively shows "Time remaining" or "Time passed".
                                val now = LocalDateTime.now()
                                val diff = Duration.between(now, alarmTime)
                                val pair = if (diff.toDaysPart() > 0) Pair(diff.toDaysPart().toInt(), ChronoUnit.DAYS)
                                else if (diff.toHoursPart() > 0) Pair(diff.toHoursPart(), ChronoUnit.HOURS)
                                else if (diff.toMinutesPart() > 0) Pair(diff.toMinutesPart(), ChronoUnit.MINUTES)
                                else Pair(diff.toSecondsPart(), ChronoUnit.SECONDS)
                                
                                // If the alarm is in the past, we might want to show it as CUSTOM or handle negative duration.
                                // For simplicity, if it's in the past, we show it as CUSTOM.
                                if (alarmTime.isBefore(now)) {
                                     add(ReminderModel(0, ChronoUnit.MINUTES, com.sinxn.mytasks.utils.ReminderTrigger.CUSTOM, alarmTime))
                                } else {
                                     add(ReminderModel(pair.first, pair.second, com.sinxn.mytasks.utils.ReminderTrigger.FROM_START))
                                }
                            }
                            com.sinxn.mytasks.utils.ReminderTrigger.CUSTOM -> {
                                add(ReminderModel(0, ChronoUnit.MINUTES, com.sinxn.mytasks.utils.ReminderTrigger.CUSTOM, alarmTime))
                            }
                        }
                    }
                }

                val fetchedFolder = folderUseCases.getFolder(fetchedTask.folderId)
                val subFolders = folderUseCases.getSubFolders(fetchedTask.folderId).first()

                // Fetch Parent
                val parentRelation = itemRelationUseCases.getParent(taskId, RelationItemType.TASK).first()
                val parentOption = parentRelation?.let { fetchParentDetails(it) }

                // Fetch Children
                val childrenRelations = itemRelationUseCases.getChildren(taskId, RelationItemType.TASK).first()
                val childrenOptions = fetchChildrenDetails(childrenRelations)

                _uiState.value = TaskScreenUiState(
                    task = fetchedTask.toUiState(),
                    reminders = alarms,
                    folder = fetchedFolder,
                    folders = subFolders,
                    parentItem = parentOption,
                    relatedItems = childrenOptions,
                    isLoading = false
                )

            } catch (e: Exception) {
                showToast(e.message ?: "An error occurred")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun insertTask(task: TaskUiState, reminders: List<ReminderModel> ) {
        if (task.title.isEmpty() && task.description.isEmpty()) {
            showToast(Constants.SAVE_FAILED_EMPTY)
            return
        }
        
        if (reminders.isNotEmpty() && reminders.map {
            validateReminder(task.due, it)
        }.contains(false) ) {
            showToast(Constants.NOTE_SAVE_FAILED_REMINDER_IN_PAST)
            return
        }
        
        viewModelScope.launch {
            var taskId = task.id
            if (taskId != null) {
                taskUseCases.updateTask(task.toDomain())
                alarmUseCases.cancelAlarmsByTaskId(taskId)
            } else taskId = taskUseCases.addTask(task.toDomain())
            
            reminders.forEach { reminder ->
                val time = calculateAlarmTime(task.due, reminder)?.toMillis()
                if (time != null) {
                    alarmUseCases.insertAlarm(
                        Alarm(
                            alarmId = 0,
                            taskId = taskId,
                            isTask = true, //TODO Event
                            time = time,
                            trigger = reminder.trigger
                        )
                    )
                }
            }

            // Handle Parent Relation
            val currentParent = _uiState.value.parentItem
            if (currentParent != null) {
                itemRelationUseCases.addRelation(
                    ItemRelation(
                        parentId = currentParent.id,
                        parentType = currentParent.type,
                        childId = taskId,
                        childType = RelationItemType.TASK
                    )
                )
            } else {
                itemRelationUseCases.removeRelationsForItem(taskId, RelationItemType.TASK)
            }

            showToast(Constants.SAVE_SUCCESS)
        }
    }

    private fun deleteTask(task: TaskUiState) = viewModelScope.launch {
        if (task.id == null) {
            showToast(Constants.DELETE_FAILED)
            return@launch
        }
        if(task.due != null) {
            alarmUseCases.cancelAlarmsByTaskId(task.id)
        }
        val deleted = taskUseCases.deleteTask(task.toDomain())
        if (deleted == 0) {
            showToast(Constants.DELETE_FAILED)
            return@launch
        }
        showToast(Constants.DELETE_SUCCESS)

    }

    private fun addReminder(reminder: ReminderModel) {
        if (!validateReminder(_uiState.value.task.due, reminder)) {
            showToast(Constants.NOTE_SAVE_FAILED_REMINDER_IN_PAST)
            return
        }
        if (_uiState.value.reminders.contains(reminder)) {
            showToast(Constants.TASK_REMINDER_ALREADY_EXISTS)
            return
        }
        _uiState.value = _uiState.value.copy(reminders = _uiState.value.reminders.plus(reminder))
    }

    private fun validateReminder(due: LocalDateTime?, reminder: ReminderModel): Boolean {
        val alarmTime = calculateAlarmTime(due, reminder) ?: return false
        return alarmTime.isAfter(LocalDateTime.now())
    }

    private fun calculateAlarmTime(due: LocalDateTime?, reminder: ReminderModel): LocalDateTime? {
        return when (reminder.trigger) {
            com.sinxn.mytasks.utils.ReminderTrigger.FROM_END -> {
                due?.minus(reminder.duration.toLong(), reminder.unit)
            }
            com.sinxn.mytasks.utils.ReminderTrigger.FROM_START -> {
                LocalDateTime.now().plus(reminder.duration.toLong(), reminder.unit)
            }
            com.sinxn.mytasks.utils.ReminderTrigger.CUSTOM -> {
                reminder.customDateTime
            }
        }
    }

    private fun removeReminder(reminder: ReminderModel) {
        _uiState.value = _uiState.value.copy(reminders = _uiState.value.reminders.minus(reminder))
    }

    private fun fetchFolderById(folderId: Long) {
        viewModelScope.launch {
            val fetchedFolder = folderUseCases.getFolder(folderId)
            val subFolders = folderUseCases.getSubFolders(folderId).first()
            _uiState.value = _uiState.value.copy(
                folder = fetchedFolder,
                folders = subFolders,
                task = _uiState.value.task.copy(folderId = folderId)
            )
        }
    }
    private fun setParent(parent: ParentItemOption) {
        _uiState.value = _uiState.value.copy(parentItem = parent)
    }

    private fun removeParent() {
        _uiState.value = _uiState.value.copy(parentItem = null)
    }

    private suspend fun fetchParentDetails(relation: ItemRelation): ParentItemOption? {
        return try {
            val title = when (relation.parentType) {
                RelationItemType.TASK -> taskUseCases.getTask(relation.parentId)?.title
                RelationItemType.EVENT -> eventUseCases.getEvent(relation.parentId)?.title
                RelationItemType.NOTE -> noteUseCases.getNote(relation.parentId)?.title
            }
            title?.let { ParentItemOption(relation.parentId, it, relation.parentType) }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchChildrenDetails(relations: List<ItemRelation>): List<ParentItemOption> {
        return relations.mapNotNull { relation ->
            try {
                val title = when (relation.childType) {
                    RelationItemType.TASK -> taskUseCases.getTask(relation.childId)?.title
                    RelationItemType.EVENT -> eventUseCases.getEvent(relation.childId)?.title
                    RelationItemType.NOTE -> noteUseCases.getNote(relation.childId)?.title
                }
                title?.let { ParentItemOption(relation.childId, it, relation.childType) }
            } catch (e: Exception) {
                null
            }
        }
    }
}

private fun Task.toUiState(): TaskUiState {
    return TaskUiState(
        id = id,
        folderId = folderId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        due = due,
        recurrenceRule = recurrenceRule
    )
}

private fun TaskUiState.toDomain(): Task {
    return Task(
        id = id,
        folderId = folderId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        due = due,
        recurrenceRule = recurrenceRule
    )
}
