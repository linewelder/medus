package io.github.linewelder.medus.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import io.github.linewelder.medus.ui.home.*
import kotlinx.serialization.*

@Composable
fun MedusNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen()
        }
    }
}

@Serializable
private data object HomeRoute
