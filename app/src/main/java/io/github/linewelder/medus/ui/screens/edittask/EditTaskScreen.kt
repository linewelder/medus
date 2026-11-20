package io.github.linewelder.medus.ui.screens.edittask

import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.screens.edittask.EditTaskViewModel.*
import io.github.linewelder.medus.ui.theme.*
import kotlin.time.*

/** Show the selected task's editable fields and confirm deletion before applying it. */
@Composable
fun EditTaskScreen(onClose: () -> Unit, viewModel: EditTaskViewModel = hiltViewModel()) {
    val taskState by viewModel.taskState.collectAsStateWithLifecycle()
    val changeState by viewModel.changeState.collectAsStateWithLifecycle()
    val areas by viewModel.areas.collectAsStateWithLifecycle(initialValue = emptyList())
    BackHandler(enabled = changeState.blocksEditing) {}
    LaunchedEffect(changeState) {
        if (changeState == ChangeState.Finished) onClose()
    }

    EditTaskScreenContent(taskState, changeState, areas, onClose, viewModel::saveTask, viewModel::deleteTask)
}

/** Render the edit form and deletion confirmation from the current task state. */
@Composable
private fun EditTaskScreenContent(
    taskState: TaskState,
    changeState: ChangeState,
    areas: List<Area>,
    onClose: () -> Unit,
    onSaveTask: (String, Id<Area>?, Boolean, String?, Int?) -> Unit,
    onDeleteTask: () -> Unit,
) {
    val editing = !changeState.blocksEditing

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.edit_task), style = MaterialTheme.typography.headlineSmall)
            when (val state = taskState) {
                TaskState.Loading -> CircularProgressIndicator()
                TaskState.Unavailable -> {
                    Text(stringResource(R.string.task_unavailable))
                    TextButton(onClick = onClose) { Text(stringResource(R.string.cancel)) }
                }
                is TaskState.Loaded -> {
                    val task = state.task
                    var title by rememberSaveable { mutableStateOf(task.title) }
                    var selectedAreaId by rememberSaveable { mutableStateOf(task.areaId?.toString()) }
                    var creatingArea by rememberSaveable { mutableStateOf(false) }
                    var areaName by rememberSaveable { mutableStateOf("") }
                    var timeEstimateMins by rememberSaveable { mutableStateOf(task.timeEstimateMins) }
                    var completed by rememberSaveable { mutableStateOf(task.dateCompleted != null) }
                    var confirmingDeletion by rememberSaveable { mutableStateOf(false) }
                    val selectedArea = areas.firstOrNull { it.id.toString() == selectedAreaId }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(stringResource(R.string.task_title)) },
                        enabled = editing,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TaskAreaPicker(
                        areas = areas,
                        selectedArea = selectedArea,
                        creatingArea = creatingArea,
                        areaName = areaName,
                        enabled = editing,
                        onSelectArea = {
                            selectedAreaId = it.id.toString()
                            creatingArea = false
                        },
                        onCreateArea = { creatingArea = true },
                        onAreaNameChange = { areaName = it },
                    )
                    TaskTimeEstimatePicker(
                        timeEstimateMins = timeEstimateMins,
                        enabled = editing,
                        onTimeEstimateChange = { timeEstimateMins = it },
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .toggleable(
                                value = completed,
                                enabled = editing,
                                role = Role.Checkbox,
                                onValueChange = { completed = it },
                            ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = completed, onCheckedChange = null, enabled = editing)
                        Text(stringResource(R.string.task_completed))
                    }

                    val errorMessage = when (changeState) {
                        ChangeState.SaveFailed -> R.string.save_task_error
                        ChangeState.DeleteFailed -> R.string.delete_task_error
                        else -> null
                    }
                    if (errorMessage != null) {
                        Text(stringResource(errorMessage), color = MaterialTheme.colorScheme.error)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { confirmingDeletion = true },
                            enabled = editing,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = stringResource(R.string.delete_task),
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = onClose, enabled = editing) {
                            Text(stringResource(R.string.cancel))
                        }
                        val canSave = editing && title.isNotBlank() && (!creatingArea || areaName.isNotBlank())
                        Button(
                            onClick = {
                                onSaveTask(
                                    title,
                                    selectedAreaId?.let { Id.fromString<Area>(it) },
                                    completed,
                                    areaName.takeIf { creatingArea },
                                    timeEstimateMins,
                                )
                            },
                            enabled = canSave,
                        ) {
                            Text(stringResource(R.string.save_task))
                        }
                    }
                    if (confirmingDeletion) {
                        AlertDialog(
                            onDismissRequest = { confirmingDeletion = false },
                            title = { Text(stringResource(R.string.delete_task)) },
                            text = { Text(stringResource(R.string.confirm_delete_task, task.title)) },
                            dismissButton = {
                                TextButton(onClick = { confirmingDeletion = false }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        confirmingDeletion = false
                                        onDeleteTask()
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error,
                                    ),
                                ) {
                                    Text(stringResource(R.string.delete_task))
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EditTaskScreenPreview() {
    val area = Area(Id.empty(), "Errands")
    val task = Task(Id.empty(), "Pick up groceries", Instant.fromEpochMilliseconds(0), null, area.id)
    MedusTheme(dynamicColor = false) {
        EditTaskScreenContent(
            taskState = TaskState.Loaded(task),
            changeState = ChangeState.Editing,
            areas = listOf(area),
            onClose = {},
            onSaveTask = { _, _, _, _, _ -> },
            onDeleteTask = {},
        )
    }
}
