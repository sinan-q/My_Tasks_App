package com.sinxn.mytasks.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sinxn.mytasks.R
import com.sinxn.mytasks.core.SelectionAction

@Composable
fun SelectionDeleteDialog(
    selectionAction: SelectionAction,
    selectionCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ConfirmationDialog(
        showDialog = selectionAction == SelectionAction.Delete,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = stringResource(R.string.delete_confirmation_title),
        message = "Sure want to delete $selectionCount items?", // Extract to string resource if needed later
    )
}
