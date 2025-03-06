package com.sinxn.mytasks.ui.screens.folderScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.ui.components.RectangleCard

@Composable
fun FolderItem(modifier: Modifier = Modifier,folder: Folder, onClick: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    RectangleCard(modifier = modifier,onClick = onClick) {
        Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(0.9f).fillMaxWidth(0.9f).padding(2.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = folder.name,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column (horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth().weight(0.2f)) {
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
                    Button(
                        onClick = {
                            expanded = false
                            onDelete() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.padding(2.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(text = "Delete", color = Color.Red)
                    }
                }

            }
        }

    }

}

@Composable
fun FolderItemEdit(folder: Folder, onDismiss: () -> Unit, onSubmit: (Folder) -> Unit) {
    val text = remember { mutableStateOf(folder.name) }
    RectangleCard(onClick = {}){
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Add New Folder")
            TextField(
                value = text.value,
                onValueChange = { text.value = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Row {
                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Cancel")
                }
                Button(
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
    onClick: (folderDd: Long) -> Unit,
    isEditing: Boolean,
    folder: Folder?,
    folders: List<Folder>
) {
    var folderChangeExpanded by remember { mutableStateOf(false) }

    Text(folder?.name?:"Parent", modifier = Modifier.clickable(enabled = isEditing) { folderChangeExpanded = true })
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