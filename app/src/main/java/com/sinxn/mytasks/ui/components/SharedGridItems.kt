package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.ui.Modifier
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.ui.features.events.EventListItemUiModel
import com.sinxn.mytasks.ui.features.events.EventSmallItem
import com.sinxn.mytasks.ui.features.folders.FolderItem
import com.sinxn.mytasks.ui.features.folders.FolderListItemUiModel
import com.sinxn.mytasks.ui.features.notes.list.NoteItem
import com.sinxn.mytasks.ui.features.notes.list.NoteListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.TaskItem
import com.sinxn.mytasks.ui.features.tasks.list.TaskListItemUiModel

fun LazyStaggeredGridScope.folderGridItems(
    folders: List<FolderListItemUiModel>,
    onFolderClick: (Long) -> Unit,
    onFolderDelete: (Folder) -> Unit,
    onFolderLock: (Folder) -> Unit,
    onFolderHold: (Long) -> Unit,
    keyPrefix: String = "folder"
) {
    items(folders, key = { "${keyPrefix}_${it.id}" }) { folder ->
        FolderItem(
            folder = folder,
            onClick = { onFolderClick(folder.id) },
            onDelete = { onFolderDelete(Folder(folderId = folder.id, name = folder.name, isLocked = folder.isLocked)) },
            onLock = { onFolderLock(Folder(folderId = folder.id, name = folder.name, isLocked = folder.isLocked)) },
            onHold = { onFolderHold(folder.id) },
            selected = folder.isSelected,
            modifier = Modifier.animateItem()
        )
    }
}

fun LazyStaggeredGridScope.taskGridItems(
    tasks: List<TaskListItemUiModel>,
    onTaskClick: (Long) -> Unit,
    onTaskUpdate: (Long, Boolean) -> Unit,
    onTaskHold: (Long) -> Unit,
    pathProvider: (Long) -> String? = { null },
    keyPrefix: String = "task"
) {
    items(
        items = tasks,
        key = { task -> "${keyPrefix}_${task.id}" },
        span = { androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine }
    ) { task ->
        TaskItem(
            task = task,
            onClick = { onTaskClick(task.id) },
            onUpdate = { status -> onTaskUpdate(task.id, status) },
            onHold = { onTaskHold(task.id) },
            path = pathProvider(task.id),
            selected = task.isSelected,
            modifier = Modifier.animateItem()
        )
    }
}

fun LazyListScope.taskListItems(
    tasks: List<TaskListItemUiModel>,
    onTaskClick: (Long) -> Unit,
    onTaskUpdate: (Long, Boolean) -> Unit,
    onTaskHold: (Long) -> Unit,
    pathProvider: (Long) -> String? = { null },
    keyPrefix: String = "task"
) {
    items(
        items = tasks,
        key = { task -> "${keyPrefix}_${task.id}" }
    ) { task ->
        TaskItem(
            task = task,
            onClick = { onTaskClick(task.id) },
            onUpdate = { status -> onTaskUpdate(task.id, status) },
            onHold = { onTaskHold(task.id) },
            path = pathProvider(task.id),
            selected = task.isSelected,
            modifier = Modifier.animateItem()
        )
    }
}

fun LazyStaggeredGridScope.noteGridItems(
    notes: List<NoteListItemUiModel>,
    onNoteClick: (Long) -> Unit,
    onNoteHold: (Long) -> Unit,
    pathProvider: (Long) -> String? = { null },
    keyPrefix: String = "note"
) {
    items(notes, key = { "${keyPrefix}_${it.id}" }) { note ->
        NoteItem(
            note = note,
            onClick = { onNoteClick(note.id) },
            onHold = { onNoteHold(note.id) },
            path = pathProvider(note.id),
            selected = note.isSelected,
            modifier = Modifier.animateItem()
        )
    }
}

fun LazyStaggeredGridScope.eventGridItems(
    events: List<EventListItemUiModel>,
    onEventClick: (Long) -> Unit,
    keyPrefix: String = "event"
) {
    items(events, key = { "${keyPrefix}_${it.id}" }) { event ->
        EventSmallItem(
            event = event,
            modifier = Modifier.animateItem(),
            onClick = { onEventClick(event.id) }
        )
    }
}
