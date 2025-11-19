package com.sinxn.mytasks.ui.features.events.list

import com.sinxn.mytasks.core.SelectionAction
import java.time.YearMonth

sealed class EventListAction {
    data class OnMonthChange(val month: YearMonth) : EventListAction()
    data class OnSelectionAction(val action: SelectionAction) : EventListAction()
}
