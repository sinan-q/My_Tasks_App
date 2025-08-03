package com.sinxn.mytasks.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sinxn.mytasks.R
import com.sinxn.mytasks.ui.navigation.Routes

sealed class BottomNavItem(val screen: Routes, @DrawableRes val icon: Int) {
    data object Home : BottomNavItem(Routes.Home, R.drawable.home_ic)
    data object Calender : BottomNavItem(Routes.Event, R.drawable.event_ic)
    data object Tasks : BottomNavItem(Routes.Task, R.drawable.task_ic)
    data object Notes : BottomNavItem(Routes.Note, R.drawable.note_ic)
}

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Calender,
        BottomNavItem.Tasks,
        BottomNavItem.Notes,
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    if (currentRoute in listOf(Routes.Home.route, Routes.Folder.route, Routes.Event.route, Routes.Task.route, Routes.Note.route)) {
        NavigationBar {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(item.icon),
                            contentDescription = item.screen.name
                        )
                    },
                    label = { Text(item.screen.name ?: "Label Missing") },
                    selected = currentRoute == item.screen.route,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
