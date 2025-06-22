package com.sinxn.mytasks.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.R
import com.sinxn.mytasks.data.local.entities.Folder


@Composable
fun ShowOptionsFAB(
    onAddTaskClick: (Long?) -> Unit = {},
    onAddNoteClick: (Long?) -> Unit = {},
    onAddEventClick: (Long?) -> Unit = {},
    onAddFolderClick: () -> Unit = {},
    currentFolder: Folder,
) {
    var isOptionsVisible by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp), // Increased spacing for better readability
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = isOptionsVisible,
        ) {
            OptionsColumn(
                onAddTaskClick = { onAddTaskClick(currentFolder.folderId) },
                onAddNoteClick = { onAddNoteClick(currentFolder.folderId) },
                onAddEventClick = { onAddEventClick(currentFolder.folderId) },
                onAddFolderClick = onAddFolderClick,
                onCloseOptions = { isOptionsVisible = false }
            )
        }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = spring())
        ) {
            MainFloatingActionButton(
                isOptionsVisible = isOptionsVisible,
                onToggleOptions = { isOptionsVisible = !isOptionsVisible }
            )
        }
    }
}

@Composable
fun OptionsColumn(
    onAddTaskClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    onAddEventClick: () -> Unit,
    onAddFolderClick: () -> Unit,
    onCloseOptions: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        OptionButton(
            onClick = { onCloseOptions(); onAddEventClick() },
            icon = R.drawable.event_ic_add,
            contentDescription = "Add Event",
            text = "Add Event"
        )
        OptionButton(
            onClick = { onCloseOptions(); onAddTaskClick() },
            icon = R.drawable.task_ic_add,
            contentDescription = "Add Task",
            text = "Add Task"
        )
        OptionButton(
            onClick = { onCloseOptions(); onAddFolderClick() },
            icon = R.drawable.folder_ic_add,
            contentDescription = "Add Folder",
            text = "Add Folder"
        )
        OptionButton(
            onClick = { onCloseOptions(); onAddNoteClick() },
            icon = R.drawable.note_ic_add,
            contentDescription = "Add Note",
            text = "Add Note"
        )
    }
}

@Composable
fun OptionButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    contentDescription: String,
    text: String
) {
    ExtendedRectangleFAB(
        onClick = onClick,
        icon = { Icon(painterResource(icon), contentDescription = contentDescription) },
        text = { Text(text = text) }
    )
}

@Composable
fun MainFloatingActionButton(
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
            Icons.Default.Add,
            contentDescription = if (isOptionsVisible) "Close Options" else "Open Options"
        )
    }
}