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
import com.sinxn.mytasks.core.SelectionActions


@Composable
fun ShowActionsFAB(
    onPaste: () -> Unit,
    onClearSelection: () -> Unit,
    onPinSelection: () -> Unit,
    action: SelectionActions,
    setActions: (SelectionActions) -> Unit
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
                onCloseOptions = { isOptionsVisible = false },
                onPaste = onPaste,
                onClearSelection = onClearSelection,
                action = action,
                setActions = setActions,
                onPinSelection = onPinSelection
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
    onPaste: () -> Unit,
    setActions: (SelectionActions) -> Unit,
    onPinSelection: () -> Unit,
    onCloseOptions: () -> Unit,
    onClearSelection: () -> Unit,
    action: SelectionActions
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        OptionButton(
            onClick = { onCloseOptions(); onClearSelection() },
            icon = R.drawable.ic_cancel,
            contentDescription = "Clear the selection",
            text = "Clear"
        )
        if (action == SelectionActions.NONE) {
            OptionButton(
                onClick = { onCloseOptions(); onPinSelection() },
                icon = R.drawable.ic_delete,
                contentDescription = "Add to pin",
                text = "Pin"
            )
            OptionButton(
                onClick = { onCloseOptions(); setActions(SelectionActions.DELETE) },
                icon = R.drawable.ic_delete,
                contentDescription = "Delete",
                text = "Delete"
            )
            OptionButton(
                onClick = { onCloseOptions(); setActions(SelectionActions.COPY) },
                icon = R.drawable.ic_copy,
                contentDescription = "Copy",
                text = "Copy"
            )
            OptionButton(
                onClick = { onCloseOptions(); setActions(SelectionActions.CUT) },
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
        } else if (action == SelectionActions.COPY || action == SelectionActions.CUT) {
            OptionButton(
                onClick = { onCloseOptions(); onPaste() },
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