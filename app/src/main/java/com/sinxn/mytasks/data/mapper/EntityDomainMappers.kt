package com.sinxn.mytasks.data.mapper

import com.sinxn.mytasks.data.local.entities.Alarm as AlarmEntity
import com.sinxn.mytasks.data.local.entities.Event as EventEntity
import com.sinxn.mytasks.data.local.entities.ExpiredTask as ExpiredTaskEntity
import com.sinxn.mytasks.data.local.entities.Folder as FolderEntity
import com.sinxn.mytasks.data.local.entities.Note as NoteEntity
import com.sinxn.mytasks.data.local.entities.Pinned as PinnedEntity
import com.sinxn.mytasks.data.local.entities.ItemType as ItemTypeEntity
import com.sinxn.mytasks.data.local.entities.Task as TaskEntity
import com.sinxn.mytasks.domain.models.Alarm
import com.sinxn.mytasks.domain.models.Event
import com.sinxn.mytasks.domain.models.ExpiredTask
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.models.ItemType
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.models.Pinned
import com.sinxn.mytasks.domain.models.Task

// Note
fun NoteEntity.toDomain(): Note = Note(
    id = id,
    folderId = folderId,
    title = title,
    content = content,
    timestamp = timestamp,
    isArchived = isArchived,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    folderId = folderId,
    title = title,
    content = content,
    timestamp = timestamp,
    isArchived = isArchived,
)

// Task
fun TaskEntity.toDomain(): Task = Task(
    id = id,
    folderId = folderId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    due = due,
    recurrenceRule = recurrenceRule,
    isArchived = isArchived,
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    folderId = folderId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    due = due,
    recurrenceRule = recurrenceRule,
    isArchived = isArchived,
)

// Event
fun EventEntity.toDomain(): Event = Event(
    id = id,
    folderId = folderId,
    title = title,
    description = description,
    timestamp = timestamp,
    start = start,
    end = end,
    recurrenceRule = recurrenceRule,
    isArchived = isArchived,
)

fun Event.toEntity(): EventEntity = EventEntity(
    id = id,
    folderId = folderId,
    title = title,
    description = description,
    timestamp = timestamp,
    start = start,
    end = end,
    recurrenceRule = recurrenceRule,
    isArchived = isArchived,
)

// Folder
fun FolderEntity.toDomain(): Folder = Folder(
    folderId = folderId,
    name = name,
    parentFolderId = parentFolderId,
    isLocked = isLocked,
    isArchived = isArchived,
)

fun Folder.toEntity(): FolderEntity = FolderEntity(
    folderId = folderId,
    name = name,
    parentFolderId = parentFolderId,
    isLocked = isLocked,
    isArchived = isArchived,
)

// Alarm
fun AlarmEntity.toDomain(): Alarm = Alarm(
    alarmId = alarmId,
    isTask = isTask,
    taskId = taskId,
    time = time,
)

fun Alarm.toEntity(): AlarmEntity = AlarmEntity(
    alarmId = alarmId,
    isTask = isTask,
    taskId = taskId,
    time = time,
)

// Pinned
fun PinnedEntity.toDomain(): Pinned = Pinned(
    id = id,
    itemId = itemId,
    itemType = itemType.toDomain(),
)

fun Pinned.toEntity(): PinnedEntity = PinnedEntity(
    id = id,
    itemId = itemId,
    itemType = itemType.toEntity(),
)

fun ItemTypeEntity.toDomain(): ItemType = when (this) {
    ItemTypeEntity.TASK -> ItemType.TASK
    ItemTypeEntity.NOTE -> ItemType.NOTE
    ItemTypeEntity.EVENT -> ItemType.EVENT
    ItemTypeEntity.FOLDER -> ItemType.FOLDER
}

fun ItemType.toEntity(): ItemTypeEntity = when (this) {
    ItemType.TASK -> ItemTypeEntity.TASK
    ItemType.NOTE -> ItemTypeEntity.NOTE
    ItemType.EVENT -> ItemTypeEntity.EVENT
    ItemType.FOLDER -> ItemTypeEntity.FOLDER
}

// ExpiredTask
fun ExpiredTaskEntity.toDomain(): ExpiredTask = ExpiredTask(
    taskId = taskId,
    expireAfterDueDate = expireAfterDueDate,
)

fun ExpiredTask.toEntity(): ExpiredTaskEntity = ExpiredTaskEntity(
    taskId = taskId,
    expireAfterDueDate = expireAfterDueDate,
)
