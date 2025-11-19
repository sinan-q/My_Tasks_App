package com.sinxn.mytasks.ui.features.events

import com.sinxn.mytasks.domain.models.Event
import java.time.YearMonth

sealed class AddEditEventAction {
    data class UpdateEvent(val event: Event) : AddEditEventAction()
    data class InsertEvent(val event: Event) : AddEditEventAction()
    data class DeleteEvent(val event: Event) : AddEditEventAction()
    data class FetchEventById(val eventId: Long) : AddEditEventAction()
    data class FetchFolderById(val folderId: Long) : AddEditEventAction()

    data class OnMonthChange(val month: YearMonth) : AddEditEventAction()

}
