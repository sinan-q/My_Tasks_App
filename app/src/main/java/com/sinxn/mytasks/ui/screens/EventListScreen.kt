package com.sinxn.mytasks.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.ui.screens.viewmodel.EventViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    eventViewModel: EventViewModel,
    onAddEventClick: () -> Unit,
    onEventClick: (Event) -> Unit
) {
    val events = eventViewModel.events.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Event List") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddEventClick() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            CalendarGrid(events = events.value)
            LazyColumn {
                items(events.value) {
                    Text(text = it.title)
                }
            }
        }

    }
}

@Composable
fun CalendarGrid(events: List<Event>) {
    val currentDate = LocalDate.now()

    val firstDayOfMonth = currentDate.withDayOfMonth(1)
    val firstDay = firstDayOfMonth.minusDays(firstDayOfMonth.dayOfWeek.value.toLong())
    val lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7), // 7 days in a week
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
    ) {
        items(firstDay.datesUntil(lastDayOfMonth).toList()
        ) { day ->
            CalendarDayItem(day = day.dayOfMonth, events = events.filter {
                val eventDate = LocalDate.ofInstant(it.start?.toInstant(), java.time.ZoneId.systemDefault())
                eventDate.isEqual(day)})
        }
    }
}

@Composable
fun CalendarDayItem(day: Int, events: List<Event>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape),
        colors = CardDefaults.cardColors(
            containerColor = if (day == LocalDate.now().dayOfMonth) Color.LightGray else Color.Transparent
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = day.toString())
            if (events.isNotEmpty()) {
                events.forEach {
                    Text(text = it.title, fontSize = MaterialTheme.typography.labelSmall.fontSize)
                }
            }
        }
    }
}