package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Folder

@Composable
fun FolderItem(folder: Folder, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.extraSmall),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = folder.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(16.dp)
            )
        }
    }

}

@Composable
fun FolderItemEdit(folder: Folder, onDismiss: () -> Unit, onSubmit: (Folder) -> Unit) {
    val text = remember { mutableStateOf(folder.name) }
    Card(modifier = Modifier.background(Color.White).padding(vertical = 4.dp)
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
            Button(
                onClick = { onSubmit(Folder(name = text.value, parentFolderId = folder.parentFolderId)); onDismiss() },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Submit")
            }
        }
    }
}