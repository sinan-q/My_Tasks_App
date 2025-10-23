package com.sinxn.mytasks.ui.features.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.utils.formatDate

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    path: String?,
    onClick: () -> Unit,
    onHold: () -> Unit,
    onUpdate: (Boolean) -> Unit,
    selected: Boolean,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.tertiary else if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
        label = "TaskItemBackgroundAnimation" // Optional label for debugging
    )
    Card(modifier = modifier
        .fillMaxWidth()
        .combinedClickable(onLongClick = onHold, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RectangleShape
    ) {

        Row (verticalAlignment = Alignment.CenterVertically) {

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onUpdate(it) }
            )
            Column(modifier = Modifier.padding(8.dp)) {
                path?.let { Text(text = path, style = MaterialTheme.typography.labelSmall, color = LocalContentColor.current.copy(alpha = 0.6f)) }

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
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalContentColor.current.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                task.due?.let {
                    Text(
                        text = it.formatDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                }
            }
        }

    }
}


