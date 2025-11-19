package com.sinxn.mytasks.ui.features.folders

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinxn.mytasks.R
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.ui.components.ConfirmationDialog
import com.sinxn.mytasks.ui.components.RectangleButton
import showBiometricsAuthentication

@Composable
fun FolderItem(
    modifier: Modifier = Modifier,
    folder: FolderListItemUiModel,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onLock: () -> Unit,
    onHold: () -> Unit,
    selected: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.tertiaryContainer,
        label = "FileItemBackgroundAnimation"
    )
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for dialog

    fun authenticate(function: () -> Unit) {
        showBiometricsAuthentication(
            context,
            onSuccess = function,
            onError = { errString ->
                // Authentication error
                Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxHeight()
            .combinedClickable(onLongClick = onHold, onClick = {
                if (folder.isLocked) authenticate(onClick)
                else onClick()
            }),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RectangleShape
        ) {
        Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            if (folder.isLocked)
                Icon(
                    modifier= Modifier.padding(horizontal = 6.dp),
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxWidth(0.9f)
                    .padding(2.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = folder.name,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column (horizontalAlignment = Alignment.End, modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)) {
                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More Options",
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    RectangleButton(
                        onClick = {
                            expanded = false
                            authenticate(onLock)
                             },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow, contentColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(text = if (folder.isLocked) "Unlock" else "Lock")
                    }
                    RectangleButton(
                        onClick = {
                            expanded = false
                            showDeleteConfirmationDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(text = stringResource(R.string.delete), color = Color.Red)
                    }
                }

            }
        }

    }
    ConfirmationDialog(
        showDialog = showDeleteConfirmationDialog,
        onDismiss = { showDeleteConfirmationDialog = false },
        onConfirm = {
            onDelete()
            showDeleteConfirmationDialog = false
        },
        title = stringResource(R.string.delete_confirmation_title),
        message = stringResource(R.string.delete_folder_message)
    )

}

@Composable
fun FolderItemEdit(folder: Folder, onDismiss: () -> Unit, onSubmit: (Folder) -> Unit) {
    val text = remember { mutableStateOf(folder.name) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Add New Folder")
            TextField(
                value = text.value,
                onValueChange = { text.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Row {
                RectangleButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cancel")
                }
                RectangleButton (
                    onClick = { onSubmit(Folder(name = text.value, parentFolderId = folder.parentFolderId)); onDismiss() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Submit")
                }
            }

        }
    }

}

@Composable
fun FolderDropDown(
    modifier: Modifier = Modifier,
    onClick: (folderDd: Long) -> Unit,
    isEditing: Boolean,
    folder: Folder?,
    folders: List<Folder>,
) {
    var folderChangeExpanded by remember { mutableStateOf(false) }

    Row( modifier = modifier.clickable(enabled = isEditing) { folderChangeExpanded = true }) {
        Icon(painterResource(R.drawable.folder_ic), contentDescription = "Folder Icon", tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        Text(folder?.name?:"Parent", fontSize = 12.sp)
    }
    DropdownMenu(
        expanded = folderChangeExpanded,
        onDismissRequest = { folderChangeExpanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("...") },
            onClick = { onClick(folder?.parentFolderId?:0) }
        )
        folders.forEach { folder ->
            DropdownMenuItem(
                text = { Text(folder.name) },
                onClick = { onClick(folder.folderId)}
            )
        }
    }


}