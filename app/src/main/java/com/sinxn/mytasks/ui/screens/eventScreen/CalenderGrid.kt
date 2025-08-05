package com.sinxn.mytasks.ui.screens.eventScreen

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.ui.components.RectangleCard
import com.sinxn.mytasks.utils.toMillis
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import kotlin.math.ceil

@Composable
fun CalendarGrid(
    events: List<Event>,
    tasks: List<Task>,
    displayMonth: YearMonth, // The month this grid instance should display
    onClick: (Long) -> Unit,
    // No more onMonthChange needed here, as pager handles month changes
) {
    val WEEK_DAYS = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    val firstDayOfMonth = displayMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.minusDays(firstDayOfMonth.dayOfWeek.value.toLong() % 7)

    val lastDayOfMonth = displayMonth.atEndOfMonth()
    val lastDayOfWeek = lastDayOfMonth.plusDays((6 - (lastDayOfMonth.dayOfWeek.value % 7)).toLong())

    val days = mutableListOf<LocalDate>()
    var tempDate = firstDayOfWeek
    while (!tempDate.isAfter(lastDayOfWeek)) {
        days.add(tempDate)
        tempDate = tempDate.plusDays(1)
    }
    val totalRows = ceil(days.size / 7f).toInt()

    Column(
        Modifier
            .fillMaxSize() // Grid should fill the pager item
            .padding(horizontal = 8.dp), // Add some padding if needed
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Weekday Headers
        Row(Modifier.fillMaxWidth()) {
            for (day in WEEK_DAYS) {
                CalendarDayWeekItem(modifier = Modifier.weight(1f), day = day)
            }
        }
        // Days
        for (x in 0 until totalRows) {
            Row(Modifier.fillMaxWidth()) {
                for (y in 0 until 7) {
                    val dayIndex = x * 7 + y
                    if (dayIndex < days.size) {
                        val targetDay = days[dayIndex]
                        val tasksForDay = tasks
                            .filter { it.due?.toLocalDate() == targetDay }
                            .map { it.title }
                        val eventsForDay = events
                            .filter { it.start?.toLocalDate() == targetDay }
                            .map { it.title }
                        // Day is part of the displayMonth if its YearMonth matches
                        val isCurrentDisplayMonth = YearMonth.from(targetDay) == displayMonth

                        CalendarDayItem(
                            modifier = Modifier.weight(1f),
                            day = targetDay,
                            events = eventsForDay.plus(tasksForDay),
                            isCurrentDisplayMonth = isCurrentDisplayMonth,
                            onClick = {  onClick(it) }
                        )
                    } else {
                        Box(modifier = Modifier
                            .weight(1f)
                            .height(60.dp)) {} // Spacer
                    }
                }
            }
        }
    }
}


@Composable
fun CalendarDayItem(
    modifier: Modifier,
    day: LocalDate,
    events: List<String>,
    isCurrentDisplayMonth: Boolean,
    onClick: (Long) -> Unit = {}
) {
    val dayTextColor = if (isCurrentDisplayMonth) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Standard disabled alpha
    }
    val containerColor = if (day == LocalDate.now() && isCurrentDisplayMonth) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    Card(
        shape = RoundedCornerShape(1.dp),
        modifier = modifier.height(60.dp),
        onClick = {
            if (isCurrentDisplayMonth) { // Only allow click if it's a day of the current month
                onClick(LocalDateTime.of(day, LocalTime.now()).toMillis())
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        enabled = isCurrentDisplayMonth // This will also affect the visual state (e.g. ripple)
    ) {
        Column (
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(),
            verticalArrangement = if(events.isEmpty()) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                style = if(events.isEmpty()) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodySmall,
                color = dayTextColor
            )
            events.forEach {
                Text(
                    text = it,
                    maxLines = 1,
                    color = dayTextColor,
                    fontSize = 8.sp,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(0.5.dp).background(MaterialTheme.colorScheme.primaryContainer,RoundedCornerShape(8.dp)).padding(4.dp)
                )
            }
        }
    }
}


@Composable
fun CalendarDayWeekItem(modifier: Modifier, day: String) {
    RectangleCard (modifier = modifier) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp), // Added a bit more vertical padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = day, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun MonthYearHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround // Or Center
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous Month")
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = currentMonth.month.toString(), lineHeight = 10.sp)
            Text(text = currentMonth.year.toString(), fontSize = 10.sp, lineHeight = 10.sp)
        }
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next Month")
        }
    }
}
