package io.github.linewelder.medus.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.navigation.compose.*
import io.github.linewelder.medus.ui.*
import io.github.linewelder.medus.ui.screens.alltasks.*
import io.github.linewelder.medus.ui.screens.createtask.*
import io.github.linewelder.medus.ui.screens.editchecklist.*
import io.github.linewelder.medus.ui.screens.edittask.*
import io.github.linewelder.medus.ui.screens.home.*
import io.github.linewelder.medus.ui.screens.menu.*

@Composable
fun MedusNavigation(actionHistory: ActionHistory) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val selectedTab = getTabFromRoute(destination)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            ActionHistorySnackbarHost(
                actionHistory,
                modifier = if (selectedTab != null) Modifier else Modifier.navigationBarsPadding(),
            )
        },
        bottomBar = {
            if (selectedTab == null) return@Scaffold

            MedusBottomBar(
                selectedTab = selectedTab,
                onClick = { tab ->
                    if (tab == selectedTab) return@MedusBottomBar
                    navController.navigateToTab(tab)
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    onNewTask = { navController.navigate(CreateTaskRoute(areaId = null)) },
                )
            }
            composable<AllTasksRoute> {
                AllTasksScreen(
                    onCreateTask = { navController.navigate(CreateTaskRoute(it?.toString())) },
                    onEditTask = { navController.navigate(EditTaskRoute(it.toString())) },
                )
            }
            composable<MenuRoute> {
                MenuScreen(
                    onEditChecklist = { navController.navigate(EditChecklistRoute(it.toString())) },
                )
            }
            composable<EditChecklistRoute> {
                EditChecklistScreen()
            }
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

