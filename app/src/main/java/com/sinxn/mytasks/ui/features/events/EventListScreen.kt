package com.sinxn.mytasks.ui.features.events

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.mytasks.ui.components.BottomBar
import com.sinxn.mytasks.ui.components.MyTasksTopAppBar
import com.sinxn.mytasks.ui.components.MyTitle
import com.sinxn.mytasks.ui.components.RectangleFAB
import com.sinxn.mytasks.ui.navigation.Routes.Event
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    eventViewModel: EventViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState by eventViewModel.uiState.collectAsState()

    // Define a very large range for "infinite" swiping.
    // Pager works with indices. We'll map these indices to YearMonth.
    val pageCount = Int.MAX_VALUE // Effectively "infinite"
    val initialPage = pageCount / 2 // Start in the middle
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pageCount }
    )

    // State to hold the YearMonth derived from the current pager page
    // Update currentDisplayMonth when the pager's currentPage changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .map { pageIndex ->
                // Calculate month based on page index relative to the initial month
                val monthOffset = pageIndex - initialPage
                YearMonth.now().plusMonths(monthOffset.toLong())
            }
            .distinctUntilChanged()
            .collect { month ->
                eventViewModel.onAction(AddEditEventAction.OnMonthChange(month))
            }
    }
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = {
            MyTasksTopAppBar(
                title = { Text(text = "Events") }
            )
        },
        floatingActionButton = {
            RectangleFAB(onClick = { navController.navigate(Event.Add.byFolder(0)) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { paddingValues ->
        val events = uiState.eventListItems
        val tasks = uiState.taskListItems
        LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxWidth()) {
            item {
                MonthYearHeader(
                    currentMonth = uiState.month,
                    onPreviousMonth = {
                        // Animate to previous page
                        // This requires a coroutine scope
                        // rememberCoroutineScope().launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        // For simplicity now, direct change, but animation is better
                        eventViewModel.onAction(AddEditEventAction.OnMonthChange(uiState.month.minusMonths(1)))
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        // You would need to calculate the target page and scroll the pager
                    },
                    onNextMonth = {
                        eventViewModel.onAction(AddEditEventAction.OnMonthChange(uiState.month.plusMonths(1)))
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        // You would need to calculate the target page and scroll the pager
                    }
                )

                HorizontalPager(
                    state = pagerState,
                    //modifier = Modifier.weight(1f) // Ensure Pager takes available space
                ) { pageIndex ->
                    // Calculate the YearMonth for the current page
                    val monthOffset = pageIndex - initialPage
                    val pageMonth = YearMonth.now().plusMonths(monthOffset.toLong())



                    // Pass this specific month to your CalendarGrid
                    // CalendarGrid will now be responsible for rendering only ONE month
                    CalendarGrid(
                        tasks = uiState.taskOnMonth,
                        events = uiState.eventsOnMonth,
                        displayMonth = pageMonth, // Pass the month this grid should display
                        onClick = { navController.navigate(Event.Add.byDate(it)) }
                    )
                }
                MyTitle(text = "Events on this month")
            }

            items(uiState.eventsOnMonth) { event ->
                EventSmallItem(event = event, modifier = Modifier.animateItem(), onClick = {
                    event.id.let { navController.navigate(Event.get(it)) }
                })
            }
        }
    }
}

