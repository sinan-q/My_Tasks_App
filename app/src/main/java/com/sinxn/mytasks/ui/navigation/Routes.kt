package com.sinxn.mytasks.ui.navigation

sealed class Routes(val route: String, val name: String? = null) {
    object Home : Routes("home_screen", "Home") {
        const val deepLink = "mytasks://home"
    }
    object Note : Routes("note_list_screen", "Note") {
        val noteIdArg = "noteId"
        val folderIdArg = "folderId"
        fun get(noteId: Long?) = "note_screen/$noteId/0"
        object Add : Routes("note_screen/{noteId}/{folderId}", "Edit Note") {
            fun byFolder(folderId: Long?) = "note_screen/-1L/$folderId"
            val deepLink = "mytasks://add_note"
        }
    }
    object Task : Routes("task_list_screen", "Task") {
        val taskIdArg = "taskId"
        val folderIdArg = "folderId"
        object Add : Routes("task_screen/{taskId}/{folderId}", "Edit Task") {
            val deepLink = "mytasks://add_task"
            fun byFolder(folderId: Long?) = "task_screen/-1L/$folderId"

        }
        fun get(taskId: Long?) = "task_screen/$taskId/0"

    }
    object Event : Routes("event_list_screen", "Calender") {
        val eventIdArg = "eventId"
        val folderIdArg = "folderId"
        val dateArg = "date"
        fun get(eventId: Long?) = "event_screen/$eventId/0/-1L"
        object Add : Routes("event_screen/{eventId}/{folderId}/{date}", "Edit Event") {
            val deepLink = "mytasks://add_event"
            fun byDate(epochDay: Long) = "event_screen/-1L/0/$epochDay"
            fun byFolder(folderId: Long?) = "event_screen/-1L/$folderId/-1L"
        }
    }

    object Folder : Routes("folder_list_screen/{folderId}", "Folders") {
        val folerIdArg = "folderId"
        fun get(folderId: Long) = "folder_list_screen/$folderId"
    }

    object Backup : Routes("backup_screen", "Backup")



}