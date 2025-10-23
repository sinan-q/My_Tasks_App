package com.sinxn.mytasks.ui.features.events

import com.sinxn.mytasks.data.local.entities.Event

sealed class AddEditEventAction {
    data class UpdateEvent(val event: Event) : AddEditEventAction()
    data class InsertEvent(val event: Event) : AddEditEventAction()
    data class DeleteEvent(val event: Event) : AddEditEventAction()
    data class FetchEventById(val eventId: Long) : AddEditEventAction()
    data class FetchFolderById(val folderId: Long) : AddEditEventAction()
}
