package io.github.linewelder.medus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import io.github.linewelder.medus.R
import io.github.linewelder.medus.ui.screens.alltasks.*
import io.github.linewelder.medus.ui.screens.createtask.*
import io.github.linewelder.medus.ui.screens.edittask.*
import io.github.linewelder.medus.ui.screens.home.*
import kotlinx.serialization.*

@Composable
fun MedusNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val isHome = destination?.hasRoute<HomeRoute>() == true
    val isTasks = destination?.hasRoute<AllTasksRoute>() == true
    val isMenu = destination?.hasRoute<MenuRoute>() == true

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (!isHome && !isTasks && !isMenu) return@Scaffold

            NavigationBar {
                NavigationBarItem(
                    selected = isHome,
                    onClick = {
                        if (isHome) return@NavigationBarItem
                        navController.popBackStack<HomeRoute>(inclusive = false, saveState = true)
                    },
                    icon = { Icon(painterResource(R.drawable.ic_home), contentDescription = null) },
                    label = { Text(stringResource(R.string.home)) },
                )
                NavigationBarItem(
                    selected = isTasks,
                    onClick = {
                        if (isTasks) return@NavigationBarItem
                        navController.navigate(AllTasksRoute) {
                            popUpTo<HomeRoute> { saveState = true }
                            restoreState = true
                        }
                    },
                    icon = { Icon(painterResource(R.drawable.ic_tasks), contentDescription = null) },
                    label = { Text(stringResource(R.string.tasks)) },
                )
                NavigationBarItem(
                    selected = isMenu,
                    onClick = {
                        if (isMenu) return@NavigationBarItem
                        navController.navigate(MenuRoute) {
                            popUpTo<HomeRoute> { saveState = true }
                            restoreState = true
                        }
                    },
                    icon = { Icon(painterResource(R.drawable.ic_menu), contentDescription = null) },
                    label = { Text(stringResource(R.string.menu)) },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
        ) {
            composable<HomeRoute> {
                HomeScreen()
            }
            composable<AllTasksRoute> {
                AllTasksScreen(
                    onCreateTask = { navController.navigate(CreateTaskRoute(it?.toString())) },
                    onEditTask = { navController.navigate(EditTaskRoute(it.toString())) },
                )
            }
            composable<MenuRoute> {}
            composable<CreateTaskRoute> { entry ->
                CreateTaskScreen(
                    onClose = { navController.popBackStack() },
                    initialAreaId = entry.toRoute<CreateTaskRoute>().areaId,
                )
            }
            composable<EditTaskRoute> {
                EditTaskScreen(onClose = { navController.popBackStack() })
            }
        }
    }
}

@Serializable
private data object HomeRoute

@Serializable
private data object AllTasksRoute

@Serializable
private data object MenuRoute

@Serializable
private data class CreateTaskRoute(val areaId: String? = null)

@Serializable
internal data class EditTaskRoute(val taskId: String)
