package com.sinxn.mytasks.ui.features.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.ui.components.RectangleCard
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun EventSmallItem(event: EventListItemUiModel, modifier: Modifier = Modifier, onClick: () -> Unit) {
    RectangleCard(onClick = onClick, modifier = modifier.fillMaxWidth())  {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = event.formattedStartDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


        }

    }
}

fun formatTime(localDateTime: LocalDateTime?): String {
    if (localDateTime == null) return "No Date found"
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    return localDateTime.format(timeFormatter)
}
