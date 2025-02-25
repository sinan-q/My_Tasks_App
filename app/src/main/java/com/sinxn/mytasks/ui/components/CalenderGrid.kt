package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.ui.screens.toMillis
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.ceil

@Composable
fun CalendarGrid(events: List<Event>, onClick: (Long) -> Unit) {
    val currentDate = LocalDate.now()

    val firstDayOfMonth = currentDate.withDayOfMonth(1)
    val firstDay = firstDayOfMonth.minusDays(firstDayOfMonth.dayOfWeek.value.toLong())
    val lastDayOfMonth = firstDayOfMonth.plusMonths(1)
    val days = firstDay.datesUntil(lastDayOfMonth).toList()
    val totalColumns = ceil(days.size / 7f).toInt()
    Column {
        for (x in 0 until totalColumns) {
            Row(Modifier.fillMaxWidth()) {
                for (y in x*7 until x*7+7) {
                    if (y >= days.size) break
                    CalendarDayItem(day = days[y], events = events.filter {
                        val eventDate = it.start
                        eventDate?.dayOfYear == days[y].dayOfYear
                    }, onClick = {
                        onClick(it)
                    })
                }

            }
        }

    }
}

@Composable
fun CalendarDayItem(day: LocalDate, events: List<Event>, onClick: (Long) -> Unit = {}) {
    Card(
        modifier = Modifier
            .height(80.dp)
            .width(50.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
            .clickable { onClick(LocalDateTime.of(day, LocalTime.now()).toMillis()) },
        colors = CardDefaults.cardColors(
            containerColor = if (day == LocalDate.now()) Color.LightGray else Color.Transparent
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = day.dayOfMonth.toString())
            if (events.isNotEmpty()) {
                events.forEach {
                    Text(text = it.title, fontSize = MaterialTheme.typography.labelSmall.fontSize)
                }
            }
        }
    }
}