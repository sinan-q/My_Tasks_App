package com.sinxn.mytasks.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Folder

@Composable
fun ShowOptionsFAB(
    onAddTaskClick: (Long?) -> Unit = {},
    onAddNoteClick: (Long?) -> Unit = {},
    onAddEventClick: (Long?) -> Unit = {},
    onAddFolderClick: () -> Unit = {},
    currentFolder: Folder? = null,
) {

    var showOptions by remember { mutableStateOf(false) }
    Column( verticalArrangement = Arrangement.spacedBy(10.dp),horizontalAlignment = Alignment.End) {
        AnimatedVisibility(
            visible = showOptions,
        ) {
            Column (verticalArrangement = Arrangement.spacedBy(10.dp),horizontalAlignment = Alignment.End ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        showOptions = false
                        onAddEventClick(currentFolder?.folderId)
                    }, icon = {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Add Event"
                        )
                    }, text = { Text(text = "Add Event") })
                ExtendedFloatingActionButton(
                    onClick = {
                        showOptions = false
                        onAddTaskClick(currentFolder?.folderId)
                    }, icon = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Add Task"
                        )
                    }, text = { Text(text = "Add Task") })
                ExtendedFloatingActionButton(
                    onClick = {
                        showOptions = false
                        onAddFolderClick()
                    }, icon = {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add Folder"
                        )
                    }, text = { Text(text = "Add Folder") })
                ExtendedFloatingActionButton(
                    onClick = {
                        showOptions = false
                        onAddNoteClick(currentFolder?.folderId)
                    }, icon = {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Add Note"
                        )
                    }, text = { Text(text = "Add Note") })

            }
        }
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = spring())
        ) {

            FloatingActionButton(onClick = { showOptions = !showOptions }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }


    }
}