package com.sinxn.mytasks.ui.screens.taskScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.components.RectangleCard
import com.sinxn.mytasks.utils.formatDate

@Composable
fun TaskItem(
    task: Task,
    path: String?,
    onClick: () -> Unit,
    onHold: () -> Unit,
    onUpdate: (Boolean) -> Unit,
    selected: Boolean
) {
    RectangleCard(modifier = Modifier
        .fillMaxHeight()
        .background(color = if (selected) MaterialTheme.colorScheme.tertiaryContainer else if (task.isCompleted) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Unspecified)
        .combinedClickable(onLongClick = onHold, onClick = onClick)
    ) {

        Row (verticalAlignment = Alignment.CenterVertically) {

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


