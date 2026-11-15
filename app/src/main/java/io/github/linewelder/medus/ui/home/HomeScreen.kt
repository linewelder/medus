package io.github.linewelder.medus.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = emptyList())
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(tasks, key = { it.id.value }) { task ->
                Text(task.title)
            }
        }
    }
}
