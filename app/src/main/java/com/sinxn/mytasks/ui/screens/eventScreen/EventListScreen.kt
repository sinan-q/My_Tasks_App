package com.sinxn.mytasks.ui.screens.eventScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.sinxn.mytasks.ui.components.CalendarGrid
import com.sinxn.mytasks.ui.components.MyTitle
import com.sinxn.mytasks.ui.components.RectangleFAB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    eventViewModel: EventViewModel,
    onAddEventClick: (folderId: Long) -> Unit,
    onEventClick: (Long) -> Unit,
    onDayClick: (Long) -> Unit
) {
    val upcomingEvents = eventViewModel.upcomingEvents.collectAsState()
    val eventOnMonth = eventViewModel.eventsOnMonth.collectAsState()
    val taskOnMonth = eventViewModel.tasksOnMonth.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Event List") }
            )
        },
        floatingActionButton = {
            RectangleFAB(onClick = { onAddEventClick( 0L) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxWidth()) {
            item {
                CalendarGrid(eventOnMonth.value, taskOnMonth.value, onDayClick)
                MyTitle(title = "Upcoming Events")
            }

            items(upcomingEvents.value) { event ->
                EventItem(event = event, onClick = {
                    event.id?.let { onEventClick(it) }
                })
            }

        }

    }
}

