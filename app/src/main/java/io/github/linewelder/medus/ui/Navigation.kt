package io.github.linewelder.medus.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import io.github.linewelder.medus.ui.createtask.*
import io.github.linewelder.medus.ui.edittask.*
import io.github.linewelder.medus.ui.home.*
import kotlinx.serialization.*

@Composable
fun MedusNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
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

@Serializable
private data object HomeRoute

@Serializable
private data object CreateTaskRoute

@Serializable
internal data class EditTaskRoute(val taskId: String)
