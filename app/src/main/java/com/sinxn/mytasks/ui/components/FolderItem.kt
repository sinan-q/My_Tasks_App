package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Folder

@Composable
fun FolderItem(folder: Folder, onClick: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.padding(vertical = 4.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.extraSmall),
    ) {
        Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {

                Text(
                    text = folder.name,
                    modifier = Modifier
                        .clickable(onClick = onClick)
                        .padding(16.dp)
                )
            }
            Column (horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Delete",
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
    Card(modifier = Modifier.padding(vertical = 4.dp)
    ) {
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