package com.sinxn.mytasks.ui.features.events.addedit

import com.sinxn.mytasks.domain.models.Event


sealed class AddEditEventAction {
    data class UpdateEvent(val event: Event) : AddEditEventAction()
    data class InsertEvent(val event: Event) : AddEditEventAction()
    data class DeleteEvent(val event: Event) : AddEditEventAction()
    data class FetchEventById(val eventId: Long) : AddEditEventAction()
    data class FetchFolderById(val folderId: Long) : AddEditEventAction()



}
