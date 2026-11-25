package io.github.linewelder.medus.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hasRoute
import io.github.linewelder.medus.R

@Composable
internal fun MedusBottomBar(
    selectedTab: BottomBarTab,
    onClick: (BottomBarTab) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == BottomBarTab.Home,
            onClick = { onClick(BottomBarTab.Home) },
            icon = { Icon(painterResource(R.drawable.ic_home), contentDescription = null) },
            label = { Text(stringResource(R.string.home)) },
        )
        NavigationBarItem(
            selected = selectedTab == BottomBarTab.Tasks,
            onClick = { onClick(BottomBarTab.Tasks) },
            icon = { Icon(painterResource(R.drawable.ic_tasks), contentDescription = null) },
            label = { Text(stringResource(R.string.tasks)) },
        )
        NavigationBarItem(
            selected = selectedTab == BottomBarTab.Menu,
            onClick = { onClick(BottomBarTab.Menu) },
            icon = { Icon(painterResource(R.drawable.ic_menu), contentDescription = null) },
            label = { Text(stringResource(R.string.menu)) },
        )
    }
}

fun getTabFromRoute(destination: NavDestination?): BottomBarTab? = when {
    destination?.hasRoute<HomeRoute>() == true -> BottomBarTab.Home
    destination?.hasRoute<AllTasksRoute>() == true -> BottomBarTab.Tasks
    destination?.hasRoute<MenuRoute>() == true -> BottomBarTab.Menu
    else -> null
}

fun NavController.navigateToTab(tab: BottomBarTab) = when (tab) {
    BottomBarTab.Home ->
        popBackStack<HomeRoute>(inclusive = false, saveState = true)
    BottomBarTab.Tasks ->
        navigate(AllTasksRoute) {
            popUpTo<HomeRoute> { saveState = true }
            restoreState = true
        }
    BottomBarTab.Menu ->
        navigate(MenuRoute) {
            popUpTo<HomeRoute> { saveState = true }
            restoreState = true
        }
}

enum class BottomBarTab {
    Home,
    Tasks,
    Menu,
}
