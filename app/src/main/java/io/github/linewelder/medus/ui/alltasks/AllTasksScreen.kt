package io.github.linewelder.medus.ui.alltasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.theme.*
import java.util.*

@Composable
fun AllTasksScreen(
    onCreateTask: () -> Unit,
    onEditTask: (Id<Task>) -> Unit,
    viewModel: AllTasksViewModel = hiltViewModel(),
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = emptyList())
    val areas by viewModel.areas.collectAsStateWithLifecycle(initialValue = emptyList())
    AllTasksScreenContent(
        tasks,
        areas,
        onCreateTask,
        onEditTask,
        onTaskCompletedChange = viewModel::setTaskCompleted,
    )
}

@Composable
private fun AllTasksScreenContent(
    tasks: List<Task>,
    areas: List<Area>,
    onCreateTask: () -> Unit,
    onEditTask: (Id<Task>) -> Unit,
    onTaskCompletedChange: (Id<Task>, Boolean) -> Unit,
) {
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
                        onCompletedChange = { onTaskCompletedChange(task.id, it) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AllTasksScreenPreview() {
    val errands = Area(Id(UUID(0, 1)), "Errands")
    val home = Area(Id(UUID(0, 2)), "Home")
    val tasks = listOf(
        Task(
            id = Id(UUID(0, 3)),
            title = "Pick up groceries",
            dateCreated = Date(0),
            dateCompleted = null,
            areaId = errands.id,
        ),
        Task(
            id = Id(UUID(0, 4)),
            title = "Water the plants",
            dateCreated = Date(0),
            dateCompleted = Date(0),
            areaId = home.id,
        ),
    )

    MedusTheme(dynamicColor = false) {
        AllTasksScreenContent(
            tasks = tasks,
            areas = listOf(errands, home),
            onCreateTask = {},
            onEditTask = {},
            onTaskCompletedChange = { _, _ -> },
        )
    }
}
