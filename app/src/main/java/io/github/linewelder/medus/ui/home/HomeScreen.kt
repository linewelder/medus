package io.github.linewelder.medus.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R

@Composable
fun HomeScreen(onCreateTask: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = emptyList())
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTask) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.create_task),
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(tasks, key = { it.id.value }) { task ->
                Text(task.title)
            }
        }
    }
}
