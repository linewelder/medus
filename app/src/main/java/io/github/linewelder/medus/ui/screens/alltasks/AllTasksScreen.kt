package io.github.linewelder.medus.ui.screens.alltasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.*
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
import kotlinx.coroutines.*
import kotlin.time.*

@Composable
fun AllTasksScreen(
    onCreateTask: (Id<Area>?) -> Unit,
    onEditTask: (Id<Task>) -> Unit,
    viewModel: AllTasksViewModel = hiltViewModel(),
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = emptyList())
    val areas by viewModel.areas.collectAsStateWithLifecycle(initialValue = null)
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
    areas: List<Area>?,
    onCreateTask: (Id<Area>?) -> Unit,
    onEditTask: (Id<Task>) -> Unit,
    onTaskCompletedChange: (Id<Task>, Boolean) -> Unit,
) {
    var savedOptionId by rememberSaveable { mutableStateOf("") }

    val allAreasOption = HorizontalChooserOption(
        id = "",
        label = stringResource(R.string.all_areas),
    )
    val areaOptions = listOf(allAreasOption) + areas.orEmpty().map { area ->
        HorizontalChooserOption(id = area.id.toString(), label = area.name)
    }
    val pageCount by rememberUpdatedState(areaOptions.size)
    val pagerState = if (areas != null) {
        remember {
            val initialPage = areaOptions.indexOfFirst { it.id == savedOptionId }
            PagerState(currentPage = initialPage.coerceAtLeast(0)) { pageCount }
        }
    } else {
        null
    }
    val selectedOptionIndex = (pagerState?.currentPage ?: 0).coerceAtMost(areaOptions.lastIndex)
    val selectedAreaId = areas?.getOrNull(selectedOptionIndex - 1)?.id

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { onCreateTask(selectedAreaId) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.create_task),
                )
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (areas != null && pagerState != null) {
                val chooserState = rememberLazyListState()
                val scope = rememberCoroutineScope()
                val settledOptionId = areaOptions.getOrNull(pagerState.settledPage)?.id
                LaunchedEffect(settledOptionId) {
                    if (settledOptionId != null) savedOptionId = settledOptionId
                }
                LaunchedEffect(selectedOptionIndex) {
                    chooserState.animateScrollToItem(selectedOptionIndex)
                }

                HorizontalChooser(
                    options = areaOptions,
                    selectedOptionId = areaOptions[selectedOptionIndex].id,
                    onOptionSelected = { optionId ->
                        val page = areaOptions.indexOfFirst { it.id == optionId }
                        scope.launch { pagerState.animateScrollToPage(page) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    state = chooserState,
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    key = { areaOptions[it].id },
                ) { page ->
                    val area = areas.getOrNull(page - 1)
                    val visibleTasks = if (area == null) tasks else tasks.filter { it.areaId == area.id }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
    }
}

@Preview
@Composable
private fun AllTasksScreenPreview() {
    val errands = Area(Id.dummy(1), "Errands")
    val home = Area(Id.dummy(2), "Home")
    val tasks = listOf(
        Task(
            id = Id.dummy(3),
            title = "Pick up groceries",
            dateCreated = Instant.fromEpochMilliseconds(0),
            dateCompleted = null,
            areaId = errands.id,
        ),
        Task(
            id = Id.dummy(4),
            title = "Water the plants",
            dateCreated = Instant.fromEpochMilliseconds(0),
            dateCompleted = Instant.fromEpochMilliseconds(0),
            areaId = home.id,
        ),
    )

    MedusTheme(dynamicColor = false) {
        ConfettiOverlay {
            AllTasksScreenContent(
                tasks = tasks,
                areas = listOf(errands, home),
                onCreateTask = {},
                onEditTask = {},
                onTaskCompletedChange = { _, _ -> },
            )
        }
    }
}
