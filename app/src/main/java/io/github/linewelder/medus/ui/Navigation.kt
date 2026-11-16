package io.github.linewelder.medus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.*
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
    val isAllTasks = destination?.hasRoute<AllTasksRoute>() == true

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (isHome || isAllTasks) {
                NavigationBar {
                    NavigationBarItem(
                        selected = isHome,
                        onClick = {
                            if (!isHome) {
                                navController.popBackStack<HomeRoute>(inclusive = false, saveState = true)
                            }
                        },
                        icon = { Icon(painterResource(R.drawable.ic_home), contentDescription = null) },
                        label = { Text(stringResource(R.string.home)) },
                    )
                    NavigationBarItem(
                        selected = isAllTasks,
                        onClick = {
                            if (!isAllTasks) {
                                navController.navigate(AllTasksRoute) {
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(painterResource(R.drawable.ic_tasks), contentDescription = null) },
                        label = { Text(stringResource(R.string.all_tasks)) },
                    )
                }
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
                    onCreateTask = { navController.navigate(CreateTaskRoute) },
                    onEditTask = { navController.navigate(EditTaskRoute(it.toString())) },
                )
            }
            composable<CreateTaskRoute> {
                CreateTaskScreen(onClose = { navController.popBackStack() })
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
private data object CreateTaskRoute

@Serializable
internal data class EditTaskRoute(val taskId: String)
