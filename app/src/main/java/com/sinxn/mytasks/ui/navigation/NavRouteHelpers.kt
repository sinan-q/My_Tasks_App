package com.sinxn.mytasks.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object NavRouteHelpers {
    private fun enc(v: Any?) = URLEncoder.encode((v ?: "").toString(), StandardCharsets.UTF_8)

    data class NoteArgs(val noteId: Long = -1L, val folderId: Long = 0L)
    data class TaskArgs(val taskId: Long = -1L, val folderId: Long = 0L)
    data class EventArgs(val eventId: Long = -1L, val folderId: Long = 0L, val date: Long = -1L)
    data class FolderArgs(val folderId: Long = 0L)

    fun routeFor(args: NoteArgs): String = "note_screen/${args.noteId}/${args.folderId}"
    fun routeFor(args: TaskArgs): String = "task_screen/${args.taskId}/${args.folderId}"
    fun routeFor(args: EventArgs): String = "event_screen/${args.eventId}/${args.folderId}/${args.date}"
    fun routeFor(args: FolderArgs): String = "folder_list_screen/${args.folderId}"

    // Deep links
    const val DL_HOME = "mytasks://home"
    const val DL_ADD_NOTE = "mytasks://add_note"
    const val DL_ADD_TASK = "mytasks://add_task"
    const val DL_ADD_EVENT = "mytasks://add_event"
}
