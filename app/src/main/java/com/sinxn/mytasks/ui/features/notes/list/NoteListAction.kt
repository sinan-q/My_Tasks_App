package com.sinxn.mytasks.ui.features.notes.list

import com.sinxn.mytasks.core.SelectionAction

sealed class NoteListAction {
    data class OnSelectionAction(val action: SelectionAction) : NoteListAction()
}
