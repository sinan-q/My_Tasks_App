package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.screens.formatDate

@Composable
fun TaskItem(
    task: Task,
    path: String?,
    onClick: () -> Unit,
    onUpdate: (Boolean) -> Unit,
) {
    RectangleCard(onClick = onClick) {
        Row (modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
            ) {

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onUpdate(it) }
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                path?.let { Text(text = path, style = MaterialTheme.typography.bodySmall) }

                if(task.title.isNotEmpty())
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                Spacer(modifier = Modifier.height(2.dp))
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                task.due?.let {
                    Text(
                        text = it.formatDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

    }
}


