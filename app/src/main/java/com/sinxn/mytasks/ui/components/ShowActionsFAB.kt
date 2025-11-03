package com.sinxn.mytasks.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.R
import com.sinxn.mytasks.core.SelectionAction


@Composable
fun ShowActionsFAB(
    folderId: Long = 0L,
    onAction: (SelectionAction) -> Unit,
    action: SelectionAction,
    pasteDisabled: Boolean = false,
) {
    var isOptionsVisible by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp), // Increased spacing for better readability
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = isOptionsVisible,
        ) {
            OptionsColumn2(
                folderId = folderId,
                onCloseOptions = { isOptionsVisible = false },
                onAction = onAction,
                action = action,
                pasteDisabled = pasteDisabled
            )
        }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = spring())
        ) {
            MainFloatingActionButton2(
                isOptionsVisible = isOptionsVisible,
                onToggleOptions = { isOptionsVisible = !isOptionsVisible }
            )
        }
    }
}

@Composable
fun OptionsColumn2(
    pasteDisabled: Boolean,
    folderId: Long,
    onCloseOptions: () -> Unit,
    onAction: (SelectionAction) -> Unit,
    action: SelectionAction
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        OptionButton(
            onClick = { onCloseOptions();  onAction(SelectionAction.None)},
            icon = R.drawable.ic_cancel,
            contentDescription = "Clear the selection",
            text = "Clear"
        )
        if (action == SelectionAction.None) {
            OptionButton(
                onClick = { onCloseOptions(); onAction(SelectionAction.Pin)  },
                icon = R.drawable.ic_delete,
                contentDescription = "Add to pin",
                text = "Pin"
            )
            OptionButton(
                onClick = { onCloseOptions(); onAction(SelectionAction.Delete) },
                icon = R.drawable.ic_delete,
                contentDescription = "Delete",
                text = "Delete"
            )
            OptionButton(
                onClick = { onCloseOptions(); onAction(SelectionAction.Copy) },
                icon = R.drawable.ic_copy,
                contentDescription = "Copy",
                text = "Copy"
            )
            OptionButton(
                onClick = { onCloseOptions(); onAction(SelectionAction.Cut) },
                icon = R.drawable.ic_move,
                contentDescription = "Move",
                text = "Move"
            )
//            OptionButton( //TODO add lock to notes, tasks db
//                onClick = { onCloseOptions(); setActions(SelectionActions.LOCK) },
//                icon = R.drawable.ic_lock,
//                contentDescription = "Lock",
//                text = "Lock"
//            )
        } else if (!pasteDisabled && action == SelectionAction.Copy || action == SelectionAction.Cut) {
            OptionButton(
                onClick = { onCloseOptions(); onAction(SelectionAction.Paste(folderId)) },
                icon = R.drawable.ic_paste,
                contentDescription = "Paste",
                text = "Paste"
            )
        }
    }
}

@Composable
fun MainFloatingActionButton2(
    isOptionsVisible: Boolean,
    onToggleOptions: () -> Unit
) {
    RectangleFAB(
        onClick = onToggleOptions,
        shape = RectangleShape,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Icon(
            Icons.Default.Settings,
            contentDescription = if (isOptionsVisible) "Close Selection Actions" else "Open Selection Actions"
        )
    }
}