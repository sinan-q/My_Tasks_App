package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.utils.toMillis
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.ceil

@Composable
fun CalendarGrid(
    events: List<Event>,
    tasks: List<Task>,
    onClick: (Long) -> Unit,
    onMonthChange: (LocalDate) -> Unit,
) {
    var localDate by remember { mutableStateOf(LocalDate.now())  }
    val WEEK_DAYS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val firstDayOfMonth = localDate.withDayOfMonth(1)
    val firstDayOfWeek = firstDayOfMonth.minusDays(firstDayOfMonth.dayOfWeek.value.toLong())
    val lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1)
    val lastDayOfWeek = lastDayOfMonth.plusDays(7 - lastDayOfMonth.dayOfWeek.value.toLong())
    val days = firstDayOfWeek.datesUntil(lastDayOfWeek).toList()
    val totalColumns = ceil(days.size / 7f).toInt()

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        localDate = localDate.minusMonths(1)
                        onMonthChange(localDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month"
                    )
                }
                Text(text = localDate.month.toString())
                IconButton(
                    onClick = {
                        localDate = localDate.plusMonths(1)
                        onMonthChange(localDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month"
                    )
                }
            }

        }
        Row {
           for (day in WEEK_DAYS) {
               CalendarDayWeekItem(modifier = Modifier.weight(1f), day = day)
           }
        }
        for (x in 0 until totalColumns) {
            Row(Modifier.fillMaxWidth()) {
                for (y in x*7 until x*7+7) {
                    val targetDay = days[y]
                    val tasksForDay = tasks
                        .filter { it.due?.toLocalDate() == targetDay }
                        .map { it.title }
                    val eventsForDay = events
                        .filter { it.start?.toLocalDate() == targetDay }
                        .map { it.title }
                    CalendarDayItem(modifier = Modifier.weight(1f), day = targetDay, events = eventsForDay.plus(tasksForDay), onClick = {
                        onClick(it)
                    })
                }

            }
        }

    }
}

@Composable
fun CalendarDayItem(modifier: Modifier, day: LocalDate, events: List<String>, onClick: (Long) -> Unit = {}) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
            .clickable { onClick(LocalDateTime.of(day, LocalTime.now()).toMillis()) },
        colors = CardDefaults.cardColors(
            containerColor = if (day == LocalDate.now()) Color.Unspecified else Color.Transparent
        )
    ) {
        Column(modifier = Modifier.padding(4.dp).fillMaxSize(), verticalArrangement = if (events.isEmpty()) Arrangement.Center else Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = day.dayOfMonth.toString(), style = if(events.isEmpty()) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodySmall)
            events.forEach {
                Text(
                    text = it,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(0.5.dp).background(MaterialTheme.colorScheme.primaryContainer,RoundedCornerShape(8.dp)).padding(4.dp)
                )
            }

        }
    }
}

@Composable
fun CalendarDayWeekItem(modifier: Modifier, day: String) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = day)
        }
    }
}
