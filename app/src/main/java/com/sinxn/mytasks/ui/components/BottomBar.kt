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

sealed class BottomNavItem(val route: String, @DrawableRes val icon: Int, val label: String) {
    data object Home : BottomNavItem("home", R.drawable.home_ic, "Home")
    data object Calender : BottomNavItem("event_list", R.drawable.event_ic, "Calender")
    data object Tasks : BottomNavItem("tasks", R.drawable.task_ic, "Tasks")
    data object Notes : BottomNavItem("note_list", R.drawable.note_ic, "Notes")
}

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Calender,
        BottomNavItem.Tasks,
        BottomNavItem.Notes,
    )
    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(item.icon) , contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
