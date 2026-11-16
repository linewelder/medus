package io.github.linewelder.medus.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.components.*

@Composable
fun HomeScreen(
    onCreateTask: () -> Unit,
    onEditTask: (Id<Task>) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = emptyList())
    val areas by viewModel.areas.collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedAreaId by rememberSaveable { mutableStateOf<String?>(null) }
    val visibleTasks = if (selectedAreaId == null) {
        tasks
    } else {
        tasks.filter { it.areaId?.toString() == selectedAreaId }
    }

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
        Column(modifier = Modifier.padding(innerPadding)) {
            val allAreasOption = HorizontalChooserOption(
                id = "",
                label = stringResource(R.string.all_areas),
            )
            val areaOptions = listOf(allAreasOption) + areas.map { area ->
                HorizontalChooserOption(id = area.id.toString(), label = area.name)
            }
            HorizontalChooser(
                options = areaOptions,
                selectedOptionId = selectedAreaId ?: allAreasOption.id,
                onOptionSelected = { optionId ->
                    selectedAreaId = optionId.takeUnless { it == allAreasOption.id }
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 88.dp),
            ) {
                items(visibleTasks, key = { it.id.value }) { task ->
                    TaskListItem(
                        task = task,
                        onEdit = { onEditTask(task.id) },
                        onCompletedChange = { viewModel.setTaskCompleted(task.id, it) },
                    )
                }
            }
        }
    }
}
