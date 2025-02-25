package com.sinxn.mytasks.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.sinxn.mytasks.ui.components.CalendarGrid
import com.sinxn.mytasks.ui.components.EventItem
import com.sinxn.mytasks.ui.screens.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    eventViewModel: EventViewModel,
    onAddEventClick: () -> Unit,
    onEventClick: (Long) -> Unit,
    onDayClick: (Long) -> Unit
) {
    val upcomingEvents = eventViewModel.upcomingEvents.collectAsState()
    val eventOnMonth = eventViewModel.eventsOnMonth.collectAsState()

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
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                CalendarGrid(eventOnMonth.value, onDayClick)
            }

            items(upcomingEvents.value) { event ->
                EventItem(event = event, onClick = {
                    event.id?.let { onEventClick(it) }
                })
            }

        }

    }
}

